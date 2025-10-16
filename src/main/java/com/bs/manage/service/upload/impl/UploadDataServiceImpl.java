package com.bs.manage.service.upload.impl;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.mapper.upload.UploadDataMapper;
import com.bs.manage.model.bean.upload.UploadData;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.upload.UploadDataService;
import com.bs.manage.until.DateUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 2020/4/15 9:47
 * fzj
 */
@Service
@Slf4j
public class UploadDataServiceImpl extends CommonServiceImpl<UploadData> implements UploadDataService {

    /**
     * 随机数
     */
    public static Random random = new Random();

    /**
     * 文件类型
     */
    private final static Map<String, String> FILE_TYPE_MAP = new HashMap<>();

    static {
        //图片
        FILE_TYPE_MAP.put("FFD8FF", "jpg");
        FILE_TYPE_MAP.put("89504E47", "png");
        FILE_TYPE_MAP.put("47494638", "gif");
        FILE_TYPE_MAP.put("49492A00", "tif");
        FILE_TYPE_MAP.put("424D", "bmp");
        //视频
        FILE_TYPE_MAP.put("57415645", "wav");
        FILE_TYPE_MAP.put("3026b2758e66cf11a6d9", "wmv"); //wmv与asf相同
        FILE_TYPE_MAP.put("41564920", "avi");
        FILE_TYPE_MAP.put("2E524D46", "rm");
        FILE_TYPE_MAP.put("000001B", "mpg");
        FILE_TYPE_MAP.put("00000020667479706d70", "mp4");
        FILE_TYPE_MAP.put("6D6F6F76", "mov");
        FILE_TYPE_MAP.put("464c5601050000000900", "flv"); //flv与f4v相同
        //音乐
        FILE_TYPE_MAP.put("49443303000000002176", "mp3");
        //pdf
        FILE_TYPE_MAP.put("255044462D312E", "pdf");
    }

    @Value("${uploadBase}")
    public String uploadBase;
    @Value("${uploadUrl}")
    private String uploadUrl;

    private final Gson gson;
    private final UploadDataMapper uploadDataMapper;

    public UploadDataServiceImpl(Gson gson, UploadDataMapper uploadDataMapper) {
        this.gson = gson;
        this.uploadDataMapper = uploadDataMapper;
    }

    /**
     * 获取文件类型
     *
     * @param bytes 文件的前几个字段
     * @return 文件类型
     */
    public static String getFileType(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2)
                stringBuilder.append(0);
            stringBuilder.append(hv);
        }
        String fileCode = stringBuilder.toString().toUpperCase();
        System.out.println(fileCode);
        for (String key : FILE_TYPE_MAP.keySet()) {
            if (key.toUpperCase().startsWith(fileCode) || fileCode.startsWith(key.toUpperCase())) {
                return FILE_TYPE_MAP.get(key);
            }
        }
        return null;
    }

    /**
     * 批量上传文件
     *
     * @param files  上传的文件
     * @param source 上传的文件来源 1-授权证书
     */
    @Override
    public List<UploadData> upload(MultipartFile[] files, Integer source) {
        List<UploadData> uploadDataList = new ArrayList<>();
        String date = LocalDateTime.now().format(DateUtil.yyyyMMdd_FORMATTER);
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            UploadData uploadData = UploadData.builder().original_name(file.getOriginalFilename()).source(source).size(file.getSize())
                    .has_use(CodeCaption.FALSE).warn_level(1).upload_status(CodeCaption.FALSE).build();
            uploadDataList.add(uploadData);

            String directoryName = getDirectoryName(source, date);
            File directory = new File(directoryName);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    uploadData.setUpload_msg("文件夹创建失败");
                    continue;
                }
            }

            FileInputStream inputStream = null;
            FileChannel inChannel = null;
            FileChannel outChannel = null;
            try {
                inputStream = (FileInputStream) file.getInputStream();
                inChannel = inputStream.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

                //读取前4个字节以获取文件格式,并写入文件
                inChannel.read(byteBuffer);
                byteBuffer.flip();
                byteBuffer.mark();
                byte[] bytes = new byte[4];
                byteBuffer.get(bytes);
                String fileType = UploadDataServiceImpl.getFileType(bytes); //获取文件真实类型
                if (fileType == null) {
                    uploadData.setUpload_msg("不支持的文件类型");
                    continue;
                }
                String fileName = directoryName + System.currentTimeMillis() + random.nextInt(9999) + "." + fileType;
                outChannel = FileChannel.open(Paths.get(fileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                byteBuffer.reset();
                outChannel.write(byteBuffer);
                byteBuffer.clear();

                //写入文件
                while (inChannel.read(byteBuffer) != -1) {
                    byteBuffer.flip();
                    outChannel.write(byteBuffer);
                    byteBuffer.clear();
                }
                uploadData.setUpload_status(CodeCaption.TRUE);
                uploadData.setUrl(fileName.replace(uploadBase, uploadUrl));
                uploadDataMapper.insert(uploadData);
            } catch (IOException e) {
                log.error(e.getMessage());
                String uploadMsg = e.getMessage();
                if (e.getMessage() != null && e.getMessage().length() > 100) {
                    uploadMsg = uploadMsg.substring(0, 100);
                }
                uploadData.setUpload_msg(uploadMsg);
            } finally {
                if (outChannel != null) {
                    try {
                        outChannel.close();
                    } catch (IOException e) {
                        log.error("outChannel关闭失败");
                    }
                }
                if (inChannel != null) {
                    try {
                        inChannel.close();
                    } catch (IOException e) {
                        log.error("inChannel关闭失败");
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        log.error("inputStream关闭失败");
                    }
                }
            }

        }
        log.info("文件上传结果：{}", gson.toJson(uploadDataList));
        return uploadDataList;
    }

    /**
     * 标记是否使用,优先使用id匹配
     *
     * @param hasUse      0-未使用 1-使用
     * @param id          图片id
     * @param url         文件访问链接
     * @param compressUrl 压缩文件的访问链接
     */
    @Override
    @Transactional
    public void markUse(Integer hasUse, Long id, String url, String compressUrl) {
        Long dataId = id;
        if (id == null) {
            UploadData uploadData = uploadDataMapper.getOneBySelectKey(UploadData.builder().url(url).compress_url(compressUrl).build());
            if (uploadData != null) {
                dataId = uploadData.getId();
            } else {
                log.error("图库中找不到对应的图片,图片链接：{},压缩图片链接：{}", url, compressUrl);
                return;
            }
        }
        uploadDataMapper.update(UploadData.builder().has_use(hasUse).id(dataId).build());
    }


    /**
     * 获取文件夹名称
     *
     * @param source 上传来源 1-授权证书
     * @param date   上传日期,yyyMMdd
     * @return 文件夹名称
     */
    private String getDirectoryName(Integer source, String date) {
        String directoryName = "/upload/temp/";
        if (source == 1) {
            directoryName = "/upload/auth/";
        }
        return uploadBase + directoryName + date + "/";
    }


    @Override
    public void afterPropertiesSet() {
        setCommonMapper(uploadDataMapper);
    }
}


