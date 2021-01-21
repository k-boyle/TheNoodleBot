package casino.noodle;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "noodle")
public class NoodleBotConfig {
    private DiscordConfig discord;

    public DiscordConfig getDiscord() {
        return this.discord;
    }

    public void setDiscord(DiscordConfig discord) {
        this.discord = discord;
    }
    
    public static class DiscordConfig {

        private String token;

        public String getToken() {
            return this.token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
