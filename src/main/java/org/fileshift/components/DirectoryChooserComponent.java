package org.fileshift.components;

import java.io.File;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;

/**
 * 目录选择组件
 *
 * @author aidan.liu
 * @version 1.0
 * @since 2026/2/11 14:20
 */
public class DirectoryChooserComponent {

  private final Label lblOutputDir;
  private final BorderPane rootPane;
  private final Button btnChooseOutput;
  private final CommonComponent commonComponent;
  private final NotificationComponent notificationComponent;

  public DirectoryChooserComponent(
      BorderPane rootPane, Label lblOutputDir, CommonComponent commonComponent,
      NotificationComponent notificationComponent, Button btnChooseOutput) {
    this.rootPane = rootPane;
    this.lblOutputDir = lblOutputDir;
    this.commonComponent = commonComponent;
    this.notificationComponent = notificationComponent;
    this.btnChooseOutput = btnChooseOutput;
  }

  /**
   * 初始化
   */
  public void init() {
    btnChooseOutput.setOnAction(e -> directoryChooser());
  }

  /**
   * 显示目录选择对话框
   */
  private void directoryChooser() {
    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("选择 PDF 输出文件夹");
    // 默认打开上次目录（如果有）
    File lastDir = commonComponent.getOutputDirectory();
    if (lastDir != null && lastDir.isDirectory()) {
      chooser.setInitialDirectory(lastDir);
    }

    File selected = chooser.showDialog(rootPane.getScene().getWindow());
    if (selected != null) {
      commonComponent.setOutputDirectory(selected);
      notificationComponent.showNotification("输出目录已设置为：" + selected.getName(), true);

      // 可选：更新界面显示
      if (lblOutputDir != null) {
        lblOutputDir.setText("输出：" + commonComponent.getOutputDirDisplayName());
      }
    }
  }
}
