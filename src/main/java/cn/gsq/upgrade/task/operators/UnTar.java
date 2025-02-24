package cn.gsq.upgrade.task.operators;

import cn.gsq.task.context.AbstractActuatorAction;
import cn.gsq.upgrade.UpgradeManagerImpl;
import com.yomahub.liteflow.annotation.Operator;
import org.springframework.beans.factory.annotation.Autowired;

@Operator(id = "unTar", name = "解压tar包")
public class UnTar extends AbstractActuatorAction {
	@Autowired
	UpgradeManagerImpl upgradeManager;

	@Override
	public void operate() throws Exception {
		upgradeManager.unTar();
	}

}
