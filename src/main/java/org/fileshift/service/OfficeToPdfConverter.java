package org.fileshift.service;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.fileshift.components.CommonComponent;
import org.fileshift.model.ConvertTask;

/**
 * <p>
 * Office 文件（Word、Excel）转 PDF 工具类，提供将 Office 文件转换为 PDF 的方法。
 * </p>
 * <p>
 * 功能：
 * <ul>
 *   <li>支持 DOC、DOCX、XLS、XLSX 文件转换为 PDF</li>
 *   <li>使用 Documents4j 库实现文件格式转换</li>
 *   <li>可更新 {@link ConvertTask} 的进度属性</li>
 * </ul>
 * </p>
 * <p>
 * 使用场景示例：
 * <pre>
 * ConvertTask task = new ConvertTask(file, "pdf");
 * boolean success = OfficeToPdfConverter.convert(file, task);
 * </pre>
 * </p>
 * <p>
 * 依赖 Documents4j 库（本地转换器 LocalConverter）。
 * </p>
 *
 * @author aidan
 * @version 1.0
 * @since 2026/2/9
 */
public class OfficeToPdfConverter {

  /**
   * 将 Office 文件转换为 PDF 文件。
   * <p>
   * 流程：
   * <ol>
   *   <li>根据输入文件名生成同名 PDF 文件，存放在同一目录</li>
   *   <li>创建 Documents4j 本地转换器</li>
   *   <li>根据文件后缀判断源文件类型（Word 或 Excel）</li>
   *   <li>执行转换操作并将输出写入 PDF 文件</li>
   *   <li>更新任务进度</li>
   * </ol>
   * </p>
   *
   * @param inputFile 待转换的 Office 文件 {@link File}
   * @param task      转换任务 {@link ConvertTask}，用于更新状态和进度
   * @return true 表示转换成功，false 表示转换失败或出现异常
   */
  public static boolean convert(File inputFile, ConvertTask task, CommonComponent helper) {
    File output = helper.resolveOutputFile(inputFile, task.getTargetFormat());

    try (InputStream in = new FileInputStream(inputFile);
         OutputStream out = new FileOutputStream(output)) {

      IConverter converter = LocalConverter.builder().build();

      // 根据文件类型选择源格式
      DocumentType sourceType = getDocumentType(inputFile);

      boolean success = converter.convert(in).as(sourceType)
          .to(out).as(DocumentType.PDF)
          .execute();

      task.setProgress(1.0);
      return success;

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 根据文件后缀判断 Office 文件类型。
   *
   * @param file 待转换文件
   * @return {@link DocumentType} 对应源文件类型
   * @throws IllegalArgumentException 不支持的文件类型
   */
  private static DocumentType getDocumentType(File file) {
    String name = file.getName().toLowerCase();
    if (name.endsWith(".docx") || name.endsWith(".doc")) {
      return DocumentType.MS_WORD;
    } else if (name.endsWith(".xlsx") || name.endsWith(".xls")) {
      return DocumentType.MS_EXCEL;
    }
    throw new IllegalArgumentException("Unsupported file type: " + name);
  }
}
