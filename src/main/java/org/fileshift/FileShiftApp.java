package org.fileshift;

import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.fileshift.controller.MainController;

/**
 * <p>
 * 应用程序启动器类，负责初始化 JavaFX 界面、设置主窗口属性以及处理应用退出逻辑。
 * 这是整个 FileShift 文件转换工具的入口类。
 * </p>
 * <p>
 * 功能包括：
 * <ul>
 *   <li>加载 FXML 界面并绑定控制器</li>
 *   <li>设置主窗口标题和图标</li>
 *   <li>处理拖拽文件功能初始化</li>
 *   <li>监听窗口关闭事件，确保线程池正确关闭</li>
 * </ul>
 * </p>
 *
 * @author aidan
 * @version 1.0
 * @since 2026/2/9
 */
public class FileShiftApp extends Application {

  private MainController controller;

  /**
   * 应用程序主入口方法。
   * <p>
   * 调用 {@link #launch(String...)} 启动 JavaFX 应用。
   * </p>
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * JavaFX 应用启动时调用的方法。
   * <p>
   * 该方法完成以下步骤：
   * <ol>
   *   <li>加载 FXML 文件并创建 Scene</li>
   *   <li>获取控制器对象并初始化拖拽功能</li>
   *   <li>设置主窗口标题和图标</li>
   *   <li>注册窗口关闭事件监听器</li>
   *   <li>显示窗口</li>
   * </ol>
   * </p>
   *
   * @param stage JavaFX 主舞台，由框架提供
   * @throws Exception 如果 FXML 加载失败或资源缺失，将抛出异常
   */
  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(FileShiftApp.class.getResource("/fxml/main.fxml"));
    Scene scene = new Scene(loader.load(), 1200, 800);

    // 关键：FXML 加载完成后，获取控制器并调用拖拽设置
    controller = loader.getController();
    controller.setupDragAndDrop();
    controller.applyTheme(scene);

    stage.setTitle("FileShift - 文件转换工具");
    stage.setScene(scene);
    // 设置图标
    stage.getIcons()
        .add(new Image(Objects.requireNonNull(FileShiftApp.class.getResourceAsStream("/icon/app-icon.jpeg"))));

    // 关键：监听窗口关闭事件，确保彻底退出
    stage.setOnCloseRequest(this::handleCloseRequest);

    stage.show();
  }

  /**
   * 处理窗口关闭请求。
   * <p>
   * 当用户尝试关闭主窗口时，首先检查是否有后台任务仍在执行。
   * 如果有任务，则弹出确认对话框，允许用户选择是否强制退出。
   * </p>
   *
   * @param event 窗口关闭事件对象
   */
  private void handleCloseRequest(WindowEvent event) {
    // 检查线程池是否已终止（所有任务完成）
    if (!controller.isExecutorTerminated()) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("确认退出");
      alert.setHeaderText("有任务正在进行");
      alert.setContentText("是否强制退出？未完成的任务将被中断。");

      // 等待用户选择
      alert.showAndWait().ifPresent(response -> {
        if (response != ButtonType.OK) {
          // 用户取消 → 阻止窗口关闭
          event.consume();
        }
      });

      // 如果用户没有确认（或对话框被关闭），也阻止关闭
      if (!alert.getResult().equals(ButtonType.OK)) {
        event.consume();
        return;
      }
    }

    // 关闭线程池（停止所有转换任务）
    controller.shutdownExecutor();
    // 强制退出 JVM（可选，但推荐）
    Platform.exit();
    System.exit(0);
  }

  /**
   * 应用程序退出时调用的方法。
   * <p>
   * 在此方法中执行额外的资源清理，例如关闭线程池。
   * 注意：此方法在窗口关闭后仍会被调用，因此需保证线程池已安全关闭。
   * </p>
   *
   * @throws Exception 如果清理过程中发生异常，将向上抛出
   */
  @Override
  public void stop() throws Exception {
    super.stop();
    // 额外清理
    if (controller != null) {
      controller.shutdownExecutor();
    }
    Platform.exit();
    System.exit(0);
  }
}
