package casino.noodle.configurations;

import casino.noodle.commands.framework.CommandHandler;
import casino.noodle.commands.modules.TestModule;
import casino.noodle.commands.modules.TestModule2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfiguration {
    @Bean
    public CommandHandler commandHandler(ApplicationContext applicationContext) {
        return CommandHandler.builder()
            .withModule(TestModule.class)
            .withModule(TestModule2.class)
            .withBeanProvider(applicationContext::getBean)
            .build();
    }
}
