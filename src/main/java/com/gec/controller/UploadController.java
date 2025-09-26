package com.gec.controller;

import com.gec.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadController {
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.upload.url}")
    private String uploadUrl;
    
    /**
     * 图片上传
     */
    @PostMapping({"", "/image"})
    public R<String> uploadImage(@RequestParam("file") MultipartFile file) {
        System.out.println("=== 开始文件上传调试 ===");
        
        if (file.isEmpty()) {
            System.out.println("ERROR: 上传文件为空");
            return R.error("上传文件不能为空");
        }
        
        try {
            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            System.out.println("原始文件名: " + originalFilename);
            System.out.println("文件大小: " + file.getSize() + " bytes");
            System.out.println("文件类型: " + file.getContentType());
            
            if (originalFilename == null || !isImageFile(originalFilename)) {
                System.out.println("ERROR: 不支持的文件格式");
                return R.error("只支持图片文件格式");
            }
            
            // 生成唯一文件名
            String extension = getFileExtension(originalFilename);
            String fileName = UUID.randomUUID().toString() + extension;
            System.out.println("生成的文件名: " + fileName);
            
            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            System.out.println("项目根目录: " + projectRoot);
            
            // 如果当前目录已经是backend，直接使用；否则添加backend路径
            Path uploadDir;
            if (projectRoot.endsWith("backend")) {
                uploadDir = Paths.get(projectRoot, "target", "classes", "static", "uploads");
            } else {
                uploadDir = Paths.get(projectRoot, "backend", "target", "classes", "static", "uploads");
            }
            System.out.println("上传目录路径: " + uploadDir.toString());
            System.out.println("上传目录是否存在: " + Files.exists(uploadDir));
            
            // 如果目录不存在则创建
            if (!Files.exists(uploadDir)) {
                System.out.println("创建上传目录...");
                Files.createDirectories(uploadDir);
                System.out.println("目录创建后是否存在: " + Files.exists(uploadDir));
            }
            
            // 保存文件
            Path filePath = uploadDir.resolve(fileName);
            System.out.println("完整文件路径: " + filePath.toString());
            System.out.println("开始保存文件...");
            
            file.transferTo(filePath.toFile());
            System.out.println("文件保存完成");
            
            // 验证文件是否保存成功
            boolean fileExists = Files.exists(filePath);
            System.out.println("文件是否存在: " + fileExists);
            if (fileExists) {
                System.out.println("保存的文件大小: " + Files.size(filePath) + " bytes");
            }
            
            if (!fileExists) {
                System.out.println("ERROR: 文件保存失败！");
                return R.error("文件保存失败");
            }
            
            // 同时保存到 resources 目录（用于开发环境）
            Path resourcesDir;
            if (projectRoot.endsWith("backend")) {
                resourcesDir = Paths.get(projectRoot, "src", "main", "resources", "static", "uploads");
            } else {
                resourcesDir = Paths.get(projectRoot, "backend", "src", "main", "resources", "static", "uploads");
            }
            System.out.println("Resources目录路径: " + resourcesDir.toString());
            System.out.println("Resources目录是否存在: " + Files.exists(resourcesDir));
            
            if (!Files.exists(resourcesDir)) {
                System.out.println("创建Resources目录...");
                Files.createDirectories(resourcesDir);
                System.out.println("Resources目录创建后是否存在: " + Files.exists(resourcesDir));
            }
            
            Path resourcesFilePath = resourcesDir.resolve(fileName);
            System.out.println("Resources文件路径: " + resourcesFilePath.toString());
            System.out.println("复制文件到Resources目录...");
            Files.copy(filePath, resourcesFilePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("复制完成");
            
            // 返回文件访问URL
            String fileUrl = uploadUrl + "/" + fileName;
            System.out.println("文件保存成功: " + filePath.toString());
            System.out.println("Resources文件路径: " + resourcesFilePath.toString());
            System.out.println("文件访问URL: " + fileUrl);
            System.out.println("=== 文件上传调试结束 ===");
            return R.success(fileUrl);
            
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 文件访问
     */
    @GetMapping("/{fileName}")
    public void getFile(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        try {
            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            Path targetFilePath = Paths.get(projectRoot, "backend", "target", "classes", "static", "uploads", fileName);
            Path filePath = null;
            
            if (Files.exists(targetFilePath)) {
                filePath = targetFilePath;
            } else {
                // 如果 target 目录没有，尝试从 resources 目录读取
                Path resourcesFilePath = Paths.get(projectRoot, "backend", "src", "main", "resources", "static", "uploads", fileName);
                if (Files.exists(resourcesFilePath)) {
                    filePath = resourcesFilePath;
                }
            }
            
            if (filePath == null || !Files.exists(filePath)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 设置响应头
            String contentType = getContentType(fileName);
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            
            // 输出文件内容
            Files.copy(filePath, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 检查是否为图片文件
     */
    private boolean isImageFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return extension.equals(".jpg") || extension.equals(".jpeg") || 
               extension.equals(".png") || extension.equals(".gif") || 
               extension.equals(".bmp") || extension.equals(".webp");
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }
    
    /**
     * 根据文件扩展名获取Content-Type
     */
    private String getContentType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        switch (extension) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".bmp":
                return "image/bmp";
            case ".webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }
}