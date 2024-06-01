package com.metax.web.util;

import cn.hutool.core.io.IoUtil;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: hanabi
 */
@Slf4j
public class FileUtil {

    /**
     * 读取远程链接或本地文件路径，返回File对象
     *
     * @param path      文件路径
     * @param resourcePath 远程链接或本地文件路径
     * @return File对象，如果读取失败则返回null
     */
    public static File getResourceAsFile(String path, String resourcePath) {
        InputStream urlStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL url = null;
            File file;
            if (resourcePath.startsWith("http://") || resourcePath.startsWith("https://")) {
                url = new URL(resourcePath);
                file = new File(path, new File(url.getPath()).getName());
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    urlStream = url.openStream();
                    fileOutputStream = new FileOutputStream(file);
                    IoUtil.copy(urlStream, fileOutputStream);
                }
            } else {
                file = new File(resourcePath);
                if (!file.exists()) {
                    throw new IllegalArgumentException("Local file does not exist");
                }
            }
            return file;
        } catch (Exception e) {
            log.error("FileUtils#getResourceAsFile failed: {}, resourcePath: {}", Throwables.getStackTraceAsString(e), resourcePath);
            return null;
        }finally {
            try {
                if (urlStream != null) {
                    IoUtil.close(urlStream);
                }
                if (fileOutputStream != null) {
                    IoUtil.close(fileOutputStream);
                }
            } catch (Exception e) {
                log.error("资源关闭失败: {}",Throwables.getStackTraceAsString(e));
            }
        }
    }

    /**
     * 读取 远程链接或本地文件路径集合 返回有效的File对象集合
     *
     * @param path       文件路径
     * @param remoteUrls cdn/oss文件访问链接集合
     * @return
     */
    public static List<File> getRemoteUrl2File(String path, Collection<String> remoteUrls) {
        List<File> files = new ArrayList<>();
        remoteUrls.forEach(remoteUrl -> {
            File file = getResourceAsFile(path, remoteUrl);
            if (file != null) {
                files.add(file);
            }
        });
        return files;
    }

}
