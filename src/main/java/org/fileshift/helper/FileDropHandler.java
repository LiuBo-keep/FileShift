package org.fileshift.helper;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import javafx.collections.ObservableList;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import org.fileshift.components.CommonComponent;
import org.fileshift.model.ConvertTask;

/**
 * <p>
 * 文件拖拽处理器，用于支持将文件拖入主界面并自动添加到转换任务列表。
 * </p>
 * <p>
 * 功能：
 * <ul>
 *   <li>监听 {@link BorderPane} 的拖拽事件</li>
 *   <li>自动识别图像文件或 Office 文件，并添加到 {@link ConversionHelper} 的对应任务列表</li>
 *   <li>拖拽时改变界面背景颜色以提供视觉反馈</li>
 * </ul>
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>依赖 {@link ConversionHelper} 提供任务列表和文件类型判断方法</li>
 *   <li>只能在主界面 {@link BorderPane} 已正确注入时使用</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * FileDropHandler dropHandler = new FileDropHandler(rootPane, conversionHelper);
 * dropHandler.setupDragAndDrop();
 * </pre>
 * </p>
 *
 * @author aidan
 * @version 1.0
 * @since 2026/2/10
 */
public class FileDropHandler {

  private final BorderPane rootPane;
  private final CommonComponent commonComponent;


  /**
   * 构造器，初始化文件拖拽处理器并设置拖拽事件。
   *
   * @param rootPane          主界面根节点
   * @param conversionHandler 转换助手实例
   */
  public FileDropHandler(BorderPane rootPane, CommonComponent commonComponent) {
    this.rootPane = rootPane;
    this.commonComponent = commonComponent;
    setupDragAndDrop();
  }

  /**
   * 设置拖拽事件监听。
   * <p>
   * 包括：
   * <ul>
   *   <li>拖拽经过（DragOver）事件：判断是否为文件并接受 COPY 传输模式，同时改变背景颜色</li>
   *   <li>拖拽离开（DragExited）事件：恢复背景颜色</li>
   *   <li>文件放下（DragDropped）事件：
   *     <ul>
   *       <li>判断文件类型（图像或 Office）</li>
   *       <li>添加到对应的转换任务列表</li>
   *       <li>更新界面提示和日志信息</li>
   *     </ul>
   *   </li>
   * </ul>
   * </p>
   * <p>
   * 如果 {@code rootPane} 未注入，将打印错误信息并跳过设置。
   * </p>
   */
  public void setupDragAndDrop() {
    if (rootPane == null) {
      System.err.println("rootPane 未注入，拖拽无法设置");
      return;
    }

    rootPane.setOnDragOver(event -> {
      System.out.println("DragOver 触发 - root 级别");
      Dragboard db = event.getDragboard();
      if (db.hasFiles()) {
        event.acceptTransferModes(TransferMode.COPY);
        event.consume();
        rootPane.setStyle("-fx-background-color: rgba(227, 242, 253, 0.6);");
      }
    });

    rootPane.setOnDragExited(event -> rootPane.setStyle(""));

    rootPane.setOnDragDropped(event -> {
      System.out.println("DragDropped 触发 - root 级别");
      Dragboard db = event.getDragboard();
      boolean success = false;
      if (db.hasFiles()) {
        List<File> files = db.getFiles();
        int added = 0;

        // 获取目标列表
        ObservableList<ConvertTask> targetList = commonComponent.getTargetList();
        // 获取目标格式
        String targetFormat = commonComponent.targetFormat();
        // 获取文件过滤器
        Predicate<File> filter = commonComponent.getFileFilter();

        for (File file : files) {
          if (filter.test(file)) {
            targetList.add(new ConvertTask(file, targetFormat));
            added++;
          }
        }
        success = added > 0;
        if (success) {
          System.out.println("成功添加 " + added + " 个文件");
        }
      }
      event.setDropCompleted(success);
      event.consume();
      rootPane.setStyle("");
    });
  }
}
