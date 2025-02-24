package cn.galaxy.loader;

import cn.gsq.common.AbstractInformationLoader;
import cn.hutool.core.collection.CollUtil;

import java.util.List;

/**
 * Project : galaxy
 * Class : cn.galaxy.loader.UpgradeInfoLoader
 *
 * @author : gsq
 * @date : 2024-05-08 15:37
 * @note : It's not technology, it's art !
 **/
public class UpgradeInfoLoader extends AbstractInformationLoader {

    /**
     * @Description : agent进程不启动
     * @Param : []
     * @Return : boolean
     * @Author : gsq
     * @Date : 15:21
     * @note : An art cell !
     **/
    public boolean isEnable() {
        return !System.getenv("ROLE").equals("agent");
    }

    /**
     * @Description : Beans扫描路径提供函数
     * @Param : []
     * @Return : java.util.List<java.lang.String>
     * @Author : gsq
     * @Date : 14:45
     * @note : ⚠️ 路径下所有的class将会按照spring boot的规则进行扫描 !
     **/
    @Override
    public List<String> springBeansSupply() {
        return CollUtil.newArrayList("cn.gsq.upgrade.task");
    }

}
