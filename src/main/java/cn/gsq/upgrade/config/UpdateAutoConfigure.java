package cn.gsq.upgrade.config;

import cn.gsq.upgrade.UpgradeManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateAutoConfigure {

    @Bean
    public UpgradeManagerImpl getUpgradeManagerImpl(){
        return new UpgradeManagerImpl();
    }
}
