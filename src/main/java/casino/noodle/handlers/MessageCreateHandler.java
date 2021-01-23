package casino.noodle.handlers;

import casino.noodle.commands.framework.CommandHandler;
import casino.noodle.commands.framework.results.CommandMessageResult;
import casino.noodle.configurations.CommandConfiguration;
import com.google.common.eventbus.Subscribe;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Component
@Import(CommandConfiguration.class)
public class MessageCreateHandler implements Handler<MessageCreateEvent> {
    private final CommandHandler commandHandler;
    private final ApplicationContext applicationContext;

    @Autowired
    public MessageCreateHandler(CommandHandler commandHandler, ApplicationContext applicationContext) {
        this.commandHandler = commandHandler;
        this.applicationContext = applicationContext;
    }

    @Subscribe
    public void handleEvent(MessageCreateEvent event) {
        if (event.getMessage().getAuthor().map(User::isBot).orElse(true)) {
            return;
        }

        commandHandler.executeAsync(event.getMessage(), applicationContext).flatMap(result -> {
            CommandMessageResult messageResult = (CommandMessageResult) result;
            return event.getMessage().getChannel().flatMap(channel -> channel.createMessage(messageResult.getMessage()));
        }).subscribe();
    }
}
