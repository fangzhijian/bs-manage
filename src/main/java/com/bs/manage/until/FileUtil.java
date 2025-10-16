package com.bs.manage.until;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 2020/4/15 9:32
 * fzj
 */
public class FileUtil {

    //压缩文件
    public static void zipFiles(List<File> srcFile, File zipFile) throws IOException {
        byte[] buf = new byte[1024];
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
        for (File file : srcFile) {
            FileInputStream in = new FileInputStream(file);
            out.putNextEntry(new ZipEntry(file.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in.close();
        }
        out.close();
    }

}
