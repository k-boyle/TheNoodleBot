package kboyle.noodle;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "noodle")
public class NoodleBotConfig {
    private DiscordConfig discord;
    private MarkovConfig markov;

    public DiscordConfig getDiscord() {
        return this.discord;
    }

    public void setDiscord(DiscordConfig discord) {
        this.discord = discord;
    }

    public MarkovConfig getMarkov() {
        return markov;
    }

    public void setMarkov(MarkovConfig markov) {
        this.markov = markov;
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

    public static class MarkovConfig {
        private String file;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }
    }
}
