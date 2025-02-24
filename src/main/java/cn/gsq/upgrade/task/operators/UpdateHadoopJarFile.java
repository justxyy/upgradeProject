package cn.gsq.upgrade.task.operators;

import cn.gsq.task.context.AbstractActuatorAction;
import cn.gsq.upgrade.UpgradeManagerImpl;
import com.yomahub.liteflow.annotation.Operator;
import org.springframework.beans.factory.annotation.Autowired;

@Operator(id = "UpdateHadoopJarFile", name = "升级hadoop生态jar文件")
public class UpdateHadoopJarFile extends AbstractActuatorAction {

	@Autowired
	UpgradeManagerImpl upgradeManager;

	@Override
	public void operate() {
		upgradeManager.updateHadoopJarFile();
	}

}
