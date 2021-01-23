package casino.noodle.handlers;

import casino.noodle.commands.framework.CommandHandler;
import casino.noodle.configurations.CommandConfiguration;
import com.google.common.eventbus.Subscribe;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Component
@Import(CommandConfiguration.class)
public class MessageCreateHandler implements Handler<MessageCreateEvent> {
    private final CommandHandler commandHandler;

    @Autowired
    public MessageCreateHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Subscribe
    public void handleEvent(MessageCreateEvent event) {
        commandHandler.executeAsync(event.getMessage()).subscribe();
    }
}
