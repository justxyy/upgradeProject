package cn.gsq.upgrade.utils;

import cn.gsq.upgrade.Constant;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static cn.hutool.core.io.FileUtil.copyFile;

@Slf4j
public class UpgradeUtil {

    /**
     * 拷贝目录及子文件
     * @param sourceDir
     * @param destinationDir
     */
    public static void backupFile(String sourceDir,String destinationDir){
        // 源目录和目标目录的路径
        Path sourceDirectory = Paths.get(sourceDir);
        Path targetDirectory = Paths.get(destinationDir);
        try {
            // 复制目录及其内容
            copyDirectory(sourceDirectory, targetDirectory);
            log.info("文件备份成功！");
        } catch (IOException e) {
            log.error("文件备份失败：" + e.getMessage());
            throw new RuntimeException("文件备份失败：" + e.getMessage());
        }
    }

    /**
     * 根据更新文件进行更新
     * CONSTRUCT_FILE文件内容格式：文件名 相对路径
     * galaxy jar 例如：xxx.jar /lib(相对galaxy)
     * .....
     * hadoop jar 例如：xxx.jar /v531/hadoop/share/hadoop/hdfs(相对/usr/sdp)
     */
    public static void updateFile(String sourceDir,String constructFileName, String destinationDir) throws IOException {

        //开始替换
        BufferedReader br = new BufferedReader(new FileReader(sourceDir+"/"+constructFileName));
        String line;
        while ((line = br.readLine()) != null) {
            String[] strings = line.split(" ");
            copyFile(sourceDir+"/"+strings[0], destinationDir+strings[1]);
        }
    }

    /**
     * CONSTRUCT_FILE文件内容格式
     * xxx.jar /lib(相对galaxy)
     * .....
     */
    public static void rollbackFile(String constructionFilePath,String backupPath,String sourcePath) {
        //开始替换
        try {
            BufferedReader br = new BufferedReader(new FileReader(constructionFilePath));
            String line;
            while ((line = br.readLine()) != null) {
                String[] strings = line.split(" ");
                if(FileUtil.exist(backupPath+strings[1]+"/"+strings[0])){ //修改的 这样还原
                    copyFile(backupPath+strings[1]+"/"+strings[0],sourcePath+strings[1]);
                }else {//新增的 还原就是直接删除
                    if(FileUtil.exist(sourcePath+strings[1]+"/"+strings[0])){
                        FileUtil.del(sourcePath+strings[1]+"/"+strings[0]);
                    }
                }
            }
        } catch (Exception e) {
            log.error("文件还原异常："+e.getMessage());
            throw new RuntimeException("文件还原异常："+e.getMessage());
        }
    }

    //*****************************************************************
    //文件拷贝
    private static void copyDirectory(Path source, Path target) throws IOException {
        // 复制目录及其内容
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
