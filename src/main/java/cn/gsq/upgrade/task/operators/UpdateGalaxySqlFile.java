package cn.gsq.upgrade.task.operators;

import cn.gsq.task.context.AbstractActuatorAction;
import cn.gsq.upgrade.UpgradeManagerImpl;
import com.yomahub.liteflow.annotation.Operator;
import org.springframework.beans.factory.annotation.Autowired;

@Operator(id = "UpdateGalaxySqlFile", name = "升级太阿sql文件")
public class UpdateGalaxySqlFile extends AbstractActuatorAction {

	@Autowired
	UpgradeManagerImpl upgradeManager;

	@Override
	public void operate() {
		upgradeManager.updateSql();
	}

}
