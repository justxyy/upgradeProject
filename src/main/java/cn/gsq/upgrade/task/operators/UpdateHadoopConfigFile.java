package cn.gsq.upgrade.task.operators;

import cn.gsq.task.context.AbstractActuatorAction;
import cn.gsq.upgrade.UpgradeManagerImpl;
import com.yomahub.liteflow.annotation.Operator;
import org.springframework.beans.factory.annotation.Autowired;

@Operator(id = "UpdateHadoopConfigFile", name = "升级hadoop生态配置文件")
public class UpdateHadoopConfigFile extends AbstractActuatorAction {

	@Autowired
	UpgradeManagerImpl upgradeManager;

	@Override
	public void operate() {
		upgradeManager.updateHadoopConfig();
	}

}
