package org.fileshift.helper;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

/**
 * <p>
 * 资源清理辅助类，用于在应用退出或主界面关闭时释放资源，确保程序不会存在内存泄漏或未关闭线程。
 * </p>
 * <p>
 * 功能：
 * <ul>
 *   <li>解绑全局进度条属性，重置进度值</li>
 *   <li>关闭 {@link ConversionHelper} 的线程池，停止所有后台转换任务</li>
 *   <li>移除根节点的拖拽事件监听器，防止节点引用循环导致内存泄漏</li>
 * </ul>
 * </p>
 * <p>
 * 使用场景示例：
 * <pre>
 * ResourceCleanupHelper cleanupHelper = new ResourceCleanupHelper(rootPane, globalProgress, conversionHelper);
 * cleanupHelper.cleanup();
 * </pre>
 * </p>
 *
 * @author aidan
 * @version 1.0
 * @since 2026/2/10
 */
public class ResourceCleanupHelper {

    private final BorderPane rootPane;
    private final ProgressBar globalProgress;
    private final ConversionHelper conversionHandler;

    /**
     * 构造器，初始化资源清理助手。
     *
     * @param rootPane          主界面根节点
     * @param globalProgress    全局进度条
     * @param conversionHandler 转换助手实例
     */
    public ResourceCleanupHelper(BorderPane rootPane, ProgressBar globalProgress, ConversionHelper conversionHandler) {
        this.rootPane = rootPane;
        this.globalProgress = globalProgress;
        this.conversionHandler = conversionHandler;
    }

    /**
     * 执行资源清理。
     * <p>
     * 清理步骤：
     * <ol>
     *   <li>解绑全局进度条属性，并重置进度为 0</li>
     *   <li>关闭转换助手的线程池，停止所有正在运行的任务</li>
     *   <li>移除根节点的拖拽事件监听器，防止内存泄漏</li>
     * </ol>
     * </p>
     * <p>
     * 调用此方法后，主界面相关资源将被释放，适合在应用退出或窗口关闭时使用。
     * </p>
     */
    public void cleanup() {
        // 1. 解绑进度条
        globalProgress.progressProperty().unbind();
        globalProgress.setProgress(0);

        // 2. 关闭线程池
        conversionHandler.shutdownExecutor();

        // 3. 移除所有事件监听器（防止根节点引用循环）
        if (rootPane != null) {
            rootPane.setOnDragOver(null);
            rootPane.setOnDragDropped(null);
            rootPane.setOnDragExited(null);
        }

        System.out.println("MainController 已清理，所有资源释放");
    }
}
