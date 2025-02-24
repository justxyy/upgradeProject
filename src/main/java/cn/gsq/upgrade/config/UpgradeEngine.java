package cn.gsq.upgrade.config;


import java.util.List;

public interface UpgradeEngine {

    /**
     * 检查更新包
     * @return true 可更新  false 不可更新
     */
    Boolean upgradeCheck() throws Exception;

    void updateHadoopConfig(String constructFilePath,String sourceDir, String destinationDir) throws Exception;

    void updateSql(String sqlFilePath) throws Exception;

    void updateApp(String constructFilePath,String sourceDir, String destinationDir) throws Exception;

    void rollbackHadoopConfig(String constructFilePath) throws Exception;

    void rollbackSql(String sqlBackPath) throws Exception;

    void rollbackApp(String constructFilePath,String sourceDir, String destinationDir) throws Exception;

    List<String> getNodeIp();

    void upgradeGalaxy();

}
