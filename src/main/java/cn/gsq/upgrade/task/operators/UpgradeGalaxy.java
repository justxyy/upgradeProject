package cn.gsq.upgrade.task.operators;

import cn.gsq.task.context.AbstractActuatorAction;
import cn.gsq.upgrade.UpgradeManagerImpl;
import com.yomahub.liteflow.annotation.Operator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Project : galaxy
 * Class : cn.gsq.upgrade.task.operators.UpgradeGalaxy
 *
 * @author : xyy
 * @date : 2024-07-19 13:56
 * @note : It's not technology, it's art !
 **/
@Operator(id = "UpgradeGalaxy", name = "太阿系统升级")
public class UpgradeGalaxy extends AbstractActuatorAction {
    @Autowired
    UpgradeManagerImpl upgradeManager;

    @Override
    public void operate() {
        upgradeManager.upgradeGalaxy();
    }
}
