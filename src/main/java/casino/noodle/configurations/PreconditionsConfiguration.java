package casino.noodle.configurations;

import casino.noodle.commands.preconditions.FailedPrecondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PreconditionsConfiguration {
    @Bean
    public FailedPrecondition failedPrecondition() {
        return new FailedPrecondition();
    }
}
