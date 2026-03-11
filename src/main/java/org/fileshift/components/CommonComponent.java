package org.fileshift.components;

import java.io.File;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import org.fileshift.common.config.OutputDirConfig;
import org.fileshift.common.enums.ConversionMode;
import org.fileshift.model.ConvertTask;
import org.fileshift.util.FileUtil;

/**
 * @author aidan.liu
 * @version 1.0
 * @since 2026/2/13 10:26
 */
public class CommonComponent {

  private File outputDirectory;

  /**
   * 拖拽文件面板
   */
  private final VBox paneImageToPdf;
  private final VBox paneOfficeToPdf;
  private final VBox panePdfToWord;

  /**
   * 转换任务列表
   */
  private final ObservableList<ConvertTask> imageTasks = FXCollections.observableArrayList();
  private final ObservableList<ConvertTask> officeTasks = FXCollections.observableArrayList();
  private final ObservableList<ConvertTask> pdfToWordTasks = FXCollections.observableArrayList();


  /**
   * 构造器
   *
   * @param paneImageToPdf  图像转换任务面板
   * @param paneOfficeToPdf Office 转换任务面板
   * @param panePdfToWord   PDF 转 Word 任务面板
   */
  public CommonComponent(VBox paneImageToPdf, VBox paneOfficeToPdf, VBox panePdfToWord) {
    this.paneImageToPdf = paneImageToPdf;
    this.paneOfficeToPdf = paneOfficeToPdf;
    this.panePdfToWord = panePdfToWord;
    this.outputDirectory = OutputDirConfig.getLastOutputDir();
  }

  public ObservableList<ConvertTask> getImageTasks() {
    return imageTasks;
  }

  public ObservableList<ConvertTask> getOfficeTasks() {
    return officeTasks;
  }

  public ObservableList<ConvertTask> getPdfToWordTasks() {
    return pdfToWordTasks;
  }

  /**
   * 获取当前显示的转换任务列表
   *
   * @return 当前显示的转换任务列表
   */
  public ObservableList<ConvertTask> getTargetList() {
    ObservableList<ConvertTask> targetList = null;
    if (paneImageToPdf.isVisible()) {
      targetList = imageTasks;
    }
    if (paneOfficeToPdf.isVisible()) {
      targetList = officeTasks;
    }
    if (panePdfToWord.isVisible()) {
      targetList = pdfToWordTasks;
    }
    return targetList;
  }

  /**
   * 获取当前显示的转换模式
   *
   * @return 当前显示的转换模式
   */
  public ConversionMode conversionMode() {
    if (paneImageToPdf.isVisible()) {
      return ConversionMode.IMAGE_TO_PDF;
    }
    if (paneOfficeToPdf.isVisible()) {
      return ConversionMode.OFFICE_TO_PDF;
    }
    if (panePdfToWord.isVisible()) {
      return ConversionMode.PDF_TO_WORD;
    }
    return null;
  }

  /**
   * 获取当前显示的转换目标格式
   *
   * @return 当前显示的转换目标格式
   */
  public String targetFormat() {
    if (paneImageToPdf.isVisible()) {
      return "pdf";
    }

    if (paneOfficeToPdf.isVisible()) {
      return "pdf";
    }

    if (panePdfToWord.isVisible()) {
      return "docx";
    }

    return null;
  }

  /**
   * 获取当前显示的转换任务列表的过滤器
   *
   * @return 当前显示的转换任务列表的过滤器
   */
  public Predicate<File> getFileFilter() {
    if (paneImageToPdf.isVisible()) {
      return FileUtil::isImageFile;
    }

    if (paneOfficeToPdf.isVisible()) {
      return FileUtil::isOfficeFile;
    }

    if (panePdfToWord.isVisible()) {
      return FileUtil::isPdfFile;
    }
    return null;
  }

  public void setOutputDirectory(File dir) {
    this.outputDirectory = dir;
    OutputDirConfig.saveOutputDir(dir);
  }

  public File resolveOutputFile(File inputFile, String targetFormat) {
    String pdfName = inputFile.getName().replaceFirst("[.][^.]+$", "") + "." + targetFormat;

    if (outputDirectory != null && outputDirectory.isDirectory() && outputDirectory.canWrite()) {
      return new File(outputDirectory, pdfName);
    }

    // 回退到原目录
    return new File(inputFile.getParent(), pdfName);
  }

  public String getOutputDirDisplayName() {
    if (outputDirectory != null) {
      return outputDirectory.getAbsolutePath();
    }
    return "原文件所在目录";
  }

  /**
   * 获取输出目录
   */
  public File getOutputDirectory() {
    return outputDirectory;
  }
}
