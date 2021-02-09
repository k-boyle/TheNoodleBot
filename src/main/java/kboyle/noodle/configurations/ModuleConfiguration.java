package kboyle.noodle.configurations;

import kboyle.noodle.commands.modules.TestModule2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModuleConfiguration {
    @Bean
    public TestModule2 testModule2() {
        return new TestModule2();
    }
}
