package cn.gsq.upgrade;

import cn.gsq.upgrade.utils.UpgradeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.*;
import java.util.Map;



@Slf4j
public class UpgradeManagerImpl extends AbstractUpgradeManager{

    @Deprecated
    @Override
    public void upgradeCheck() throws Exception {
        if(!engine.upgradeCheck()){
            log.error("更新失败，请检查更新包是否存在，更新版本是否匹配！");
            throw new RuntimeException("更新失败，请检查更新包是否存在，更新版本是否匹配！");
        }
    }

    @Deprecated
    @Override
    public void unTar() {
        // 获取指定文件夹下的所有文件
        File folder = new File(Constant.UPGRADE_PATH);
        File[] files = folder.listFiles();

        // 遍历文件夹中的所有文件
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".tar")) {
                    // 找到以.tar结尾的文件，将其解压到目标文件夹
                    String sourceFilePath = file.getAbsolutePath();
                    extractTar(sourceFilePath, Constant.UPGRADE_PATH+"/upgrade");
                }
            }
        }
    }


    @Deprecated
    @Override
    public void backupGalaxy() {
        UpgradeUtil.backupFile(Constant.GALAXY_SOURCE_PATH,Constant.GALAXY_BACKUP_PATH);
    }

    @Deprecated
    @Override
    public void backupHadoop() {
        UpgradeUtil.backupFile(Constant.HADOOP_SOURCE_PATH,Constant.HADOOP_BACKUP_PATH);
    }

    /**
     * GalaxyJar  CONSTRUCT_FILE文件内容格式
     * xxx.jar /lib(相对galaxy)
     * .....
     * 注：支持增加jar 或者修改jar 不支持删除
     */
    @Deprecated
    @Override
    public void updateGalaxyJarFile() {
        if(!isFileExist(Constant.UPDATE_GALAXY_JAR_PATH,Constant.CONSTRUCT_FILE))
            return;
        try {
            UpgradeUtil.updateFile(Constant.UPDATE_GALAXY_JAR_PATH,Constant.CONSTRUCT_FILE,Constant.GALAXY_SOURCE_PATH);
        } catch (IOException e) {
            rollbackGalaxyJarFile();
            log.error("太阿jar文件更新失败！");
            throw new RuntimeException("太阿jar文件更新失败！");
        }
    }

    /**
     * Hadoop 生态Jar  CONSTRUCT_FILE文件内容格式
     * xxx.jar /v531/hadoop/share/hadoop/hdfs(相对/usr/sdp)
     * xxx.jar /v530/flink/share/(相对/usr/sdp)
     * .....
     * 注：支持增加jar 或者修改jar 不支持删除
     */
    @Deprecated
    @Override
    public void updateHadoopJarFile() {
        if(!isFileExist(Constant.UPDATE_HADOOP_JAR_PATH,Constant.CONSTRUCT_FILE))
            return;
        try {
            UpgradeUtil.updateFile(Constant.UPDATE_HADOOP_JAR_PATH,Constant.CONSTRUCT_FILE,Constant.HADOOP_SOURCE_PATH);
        } catch (IOException e) {
            rollbackHadoopJarFile();
            log.error("hadoop生态jar文件更新失败！");
            throw new RuntimeException("hadoop生态jar文件更新失败！");
        }
    }

    /**
     * 更新太阿配置文件
     * 在/usr/galaxy/upgrade/taie/conf/目录中 放需要更新的配置文件如 application-server.yml application-agent.yml及CONSTRUCT_FILE
     * CONSTRUCT_FILE 文件格式：配置文件名 目的路径
     * 例如：application-server.yml /conf(相对galaxy)
     * 注 yml文件中的配置项如果需要用旧值  则用 x 占位
     */
    @Deprecated
    @Override
    public void updateGalaxyConfig() {
        if(!isFileExist(Constant.UPDATE_GALAXY_CONFIG_PATH,Constant.CONSTRUCT_FILE))
            return;
        try {
            //开始替换
            BufferedReader br = new BufferedReader(new FileReader(Constant.UPDATE_GALAXY_CONFIG_PATH+"/"+Constant.CONSTRUCT_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                String[] strings = line.split(" ");
                //1、读取要更新的yml
                //2、读取旧的yml
                //3、将旧的yml中的值放到新的中
                //4、写到yml文件
                updateYml(Constant.UPDATE_GALAXY_CONFIG_PATH+"/"+strings[0],Constant.GALAXY_SOURCE_PATH+strings[1]+"/"+strings[0]);
            }
        } catch (Exception e) {
            rollbackGalaxyConfig();
            log.error("galaxy 配置文件更新失败！");
            throw new RuntimeException("galaxy 配置文件更新失败！");
        }
    }

    /**
     * 1、升级目录文件 /usr/galaxy/upgrade/sdp/conf/construct.txt
     * 2、所有需要升级的配置文件放在/usr/galaxy/upgrade/sdp/conf下
     * 3、construct.txt格式 配置文件名 服务名 分支名
     * 例如：hdfs-site.xml HDFS default
     */
    @Deprecated
    @Override
    public void updateHadoopConfig() {
        if(!isFileExist(Constant.UPDATE_HADOOP_CONFIG_PATH,Constant.CONSTRUCT_FILE))
            return;
        try {
            engine.updateHadoopConfig(Constant.UPDATE_HADOOP_CONFIG_PATH+"/"+Constant.CONSTRUCT_FILE,Constant.UPDATE_HADOOP_CONFIG_PATH,Constant.HADOOP_SOURCE_PATH);
        } catch (Exception e) {
            rollbackHadoopConfig();
            log.error("hadoop生态 配置文件更新失败！");
            throw new RuntimeException("hadoop生态 配置文件更新失败！");
        }
    }

    @Deprecated
    @Override
    public void updateSql() {
        if(!isFileExist(Constant.UPDATE_SQL_PATH,Constant.SQL_FILE))
            return;
        try {
            engine.updateSql(Constant.UPDATE_SQL_PATH+"/"+Constant.SQL_FILE);
        } catch (Exception e) {
            rollbackSql();
            log.error("galaxy sql 文件更新失败！");
            throw new RuntimeException("galaxy sql 文件更新失败！");
        }
    }

    @Deprecated
    @Override
    public void updateApp() {
        if(!isFileExist(Constant.UPDATE_OTHERS_PATH,Constant.CONSTRUCT_FILE))
            return;
        try {
            engine.updateApp("xx","xx","xx");
        } catch (Exception e) {
            rollbackApp();
            log.error("galaxy 应用商店更新失败！");
            throw new RuntimeException("galaxy 应用商店更新失败！");
        }
    }

    @Deprecated
    @Override
    public void rollbackGalaxyJarFile() {
        if(!isFileExist(Constant.UPDATE_GALAXY_JAR_PATH,Constant.CONSTRUCT_FILE))
            return;
        try {
            UpgradeUtil.rollbackFile(Constant.UPDATE_GALAXY_JAR_PATH+"/"+Constant.CONSTRUCT_FILE,Constant.GALAXY_BACKUP_PATH,Constant.GALAXY_SOURCE_PATH);
        } catch (Exception e) {
            log.error("galaxy jar文件回滚 失败！");
            throw new RuntimeException("galaxy jar文件回滚 失败！");
        }
    }

    @Deprecated
    @Override
    public void rollbackHadoopJarFile() {
        if(!isFileExist(Constant.UPDATE_HADOOP_JAR_PATH,Constant.CONSTRUCT_FILE))
            return;
        try {
            UpgradeUtil.rollbackFile(Constant.UPDATE_HADOOP_JAR_PATH+"/"+Constant.CONSTRUCT_FILE,Constant.HADOOP_BACKUP_PATH,Constant.HADOOP_SOURCE_PATH);
        } catch (Exception e) {
            log.error("hadoop生态 jar文件回滚 失败！");
            throw new RuntimeException("hadoop生态 jar文件回滚 失败！");
        }
    }

    @Deprecated
    @Override
    public void rollbackGalaxyConfig() {
        if(!isFileExist(Constant.UPDATE_GALAXY_CONFIG_PATH,Constant.CONSTRUCT_FILE))
            return;
        try {
            UpgradeUtil.rollbackFile(Constant.UPDATE_GALAXY_CONFIG_PATH+"/"+Constant.CONSTRUCT_FILE,Constant.GALAXY_BACKUP_PATH,Constant.GALAXY_SOURCE_PATH);
        } catch (Exception e) {
            log.error("galaxy配置文件回滚 失败！");
            throw new RuntimeException("galaxy配置文件回滚 失败！");
        }
    }

    @Deprecated
    @Override
    public void rollbackHadoopConfig() {
        if(!isFileExist(Constant.UPDATE_HADOOP_CONFIG_PATH,Constant.CONSTRUCT_FILE))
            return;
        try {
            engine.rollbackHadoopConfig(Constant.UPDATE_HADOOP_CONFIG_PATH+"/"+Constant.CONSTRUCT_FILE);
        } catch (Exception e) {
            log.error("hadoop生态 配置文件回滚 失败！");
            throw new RuntimeException("hadoop生态 配置文件回滚 失败！");
        }
    }

    @Deprecated
    @Override
    public void rollbackSql() {
        if(!isFileExist(Constant.UPDATE_SQL_PATH,Constant.SQL_FILE))
            return;
        try {
            engine.rollbackSql(Constant.SQL_BACKUP_PATH);
        } catch (Exception e) {
            log.error("galaxy sql文件回滚 失败！");
            throw new RuntimeException("galaxy sql文件回滚 失败！",e.getCause());
        }
    }

    @Deprecated
    @Override
    public void rollbackApp() {
        try {
            engine.rollbackApp("xx","xx","xx");
        } catch (Exception e) {
            log.error("galaxy 应用商店文件回滚 失败！");
            throw new RuntimeException("galaxy 应用商店文件回滚 失败！");
        }
    }

    /**
     * construct.txt
     * 文件名 目的路径
     */
    @Deprecated
    @Override
    public void updateFile() {
        if(!isFileExist(Constant.UPDATE_FILE_PATH,Constant.CONSTRUCT_FILE))
            return;
        try (BufferedReader reader = new BufferedReader(new FileReader(Constant.UPDATE_FILE_PATH+"/"+Constant.CONSTRUCT_FILE))) {
            String line;
            // 逐行读取文件内容并打印到控制台
            while ((line = reader.readLine()) != null) {
                String[] s = line.split(" ");
                Path sourcePath = Paths.get(Constant.UPDATE_FILE_PATH+"/"+s[0]);
                Path targetPath = Paths.get(s[1]);

                //scp 分发到节点
                scpFile(sourcePath,targetPath);
            }
        } catch (Exception e) {
            rollbackFile();
            log.error("updateFile 失败！");
            throw new RuntimeException("updateFile 失败！");
        }
    }

    @Deprecated
    @Override
    public void rollbackFile() {
        if(!isFileExist(Constant.UPDATE_FILE_PATH,Constant.CONSTRUCT_FILE))
            return;
        try (BufferedReader reader = new BufferedReader(new FileReader(Constant.UPDATE_FILE_PATH+"/"+Constant.CONSTRUCT_FILE))) {
            String line;
            // 逐行读取文件内容并打印到控制台
            while ((line = reader.readLine()) != null) {
                String[] s = line.split(" ");

                Path sourcePath = Paths.get("/tmp/backup/"+s[1]);
                Path targetPath = Paths.get(s[1]);

                //scp 分发到节点
                scpFile(sourcePath,targetPath);
            }
        } catch (Exception e) {
            log.error("rollbackFile 失败！");
            throw new RuntimeException("rollbackFile 失败！");
        }
    }

    private void scpFile(Path sourcePath, Path targetPath) throws Exception{
        for (String ip : engine.getNodeIp()) {
            String destination = "root@"+ip+":"+targetPath;
            // 构建scp命令
            String[] command = {"scp", String.valueOf(sourcePath), destination};

            // 执行scp命令
            Process process = Runtime.getRuntime().exec(command);

            // 获取命令执行的输出流
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // 读取命令执行的输出
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("文件分发失败");
            }
        }
    }

    //判断文件是否存在
    private Boolean isFileExist(String updateFilePath, String constructFile) {
        Path directoryPath = Paths.get(updateFilePath);
        Path filePathInDirectory = directoryPath.resolve(constructFile);

        if (Files.isDirectory(directoryPath) && Files.exists(filePathInDirectory)) {
            return true;
        } else {
            log.warn(updateFilePath+"/"+constructFile+"不存在");
            return false;
        }

    }

    private static void mergeConfigs(Map<String, Object> base, Map<String, Object> overlay) {
        for (Map.Entry<String, Object> entry : overlay.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (base.containsKey(key) && base.get(key) instanceof Map && value instanceof Map) {
                // 递归合并
                mergeConfigs((Map<String, Object>) base.get(key), (Map<String, Object>) value);
            }
            if(base.containsKey(key) && base.get(key).equals("x")){
                base.put(key, value);
            }

        }
    }

    private void updateYml(String sourcePath, String destinationPath) throws Exception{
        InputStream input = new FileInputStream(sourcePath);
        Yaml yaml = new Yaml();
        Map<String, Object> newYml = yaml.load(input);

        InputStream input1 = new FileInputStream(destinationPath);
        Yaml yaml1 = new Yaml();
        Map<String, Object> oldYml = yaml1.load(input1);

        mergeConfigs(newYml, oldYml);//把old合并到new

        FileWriter writer = new FileWriter(destinationPath);
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml2 = new Yaml(options);
        yaml2.dump(newYml, writer);
    }

    // 解压tar文件
    public void extractTar(String sourceFilePath, String destinationFolderPath) {
        try {
            // 创建输出文件夹
            File destinationFolder = new File(destinationFolderPath);
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }

            // 创建TarArchiveInputStream
            FileInputStream fileInputStream = new FileInputStream(sourceFilePath);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(bufferedInputStream);

            ArchiveEntry entry;
            // 逐个解压条目
            while ((entry = tarArchiveInputStream.getNextEntry()) != null) {
                File outputFile = new File(destinationFolder, entry.getName());
                if (entry.isDirectory()) {
                    // 如果是文件夹，则创建文件夹
                    if (!outputFile.exists()) {
                        outputFile.mkdirs();
                    }
                } else {
                    // 如果是文件，则写入文件
                    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = tarArchiveInputStream.read(buffer)) != -1) {
                        bufferedOutputStream.write(buffer, 0, len);
                    }
                    bufferedOutputStream.close();
                }
            }
            tarArchiveInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {




        //******************************test updateYML****************************
//        try{
//            InputStream input = new FileInputStream("C:\\Users\\xuyy\\Desktop\\a.yml");
//            Yaml yaml = new Yaml();
//            Map<String, Object> a = yaml.load(input);
////            System.out.println(JSONUtil.toJsonPrettyStr(yamlData));
//
//            InputStream input1 = new FileInputStream("C:\\Users\\xuyy\\Desktop\\b.yml");
//            Yaml yaml1 = new Yaml();
//            Map<String, Object> b = yaml1.load(input1);
//
//            mergeConfigs(b, a);
//
//            FileWriter writer = new FileWriter("C:\\Users\\xuyy\\Desktop\\b.yml");
//            DumperOptions options = new DumperOptions();
//            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//
//            Yaml yaml2 = new Yaml(options);
//            yaml2.dump(b, writer);
//            System.out.println("------------------");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //**************************TEST LOAD PROPERTIES***********************************
//        Properties properties = new Properties();
//        try{
//            FileReader reader = new FileReader("C:\\Users\\xuyy\\Desktop\\7.9.5.1705860181-stable-v5.3.1\\7.9.5.1705860181-stable-v5.3.1\\upgrade.properties");
//            // 从文件中加载属性
//            properties.load(reader);
//
//            // 获取属性值
//            String url = properties.getProperty("sdp.version");
//            String username = properties.getProperty("base.galaxy.version");
//            String password = properties.getProperty("base.galaxy.mold");
//
//            // 打印属性值
//            System.out.println("Database URL: " + url);
//            System.out.println("Username: " + username);
//            System.out.println("Password: " + password);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
