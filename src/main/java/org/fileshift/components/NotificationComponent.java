package org.fileshift.components;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


/**
 * <p>
 * 通知组件，用于在界面上显示临时提示信息（如操作成功或失败）。
 * 支持颜色区分（绿色表示成功，红色表示失败），并带有淡入/淡出与滑入/滑出动画效果。
 * </p>
 * <p>
 * 使用场景示例：
 * <pre>
 * Label label = new Label();
 * VBox container = new VBox(label);
 * NotificationComponent notification = new NotificationComponent(label, container);
 * notification.showNotification("操作成功", true);
 * </pre>
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>该组件依赖 JavaFX 的动画类（FadeTransition、TranslateTransition、PauseTransition）</li>
 *   <li>需要将 `notificationContainer` 放置在可见的父布局中，否则动画无法显示</li>
 * </ul>
 * </p>
 *
 * @author aidan
 * @version 1.0
 * @since 2026/2/10
 */
public class NotificationComponent {

  private final Label notificationLabel;
  private final VBox notificationContainer;

  /**
   * 构造器，初始化通知组件。
   *
   * @param notificationLabel     显示通知文字的 Label 控件
   * @param notificationContainer 承载通知 Label 的容器 VBox
   */
  public NotificationComponent(Label notificationLabel, VBox notificationContainer) {
    this.notificationLabel = notificationLabel;
    this.notificationContainer = notificationContainer;
  }


  /**
   * 显示通知消息，并带有动画效果。
   *
   * <p>显示流程：
   * <ol>
   *   <li>设置消息文本</li>
   *   <li>根据 isSuccess 设置背景颜色（绿色或红色）</li>
   *   <li>显示容器并播放淡入 + 从底部滑入动画</li>
   *   <li>2 秒后播放淡出 + 滑出动画并隐藏容器</li>
   * </ol>
   * </p>
   *
   * @param message   要显示的通知消息
   * @param isSuccess true 表示成功通知（绿色），false 表示失败通知（红色）
   */
  public void showNotification(String message, boolean isSuccess) {
    notificationLabel.setText(message);

    // 颜色
    String bgColor = isSuccess ? "#4CAF50" : "#f44336";
    notificationLabel.setStyle(
        "-fx-background-color: " + bgColor + "cc; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12 24 12 24; " +
            "-fx-background-radius: 8; " +
            "-fx-font-size: 14px; " +
            "-fx-min-width: 320; " +
            "-fx-max-width: 400; " +
            "-fx-alignment: CENTER; " +
            "-fx-wrap-text: true;"
    );

    notificationContainer.setVisible(true);

    // 从上方滑入 + 淡入
    FadeTransition fadeIn = new FadeTransition(Duration.millis(350), notificationContainer);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);

    TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), notificationContainer);
    slideIn.setFromY(-100);   // 从上方 -100px
    slideIn.setToY(0);

    fadeIn.play();
    slideIn.play();

    // 停留 2.2 秒后向上滑出 + 淡出
    PauseTransition pause = new PauseTransition(Duration.seconds(2.2));
    pause.setOnFinished(e -> {
      FadeTransition fadeOut = new FadeTransition(Duration.millis(350), notificationContainer);
      fadeOut.setFromValue(1.0);
      fadeOut.setToValue(0.0);

      TranslateTransition slideOut = new TranslateTransition(Duration.millis(350), notificationContainer);
      slideOut.setFromY(0);
      slideOut.setToY(-100);   // 向上滑出

      fadeOut.play();
      slideOut.play();

      fadeOut.setOnFinished(ev -> notificationContainer.setVisible(false));
    });
    pause.play();
  }
}
