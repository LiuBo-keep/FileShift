package org.fileshift.service;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.fileshift.components.CommonComponent;
import org.fileshift.model.ConvertTask;

/**
 * @author aidan.liu
 * @version 1.0
 * @since 2026/2/13 10:45
 */
public class PdfToWordConverter {

  public static boolean convert(File inputPdf, ConvertTask task, CommonComponent helper) {
    File output = helper.resolveOutputFile(inputPdf, task.getTargetFormat());
    System.out.println("output: " + output.getAbsolutePath());
    System.out.println("output: " + output.getName());
    // TODO: 2026/2/13
    IConverter converter = LocalConverter.builder().build();

    try (FileInputStream in = new FileInputStream(inputPdf);
         OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(output))) {

      boolean success = converter.convert(in).as(DocumentType.PDF)
          .to(outputStream).as(DocumentType.DOCX)
          .execute();

      task.setProgress(1.0);
      return success;

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
