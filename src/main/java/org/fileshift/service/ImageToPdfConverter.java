package org.fileshift.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.fileshift.components.CommonComponent;
import org.fileshift.model.ConvertTask;

/**
 * <p>
 * 图像转 PDF 工具类，提供将单张图像文件转换为 PDF 文件的方法。
 * </p>
 * <p>
 * 功能：
 * <ul>
 *   <li>支持 PNG、JPG、JPEG、BMP、GIF 等常用图像格式</li>
 *   <li>生成的 PDF 页面大小与原图像宽高一致</li>
 *   <li>可更新 {@link ConvertTask} 的进度属性</li>
 * </ul>
 * </p>
 * <p>
 * 使用场景示例：
 * <pre>
 * ConvertTask task = new ConvertTask(file, "pdf");
 * boolean success = ImageToPdfConverter.convert(file, task);
 * </pre>
 * </p>
 * <p>
 * 依赖 Apache PDFBox 库。
 * </p>
 *
 * @author aidan
 * @version 1.0
 * @since 2026/2/9
 */
public class ImageToPdfConverter {


  /**
   * 将单张图像文件转换为 PDF 文件。
   * <p>
   * 流程：
   * <ol>
   *   <li>根据输入文件名生成同名 PDF 文件，存放在同一目录</li>
   *   <li>读取图像并获取宽高</li>
   *   <li>创建 PDF 页面大小与图像一致</li>
   *   <li>将图像绘制到 PDF 页面</li>
   *   <li>保存 PDF 文件并更新任务进度</li>
   * </ol>
   * </p>
   *
   * @param inputImage 输入图像文件 {@link File}
   * @param task       转换任务 {@link ConvertTask}，用于更新状态和进度
   * @return true 表示转换成功，false 表示转换失败（图像无法读取或发生 IO 错误）
   */
  public static boolean convert(File inputImage, ConvertTask task, CommonComponent helper) {
    File output = helper.resolveOutputFile(inputImage, task.getTargetFormat());

    try (PDDocument doc = new PDDocument()) {
      BufferedImage img = ImageIO.read(inputImage);
      if (img == null) {
        return false;
      }

      float width = img.getWidth();
      float height = img.getHeight();

      PDPage page = new PDPage(new PDRectangle(width, height));
      doc.addPage(page);

      PDImageXObject pdImage = PDImageXObject.createFromFile(inputImage.getAbsolutePath(), doc);

      try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
        content.drawImage(pdImage, 0, 0, width, height);
      }

      doc.save(output.getAbsolutePath());
      task.setProgress(1.0);
      return true;

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}
