package casino.noodle.configurations;

import casino.noodle.commands.NoodleCommandContext;
import casino.noodle.commands.modules.TestModule;
import casino.noodle.commands.modules.TestModule2;
import kboyle.octane.core.CommandHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfiguration {
    @Bean
    public CommandHandler<NoodleCommandContext> commandHandler(ApplicationContext applicationContext) {
        return CommandHandler.builderForContext(NoodleCommandContext.class)
            .withModule(TestModule.class)
            .withModule(TestModule2.class)
            .withBeanProvider(applicationContext::getBean)
            .build();
    }
}
