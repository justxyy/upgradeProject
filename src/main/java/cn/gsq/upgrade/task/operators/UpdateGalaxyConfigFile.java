package cn.gsq.upgrade.task.operators;

import cn.gsq.task.context.AbstractActuatorAction;
import cn.gsq.upgrade.UpgradeManagerImpl;
import com.yomahub.liteflow.annotation.Operator;
import org.springframework.beans.factory.annotation.Autowired;

@Operator(id = "UpdateGalaxyConfigFile", name = "升级太阿配置文件")
public class UpdateGalaxyConfigFile extends AbstractActuatorAction {

	@Autowired
	UpgradeManagerImpl upgradeManager;

	@Override
	public void operate() {
		upgradeManager.updateGalaxyConfig();
	}

}
