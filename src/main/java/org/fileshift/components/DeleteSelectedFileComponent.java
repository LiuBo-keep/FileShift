package org.fileshift.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import org.fileshift.common.constant.ConversionStartInfo;
import org.fileshift.model.ConvertTask;

/**
 * @author aidan.liu
 * @version 1.0
 * @since 2026/2/12 13:55
 */
public class DeleteSelectedFileComponent {

  private final Button btnDelete;
  private final RadioButton rbPdfToWord;
  private final RadioButton rbOfficeToPdf;
  private final RadioButton rbImageToPdf;
  private final ToggleGroup functionGroup;
  private final TableView<ConvertTask> tableFiles;
  private final TableView<ConvertTask> tableOffice;
  private final TableView<ConvertTask> tablePdfToWord;
  private final NotificationComponent notificationComponent;

  public DeleteSelectedFileComponent(
      Button btnDelete,
      RadioButton rbImageToPdf,
      ToggleGroup functionGroup,
      RadioButton rbOfficeToPdf,
      RadioButton rbPdfToWord,
      TableView<ConvertTask> tableFiles,
      TableView<ConvertTask> tableOffice,
      TableView<ConvertTask> tablePdfToWord,
      NotificationComponent notificationComponent
  ) {
    this.btnDelete = btnDelete;
    this.rbImageToPdf = rbImageToPdf;
    this.functionGroup = functionGroup;
    this.rbOfficeToPdf = rbOfficeToPdf;
    this.tableFiles = tableFiles;
    this.tableOffice = tableOffice;
    this.notificationComponent = notificationComponent;
    this.tablePdfToWord = tablePdfToWord;
    this.rbPdfToWord = rbPdfToWord;
  }


  /**
   * 初始化
   */
  public void init() {
    setupDeleteButtonListener();
    btnDelete.setOnAction(event -> handleDeleteSelected());
  }

  private void setupDeleteButtonListener() {
    // 因为有两个 TableView，根据当前模式监听对应的一个
    functionGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
      updateDeleteButtonState();
    });

    // 监听图像 TableView 的选中变化
    tableFiles.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
      if (rbImageToPdf.isSelected()) {
        updateDeleteButtonState();
      }
    });

    // 监听 Office TableView 的选中变化
    tableOffice.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
      if (rbOfficeToPdf.isSelected()) {
        updateDeleteButtonState();
      }
    });

    // 监听 PDF To Word TableView 的选中变化
    tablePdfToWord.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
      if (rbPdfToWord.isSelected()) {
        updateDeleteButtonState();
      }
    });

    // 初始状态
    updateDeleteButtonState();
  }

  private void handleDeleteSelected() {
    TableView<ConvertTask> currentTable =
        rbImageToPdf.isSelected() ? tableFiles : (rbOfficeToPdf.isSelected() ? tableOffice : tablePdfToWord);

    // 获取选中的项（使用 ArrayList 避免并发修改异常）
    ObservableList<ConvertTask> selectedItems = FXCollections.observableArrayList(
        currentTable.getSelectionModel().getSelectedItems()
    );

    if (selectedItems.isEmpty()) {
      return; // 虽然按钮已禁用，但多一层防护
    }

    // 检查是否有正在转换中的任务（状态为 "转换中..."）
    boolean hasRunning = selectedItems.stream()
        .anyMatch(task -> ConversionStartInfo.CONVERTING.equals(task.statusProperty().get()));

    if (hasRunning) {
      notificationComponent.showNotification(
          "无法删除正在转换中的文件",
          false
      );
      return;
    }

    // 执行删除
    currentTable.getItems().removeAll(selectedItems);

    notificationComponent.showNotification(
        "已删除 " + selectedItems.size() + " 个文件",
        true
    );

    // 更新删除按钮可用状态
    updateDeleteButtonState();
  }

  private void updateDeleteButtonState() {
    TableView<ConvertTask> currentTable =
        rbImageToPdf.isSelected() ? tableFiles : (rbOfficeToPdf.isSelected() ? tableOffice : tablePdfToWord);
    btnDelete.setDisable(currentTable.getSelectionModel().getSelectedItems().isEmpty());
  }
}
