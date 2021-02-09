package kboyle.noodle.configurations;

import kboyle.noodle.commands.preconditions.FailedPrecondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PreconditionsConfiguration {
    @Bean
    public FailedPrecondition failedPrecondition() {
        return new FailedPrecondition();
    }
}
