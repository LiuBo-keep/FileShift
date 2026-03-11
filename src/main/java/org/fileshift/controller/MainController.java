package org.fileshift.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.fileshift.components.CommonComponent;
import org.fileshift.components.DeleteSelectedFileComponent;
import org.fileshift.components.DirectoryChooserComponent;
import org.fileshift.components.NotificationComponent;
import org.fileshift.components.SwitchThemesComponent;
import org.fileshift.helper.ConversionHelper;
import org.fileshift.helper.FileDropHandler;
import org.fileshift.helper.ResourceCleanupHelper;
import org.fileshift.model.ConvertTask;

/**
 * <p>
 * 主界面控制器类，负责管理 FileShift 应用的核心界面逻辑。
 * </p>
 * <p>
 * 功能：
 * <ul>
 *   <li>初始化 UI 控件和表格绑定</li>
 *   <li>初始化通知组件 {@link NotificationComponent}</li>
 *   <li>初始化转换组件 {@link ConversionHelper} 并绑定按钮事件</li>
 *   <li>初始化文件拖拽组件 {@link FileDropHandler}</li>
 *   <li>初始化资源清理组件 {@link ResourceCleanupHelper}</li>
 *   <li>根据选中的功能切换显示图像或 Office 面板</li>
 *   <li>提供线程池关闭和状态检查接口</li>
 * </ul>
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>此控制器对应 FXML 文件的控件注入</li>
 *   <li>在 {@link AppLauncher} 中启动后会调用 {@link #setupDragAndDrop()}</li>
 * </ul>
 * </p>
 *
 * @author aidan
 * @version 1.0
 * @since 2026/2/10
 */
@SuppressWarnings("all")
public class MainController implements Initializable {

  @FXML
  private BorderPane rootPane;

  /**
   * 通知容器
   */
  @FXML
  private VBox notificationContainer;
  @FXML
  private Label notificationLabel;

  //=========侧边栏按钮功能选择组================
  @FXML
  private ToggleGroup functionGroup;
  /**
   * 图像转PDF功能按钮
   */
  @FXML
  private RadioButton rbImageToPdf;
  /**
   * Office转PDF功能按钮
   */
  @FXML
  private RadioButton rbOfficeToPdf;
  /**
   * PDF转Word功能按钮
   */
  @FXML
  private RadioButton rbPdfToWord;
  /**
   * 主题切换按钮
   */
  @FXML
  private CheckBox chkDarkMode;

  //=========功能面板================
  /**
   * 图片转PDF功能面板
   */
  @FXML
  private VBox paneImageToPdf;
  @FXML
  private TableView<ConvertTask> tableFiles;
  @FXML
  private TableColumn<ConvertTask, String> colName;
  @FXML
  private TableColumn<ConvertTask, String> colStatus;
  @FXML
  private TableColumn<ConvertTask, Number> colProgress;

  /**
   * Office转PDF功能面板
   */
  @FXML
  private VBox paneOfficeToPdf;
  @FXML
  private TableView<ConvertTask> tableOffice;
  @FXML
  private TableColumn<ConvertTask, String> colNameOffice;
  @FXML
  private TableColumn<ConvertTask, String> colStatusOffice;
  @FXML
  private TableColumn<ConvertTask, Number> colProgressOffice;

  /**
   * PDF转Word功能面板
   */
  @FXML
  private VBox panePdfToWord;
  @FXML
  private TableView<ConvertTask> tablePdfToWord;
  @FXML
  private TableColumn<ConvertTask, String> colStatusPdfToWord;
  @FXML
  private TableColumn<ConvertTask, String> colNamePdfToWord;
  @FXML
  private TableColumn<ConvertTask, Number> colProgressPdfToWord;

  //=========底部栏================
  /**
   * 开始按钮
   */
  @FXML
  private Button btnStart;
  /**
   * 全局进度条
   */
  @FXML
  private ProgressBar globalProgress;

  /**
   * 输出目录选择按钮
   */
  @FXML
  private Button btnChooseOutput;
  /**
   * 输出目录显示标签
   */
  @FXML
  private Label lblOutputDir;
  /**
   * 删除按钮
   */
  @FXML
  private Button btnDelete;


  private CommonComponent commonComponent;
  private FileDropHandler fileDropHandler;
  private ConversionHelper conversionHelper;
  private SwitchThemesComponent switchThemesComponent;
  private ResourceCleanupHelper resourceCleanupHelper;
  private NotificationComponent notificationComponent;
  private DirectoryChooserComponent directoryChooserComponent;
  private DeleteSelectedFileComponent deleteSelectedFileComponent;

  /**
   * 初始化控制器，绑定界面控件和逻辑组件。
   * <p>
   * 初始化流程：
   * <ol>
   *   <li>创建通知组件并绑定 Label 与容器</li>
   *   <li>创建转换组件并绑定表格和进度条</li>
   *   <li>初始化文件拖拽处理器</li>
   *   <li>初始化资源清理助手</li>
   *   <li>绑定表格列显示文件名、状态和进度</li>
   *   <li>根据功能选择切换显示面板</li>
   *   <li>绑定开始按钮触发转换任务</li>
   * </ol>
   * </p>
   *
   * @param url FXML 文件路径（框架提供）
   * @param rb  国际化资源包（框架提供）
   */

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    initComponent();
    // 图片转PDF功能面板
    tableFiles.setItems(commonComponent.getImageTasks());
    tableFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFileName()));
    colStatus.setCellValueFactory(c -> c.getValue().statusProperty());
    colProgress.setCellValueFactory(c -> c.getValue().progressProperty());

    // Office转PDF功能面板
    tableOffice.setItems(commonComponent.getOfficeTasks());
    tableOffice.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    colNameOffice.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFileName()));
    colStatusOffice.setCellValueFactory(c -> c.getValue().statusProperty());
    colProgressOffice.setCellValueFactory(c -> c.getValue().progressProperty());

    // PDF转Word功能面板
    tablePdfToWord.setItems(commonComponent.getPdfToWordTasks());
    tablePdfToWord.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    colStatusPdfToWord.setCellValueFactory(
        c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFileName()));
    colNamePdfToWord.setCellValueFactory(c -> c.getValue().statusProperty());
    colProgressPdfToWord.setCellValueFactory(c -> c.getValue().progressProperty());

    functionGroup.selectedToggleProperty().addListener((obs, old, newToggle) -> {
      if (newToggle == rbImageToPdf) {
        paneImageToPdf.setVisible(true);
        paneOfficeToPdf.setVisible(false);
        panePdfToWord.setVisible(false);
      } else if (newToggle == rbOfficeToPdf) {
        paneImageToPdf.setVisible(false);
        paneOfficeToPdf.setVisible(true);
        panePdfToWord.setVisible(false);
      } else if (newToggle == rbPdfToWord) {
        paneImageToPdf.setVisible(false);
        paneOfficeToPdf.setVisible(false);
        panePdfToWord.setVisible(true);
      }
    });

    /**
     * 绑定组件
     */
    bindingComponent();

    System.out.println("MainController 初始化完成");
  }

  /**
   * 初始化组件。
   * <p>
   * 创建转换组件、通知组件、文件拖拽组件、资源清理组件，并绑定到对应的控件。
   * </p>
   */
  private void initComponent() {
    commonComponent = new CommonComponent(paneImageToPdf, paneOfficeToPdf, panePdfToWord);
    // 初始化通知组件
    notificationComponent =
        new NotificationComponent(notificationLabel, notificationContainer);
    // 初始化转换组件
    conversionHelper = new ConversionHelper(btnStart, globalProgress, commonComponent,
        notificationComponent
    );
    // 初始化文件拖拽
    fileDropHandler = new FileDropHandler(rootPane, commonComponent);
    // 初始化目录选择组件
    directoryChooserComponent =
        new DirectoryChooserComponent(rootPane, lblOutputDir, commonComponent, notificationComponent, btnChooseOutput);
    // 初始化主题切换组件
    switchThemesComponent = new SwitchThemesComponent(rootPane, chkDarkMode, notificationComponent);
    // 初始化资源清理组件
    resourceCleanupHelper = new ResourceCleanupHelper(rootPane, globalProgress, conversionHelper);
    // 初始化删除选中文件组件
    deleteSelectedFileComponent =
        new DeleteSelectedFileComponent(btnDelete, rbImageToPdf, functionGroup, rbOfficeToPdf, rbPdfToWord, tableFiles,
            tableOffice, tablePdfToWord, notificationComponent);
  }

  /**
   * 绑定组件。
   * <p>
   * 绑定转换组件、目录选择组件、主题切换组件、删除选中文件组件。
   * </p>
   */
  private void bindingComponent() {
    // 启动转换
    conversionHelper.init();
    // 选择输出目录
    directoryChooserComponent.init();
    // 初始化主题切换
    switchThemesComponent.init();
    // 绑定删除按钮事件
    deleteSelectedFileComponent.init();
  }


  /**
   * 设置拖拽文件功能。
   * <p>
   * 在 {@link AppLauncher#start(javafx.stage.Stage)} 中调用，
   * 保证界面加载后即可支持拖拽添加文件。
   * </p>
   */
  public void setupDragAndDrop() {
    fileDropHandler.setupDragAndDrop();
  }

  /**
   * 获取主题切换组件。
   *
   * @return 主题切换组件
   */
  public void applyTheme(Scene scene) {
    switchThemesComponent.applyTheme(scene);
  }

  /**
   * 执行资源清理。
   * <p>
   * 调用 {@link ResourceCleanupHelper#cleanup()}，解绑进度条并关闭线程池。
   * </p>
   */
  public void shutdownExecutor() {
    resourceCleanupHelper.cleanup();
  }

  /**
   * 判断后台线程池是否已终止。
   *
   * @return true 表示线程池已关闭且所有任务完成
   */
  public boolean isExecutorTerminated() {
    List<Runnable> runnables = conversionHelper.shutdownNow();
    return runnables.isEmpty();
  }
}