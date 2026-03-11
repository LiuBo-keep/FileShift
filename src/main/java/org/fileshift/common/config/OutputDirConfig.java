package org.fileshift.common.config;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * @author aidan.liu
 * @version 1.0
 * @since 2026/2/11 13:52
 */
public class OutputDirConfig {

  private static final Preferences prefs = Preferences.userNodeForPackage(OutputDirConfig.class);
  private static final String KEY_LAST_OUTPUT_DIR = "last_output_directory";

  /**
   * 获取上次保存的输出目录
   *
   * @return 上次选择的有效目录，或 null（使用原文件目录）
   */
  public static File getLastOutputDir() {
    String path = prefs.get(KEY_LAST_OUTPUT_DIR, null);
    if (path != null) {
      File dir = new File(path);
      if (dir.isDirectory() && dir.canWrite()) {
        return dir;
      }
    }
    return null;
  }

  /**
   * 保存用户选择的输出目录
   *
   * @param dir 要保存的目录
   */
  public static void saveOutputDir(File dir) {
    if (dir != null && dir.isDirectory() && dir.canWrite()) {
      prefs.put(KEY_LAST_OUTPUT_DIR, dir.getAbsolutePath());
    }
  }

  /**
   * 清除保存的输出目录（恢复为使用原文件目录）
   */
  public static void clearOutputDir() {
    prefs.remove(KEY_LAST_OUTPUT_DIR);
  }
}
