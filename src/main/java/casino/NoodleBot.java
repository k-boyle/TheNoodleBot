package casino;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NoodleBot implements CommandLineRunner {
    @Autowired
    private GatewayDiscordClient client;

    public static void main(String... args) {
        SpringApplication.run(NoodleBot.class, args);
    }

    // todo: logging
    // todo: JITPACK!!!!!
    // todo: Data source (psql)
    // todo: local conf (YAML/property)
    // todo: debug/prod runmode

    @Override
    public void run(String... args) {
        client.getEventDispatcher().on(ReadyEvent.class)
            .subscribe(event -> {
                final User self = event.getSelf();
                System.out.println(String.format(
                    "Logged in as %s#%s", self.getUsername(), self.getDiscriminator()
                ));
            });

        //Steffen=116203556947623936L
        //Kieran=84291986575613952L
        //Hare=366748117569110016L
        //Noodle=801510967211982850L

        client.getEventDispatcher().on(MessageCreateEvent.class)
            .map(MessageCreateEvent::getMessage)
            .filter(message -> message.getAuthor()
                .map(user -> user.getId().asLong() == 116203556947623936L).orElse(false))
            .filter(message -> message.getUserMentionIds().contains(Snowflake.of(84291986575613952L)))
            .flatMap(Message::getChannel)
            .flatMap(channel -> channel.createMessage("Pong!"))
            .subscribe();

        client.onDisconnect().block();
    }
}
