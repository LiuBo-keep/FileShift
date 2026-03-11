package org.fileshift.components;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import org.fileshift.common.config.ThemeConfig;

/**
 * 主题切换组件
 *
 * @author aidan.liu
 * @version 1.0
 * @since 2026/2/11 14:32
 */
public class SwitchThemesComponent {

  private final BorderPane rootPane;
  private final CheckBox chkDarkMode;
  private final NotificationComponent notificationComponent;

  public SwitchThemesComponent(BorderPane rootPane, CheckBox chkDarkMode, NotificationComponent notificationComponent) {
    this.rootPane = rootPane;
    this.chkDarkMode = chkDarkMode;
    this.notificationComponent = notificationComponent;
  }

  /**
   * 初始化
   */
  public void init() {
    chkDarkMode.setSelected(ThemeConfig.getTheme() == ThemeConfig.Theme.DARK);
    chkDarkMode.selectedProperty().addListener((obs, oldVal, newVal) -> {
      ThemeConfig.Theme newTheme = newVal ? ThemeConfig.Theme.DARK : ThemeConfig.Theme.LIGHT;
      ThemeConfig.saveTheme(newTheme);
      applyTheme(rootPane.getScene());
      notificationComponent.showNotification("主题已切换为 " + (newVal ? "深色" : "浅色"), true);
    });
  }

  /**
   * 应用主题
   */
  public void applyTheme(Scene scene) {
    ThemeConfig.Theme theme = ThemeConfig.getTheme();

    // 先清空所有自定义 stylesheet（保留 JavaFX 默认）
    scene.getStylesheets().clear();

    // 始终加载基础样式（如果有）
    // scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

    // 根据主题加载
    if (theme == ThemeConfig.Theme.DARK) {
      scene.getStylesheets().add(getClass().getResource("/css/styles-dark.css").toExternalForm());
    } else {
      scene.getStylesheets().add(getClass().getResource("/css/styles-light.css").toExternalForm());
    }
  }
}
