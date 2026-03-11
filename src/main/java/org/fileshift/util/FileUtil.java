package org.fileshift.util;

import java.io.File;

/**
 * @author aidan.liu
 * @version 1.0
 * @since 2026/2/9 15:12
 */
public class FileUtil {


  /**
   * 判断文件是否为图像文件。
   * 支持扩展名：png, jpg, jpeg, bmp, gif
   *
   * @param f 文件对象
   * @return true 表示是图像文件
   */
  public static boolean isImageFile(File f) {
    String n = f.getName().toLowerCase();
    return n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg") ||
        n.endsWith(".bmp") || n.endsWith(".gif");
  }

  /**
   * 判断文件是否为 Office 文件。
   * 支持扩展名：doc, docx, xls, xlsx
   *
   * @param f 文件对象
   * @return true 表示是 Office 文件
   */
  public static boolean isOfficeFile(File f) {
    String n = f.getName().toLowerCase();
    return n.endsWith(".doc") || n.endsWith(".docx") || n.endsWith(".xls") || n.endsWith(".xlsx");
  }

  /**
   * 检测文件是否为 PDF 文件
   *
   * @param f 文件对象
   * @return true ：是 PDF 文件
   */
  public static boolean isPdfFile(File f) {
    String n = f.getName().toLowerCase();
    return n.endsWith(".pdf");
  }
}
