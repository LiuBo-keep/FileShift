package org.fileshift.model;


import java.io.File;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.fileshift.common.constant.ConversionStartInfo;

/**
 * <p>
 * 转换任务实体类，用于表示单个文件的转换信息。
 * </p>
 * <p>
 * 功能：
 * <ul>
 *   <li>保存源文件 {@link File} 和目标格式（如 "pdf"）</li>
 *   <li>维护任务状态（等待中、转换中、完成、失败等）</li>
 *   <li>维护任务进度，可绑定到 UI {@link javafx.scene.control.ProgressBar}</li>
 * </ul>
 * </p>
 * <p>
 * 使用场景：
 * <pre>
 * ConvertTask task = new ConvertTask(file, "pdf");
 * task.statusProperty().addListener(...);
 * task.progressProperty().bind(progressBar.progressProperty());
 * </pre>
 * </p>
 * <p>
 * 依赖 JavaFX 属性类，可直接绑定到 TableView 或 ProgressBar 显示状态和进度。
 *
 * @author aidan
 * @version 1.0
 * @since 2026/2/9
 */
public class ConvertTask {
    private final File file;

    private final String targetFormat;  // 新增：目标格式（如 "pdf"）

    private final StringProperty status = new SimpleStringProperty(ConversionStartInfo.WAITING);
    private final DoubleProperty progress = new SimpleDoubleProperty(0.0);

    public ConvertTask(File file, String targetFormat) {
        this.file = file;
        this.targetFormat = targetFormat;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return file.getName();
    }

    public String getTargetFormat() {
        return targetFormat;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String s) {
        status.set(s);
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double p) {
        progress.set(p);
    }
}