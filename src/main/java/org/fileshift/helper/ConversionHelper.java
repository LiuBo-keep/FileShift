package org.fileshift.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;
import org.fileshift.common.constant.ConversionStartInfo;
import org.fileshift.common.enums.ConversionMode;
import org.fileshift.components.CommonComponent;
import org.fileshift.components.NotificationComponent;
import org.fileshift.model.ConvertTask;
import org.fileshift.service.ImageToPdfConverter;
import org.fileshift.service.OfficeToPdfConverter;
import org.fileshift.service.PdfToWordConverter;

/**
 * <p>
 * 文件转换助手类，负责管理图像和 Office 文件的批量转换任务。
 * </p>
 * <p>
 * 功能：
 * <ul>
 *   <li>维护图像任务列表和 Office 文件任务列表</li>
 *   <li>执行文件批量转换（图像转 PDF / Office 转 PDF）</li>
 *   <li>更新转换进度条和任务状态</li>
 *   <li>显示转换完成或失败的通知</li>
 *   <li>管理后台线程池，支持多任务并行处理</li>
 * </ul>
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>依赖 {@link NotificationComponent} 显示通知</li>
 *   <li>依赖 {@link ImageToPdfConverter} 和 {@link OfficeToPdfConverter} 执行具体文件转换</li>
 *   <li>线程池使用虚拟线程（Virtual Threads），确保并行任务高效执行</li>
 * </ul>
 * </p>
 *
 * @author aidan
 * @version 1.0
 * @since 2026/2/10
 */
public class ConversionHelper {
  private final Button btnStart;
  private final ProgressBar globalProgress;
  private final NotificationComponent notificationComponent;

  private final CommonComponent commonComponent;
  private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();


  /**
   * 构造器，初始化转换助手。
   *
   * @param btnStart              开始转换按钮
   * @param globalProgress        全局进度条
   * @param notificationComponent 通知组件
   */
  public ConversionHelper(Button btnStart, ProgressBar globalProgress, CommonComponent commonComponent,
                          NotificationComponent notificationComponent) {
    this.globalProgress = globalProgress;
    this.btnStart = btnStart;
    this.commonComponent = commonComponent;
    this.notificationComponent = notificationComponent;
  }

  /**
   * 初始化转换助手。
   * <p>
   * 绑定开始转换按钮点击事件，并设置初始状态。
   * </p>
   */
  public void init() {
    btnStart.setOnAction(e -> startConversion());
  }

  /**
   * 启动批量转换任务。
   * <p>
   * 根据 {@code mode} 确定执行图像转 PDF 还是 Office 转 PDF。
   * 任务执行期间：
   * <ul>
   *   <li>更新每个任务状态</li>
   *   <li>更新全局进度条</li>
   *   <li>任务完成后显示通知</li>
   * </ul>
   * </p>
   */
  private void startConversion() {
    ConversionMode mode = commonComponent.conversionMode();
    ObservableList<ConvertTask> activeTasks = commonComponent.getTargetList();

    System.out.println("start converter ....");
    if (activeTasks.isEmpty()) {
      System.out.println("not file....");
      return;
    }

    // 先解绑，避免绑定冲突
    globalProgress.progressProperty().unbind();

    btnStart.setDisable(true);
    globalProgress.setProgress(0);

    Task<Void> batch = new Task<>() {
      @Override
      protected Void call() {
        int total = activeTasks.size();
        int done = 0;

        for (ConvertTask task : activeTasks) {
          if (isCancelled()) {
            break;
          }
          task.setStatus(ConversionStartInfo.CONVERTING);
          updateProgress(done, total);

          boolean success = false;
          try {
            success = switch (mode) {
              case IMAGE_TO_PDF -> ImageToPdfConverter.convert(task.getFile(), task, commonComponent);
              case OFFICE_TO_PDF -> OfficeToPdfConverter.convert(task.getFile(), task, commonComponent);
              case PDF_TO_WORD -> PdfToWordConverter.convert(task.getFile(), task, commonComponent);
            };
            task.setStatus(success ? ConversionStartInfo.CONVERT_SUCCESS : ConversionStartInfo.CONVERT_FAIL);
          } catch (Exception ex) {
            task.setStatus("错误: " + ex.getMessage());
            ex.printStackTrace();
          }

          done++;
          updateProgress(done, total);
        }
        return null;
      }

      @Override
      protected void succeeded() {
        btnStart.setDisable(false);
        // 完成时先显示 1.0，0.5 秒后重置为 0
        globalProgress.progressProperty().unbind();
        globalProgress.setProgress(1.0);

        Platform.runLater(() -> {
          PauseTransition pause = new PauseTransition(Duration.millis(500));
          pause.setOnFinished(e -> globalProgress.setProgress(0));
          pause.play();

          notificationComponent.showNotification(ConversionStartInfo.CONVERT_COMPLETED, true);
        });
      }

      @Override
      protected void failed() {
        btnStart.setDisable(false);
        globalProgress.progressProperty().unbind();
        globalProgress.setProgress(0);

        Platform.runLater(() -> {
          notificationComponent.showNotification(ConversionStartInfo.CONVERT_FAILED, false);
        });
      }
    };

    globalProgress.progressProperty().bind(batch.progressProperty());
    executor.submit(batch);
  }

  /**
   * 安全关闭线程池。
   * <p>
   * 尝试等待 5 秒让现有任务完成，超时后强制关闭。
   * </p>
   */
  public void shutdownExecutor() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
    System.out.println("线程池已关闭");
  }

  /**
   * 强制关闭线程池。
   * <p>
   * 尝试立即关闭线程池，并返回未处理的任务列表。
   * </p>
   *
   * @return 未处理的任务列表
   */
  public List<Runnable> shutdownNow() {
    if (executor.isShutdown()) {
      return new ArrayList<>();
    }
    return executor.shutdownNow();
  }
}
