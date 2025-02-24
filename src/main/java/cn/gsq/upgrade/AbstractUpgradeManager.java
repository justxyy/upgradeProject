package cn.gsq.upgrade;

import cn.gsq.task.TaskActuator;
import cn.gsq.task.pojo.PTStage;
import cn.gsq.upgrade.config.UpgradeEngine;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Slf4j
public abstract class AbstractUpgradeManager implements UpgradeManager{

    @Autowired
    TaskActuator taskActuator;

    @Autowired
    UpgradeEngine engine;

    @Override
    public boolean canUpdate() {
        boolean canUpdate;
        try {
            canUpdate=engine.upgradeCheck();
        } catch (Exception e) {
            log.error("升级文件检测异常："+e.getMessage());
            return false;
        }
        return canUpdate;
    }

    @Override
    public String getReadme() {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Constant.UPGRADE_README_PATH));
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n"); // 将每一行内容追加到StringBuilder中
            }
            reader.close();
        } catch (IOException e) {
            log.error("getReadme异常："+e.getMessage());
        }
        return contentBuilder.toString();
    }

    @Override
    public void update() {
        List<PTStage> stages = CollUtil.newArrayList(
                PTStage.build("更新文件", 100)
        );
        taskActuator.execute("upgradeGalaxy", "太阿升级", "", () -> "太阿系统_太阿升级", stages);
    }

    public abstract void upgradeCheck() throws Exception;

    public abstract void unTar();

    public abstract void backupGalaxy() ;
    public abstract void backupHadoop() ;

    public abstract void updateGalaxyJarFile() ;
    public abstract void updateHadoopJarFile() ;

    public abstract void updateGalaxyConfig() ;
    public abstract void updateHadoopConfig() ;

    public abstract void updateSql() ;

    public abstract void updateApp() ;

    public abstract void rollbackGalaxyJarFile() ;
    public abstract void rollbackHadoopJarFile() ;

    public abstract void rollbackGalaxyConfig() ;
    public abstract void rollbackHadoopConfig() ;

    public abstract void rollbackSql() ;

    public abstract void rollbackApp() ;

    public abstract void updateFile() ;

    public abstract void rollbackFile() ;

/**
 * @Description : 太阿系统升级
 * @Param :
 * @Return :
 * @Author : xyy
 * @Date : 2024/7/19
 * @note : ⚠️ 有错误要抛出来 !
 **/

    public void upgradeGalaxy() {
        engine.upgradeGalaxy();
    }
}
