package casino.noodle;

import discord4j.core.GatewayDiscordClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NoodleBot implements CommandLineRunner {
    private final GatewayDiscordClient client;

    public static void main(String... args) {
        SpringApplication.run(NoodleBot.class, args);
    }

    @Autowired
    public NoodleBot(GatewayDiscordClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) {
        client.onDisconnect().block();
    }
}
