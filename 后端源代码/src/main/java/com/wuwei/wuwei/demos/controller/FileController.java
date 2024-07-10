package com.wuwei.wuwei.demos.controller;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectResult;
import com.wuwei.wuwei.demos.vo.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class FileController {

    @Value("${uploadFilePath.linux}")
    String uploadFilePath;
    @Value("${localhost}")
    String localhost;

    @Value("${alioss.endpoint}")
    String endpoint;
    @Value("${alioss.accessKeyId}")
    String accessKeyId;
    @Value("${alioss.accessKeySecret}")
    String accessKeySecret;
    @Value("${alioss.bucketName}")
    String bucketName;

    /**
     * 自己写的上传,不能删,用来给小程序上传头像的,懒得合并了
     * 狗屎`*_*`
     *
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/upload", produces = "application/json; charset=utf-8")
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        //获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        //获取文件的类型
//        String type = null;
//        if (originalFilename != null) {
//            type = originalFilename.split(".")[1];
//        }else {
//            return "";
//        }
//        log.info("文件类型是：" + type);
        //获取文件大小
        long size = file.getSize();
        File dest = new File(uploadFilePath + originalFilename);
        file.transferTo(dest);
        //设置下载的文件路径
        String url = "http://" + localhost + ":8080/file/" + originalFilename;
        return url;
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @PostMapping(value = "/Fileupload", produces = "application/json;charset=utf-8")
    public Result<String> fileupload(@RequestParam("file") MultipartFile file) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
//            File content = new File("src/main/resources/慧瞳守护.pptx");
            byte[] content = file.getBytes();
//            获取文件字节数组
            // 填写Object完整路径，例如exampled/example object.txt。Object完整路径中不能包含Bucket名称。
            String objectName = file.getOriginalFilename();
            System.out.println(objectName);
            //获取文件名称
//             >>> 如 图片1.png
            PutObjectResult putObjectResult = ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content));
            return new Result<>("上传成功", 200);
        } catch (OSSException | ClientException | IOException oe) {
            return new Result<>("上传失败", 500);

        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 下载文件
     */
    @GetMapping("/downLoad")
    public void downLoad(@RequestParam("key") String objectName, HttpServletResponse response) throws UnsupportedEncodingException {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        //通知浏览器以附件形式下载
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(objectName.getBytes(),  "ISO-8859-1"));
        response.setContentType("application/octet-stream");
        try {

            // 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元数据。
            OSSObject ossObject = ossClient.getObject(bucketName, objectName);
            // 调用ossObject.getObjectContent获取文件输入流，可读取此输入流获取其内容。
            InputStream fis = ossObject.getObjectContent();

            //获取输出流
            OutputStream fos = response.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            if (bos != null) {
                bos.flush();
                bos.close();
            }
            if (fis != null) {
                fis.close();
            }

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());


        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

    }


    /**
     * 删除桶中文件
     *
     * @param objectName
     */
    @PostMapping("/deleteFile")
    public Result<String> deleteFile(@RequestParam("key") String objectName) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            ossClient.deleteObject(bucketName, objectName);
            return new Result<>("删除完成", 200);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return new Result<>("删除失败", 500);
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return new Result<>("删除失败", 500);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

    }

    /**
     * 用于返回的文件实例类
     */
    @AllArgsConstructor
    @Data
    public static class FileListInstance {
        private String key;
        private long size;
        private String modifiedTime;
    }

    /**
     * 获取桶中所有文件列表
     *
     * @return
     */
    @GetMapping("/getFileList")
    public Result<List<FileListInstance>> getFileList() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            ObjectListing objectListing = ossClient.listObjects(bucketName);
            List<FileListInstance> fileListInstanceList = new ArrayList<>();
            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {

                fileListInstanceList.add(new FileListInstance(
                                objectSummary.getKey(),
                                //文件名称
                                objectSummary.getSize(),
                                //文件大小
                                sdf.format(objectSummary.getLastModified()))
                        //文件最后更新时间
                );
            }
            return new Result<>(fileListInstanceList, 200);
        } catch (Exception e) {
            return new Result<>(null, 500);
        }

    }


    @GetMapping("/getFileView")
    public Result<String> getFileView(@RequestParam("key") String objectName) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            Date expiration = new Date(new Date().getTime() + 3600L * 1000 * 24 * 365 * 10);
            String url = ossClient.generatePresignedUrl(bucketName, objectName, expiration).toString();
            return new Result<>(url,200);
        }catch (Exception e){
            return  new Result<>(null,500);
        }

    }
}
