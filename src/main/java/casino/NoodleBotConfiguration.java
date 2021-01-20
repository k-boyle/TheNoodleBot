package casino;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class NoodleBotConfiguration {
    @Bean
    public GatewayDiscordClient gatewayDiscordClient(NoodleBotConfig config) {
        return DiscordClientBuilder.create(config.getDiscord().getToken())
            .build()
            .login()
            .block();
    }
}
