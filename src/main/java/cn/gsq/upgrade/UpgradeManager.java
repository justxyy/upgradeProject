package cn.gsq.upgrade;

public interface UpgradeManager {

    //判断升级文件是否可用于升级
    boolean canUpdate();

    //读取升级内容
    String getReadme();

    void update();//更新
}
