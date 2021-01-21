package casino.noodle.handlers;

import com.google.common.eventbus.Subscribe;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class MessageCreateHandler implements Handler<MessageCreateEvent> {
    private final Logger logger = LogManager.getLogger(MessageCreateHandler.class);

    @Subscribe
    public void handleEvent(MessageCreateEvent event) {
        Message msg = event.getMessage();

        if (msg.getAuthor().map(user -> !user.isBot()).orElse(false) && msg.getContent().equals("ping")) {
            msg.getChannel().flatMap(channel -> channel.createMessage("pong!")).block();
        }
    }
}
