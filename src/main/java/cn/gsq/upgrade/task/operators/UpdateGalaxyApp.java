package cn.gsq.upgrade.task.operators;

import cn.gsq.task.context.AbstractActuatorAction;
import cn.gsq.upgrade.UpgradeManagerImpl;
import com.yomahub.liteflow.annotation.Operator;
import org.springframework.beans.factory.annotation.Autowired;

@Operator(id = "UpdateGalaxyApp", name = "升级太阿应用商店")
public class UpdateGalaxyApp extends AbstractActuatorAction {
	@Autowired
	UpgradeManagerImpl upgradeManager;

	@Override
	public void operate() {
		upgradeManager.updateApp();
	}

}
