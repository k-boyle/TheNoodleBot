package casino.noodle.configurations;

import casino.noodle.NoodleBotConfig;
import casino.noodle.handlers.Handler;
import com.google.common.eventbus.EventBus;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class NoodleBotConfiguration {
    private static final String NOODLE_BUS = "Noodle Bus";

    @Bean
    public GatewayDiscordClient gatewayDiscordClient(NoodleBotConfig config, EventBus eventBus) {
        return DiscordClientBuilder.create(config.getDiscord().getToken())
            .build()
            .login()
            .map(client -> {
                client.getEventDispatcher()
                    .on(Event.class)
                    .subscribe(eventBus::post);
                return client;
            })
            .block();
    }

    @Bean
    public EventBus eventBus(List<Handler<?>> eventHandlers) {
        var eventBus = new EventBus(NOODLE_BUS);
        eventHandlers.forEach(eventBus::register);
        return eventBus;
    }
}
