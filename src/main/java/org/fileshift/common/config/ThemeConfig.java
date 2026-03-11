package org.fileshift.common.config;

import java.util.prefs.Preferences;

/**
 * 主题配置类
 *
 * @author aidan.liu
 * @version 1.0
 * @since 2026/2/11 14:29
 */
public class ThemeConfig {

  private static final Preferences prefs = Preferences.userNodeForPackage(ThemeConfig.class);
  private static final String KEY_THEME = "theme_mode";

  public enum Theme {
    LIGHT, DARK
  }

  public static Theme getTheme() {
    String saved = prefs.get(KEY_THEME, Theme.LIGHT.name());
    try {
      return Theme.valueOf(saved);
    } catch (Exception e) {
      return Theme.LIGHT;
    }
  }

  public static void saveTheme(Theme theme) {
    prefs.put(KEY_THEME, theme.name());
  }
}
