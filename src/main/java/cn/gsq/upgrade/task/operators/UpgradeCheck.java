package cn.gsq.upgrade.task.operators;

import cn.gsq.task.context.AbstractActuatorAction;
import cn.gsq.task.context.CalculateContext;
import cn.gsq.upgrade.UpgradeManagerImpl;
import cn.hutool.core.map.MapUtil;
import com.yomahub.liteflow.annotation.Operator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Operator(id = "UpgradeCheck", name = "太阿升级检查")
public class UpgradeCheck extends AbstractActuatorAction {
	@Autowired
	UpgradeManagerImpl upgradeManager;

	@Override
	public void operate() throws Exception {
		upgradeManager.upgradeCheck();
	}

}
