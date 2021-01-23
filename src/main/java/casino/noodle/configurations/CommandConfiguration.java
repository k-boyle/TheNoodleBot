package casino.noodle.configurations;

import casino.noodle.commands.framework.CommandHandler;
import casino.noodle.commands.modules.TestModule;
import com.google.common.collect.ImmutableSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfiguration {
    @Bean
    public CommandHandler commandHandler() {
        return CommandHandler.builder()
            .withModule(TestModule.class)
            .withPrefixProvider(ImmutableSet::of)
            .build();
    }
}
