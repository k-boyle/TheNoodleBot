package casino.noodle.handlers;

import casino.noodle.commands.framework.BeanProvider;
import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.CommandHandler;
import casino.noodle.commands.framework.results.CommandMessageResult;
import casino.noodle.configurations.CommandConfiguration;
import com.google.common.eventbus.Subscribe;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Component
@Import(CommandConfiguration.class)
public class MessageCreateHandler implements Handler<MessageCreateEvent> {
    private final CommandHandler commandHandler;
    private final BeanProvider beanProvider;

    @Autowired
    public MessageCreateHandler(CommandHandler commandHandler, ApplicationContext applicationContext) {
        this.commandHandler = commandHandler;
        this.beanProvider = applicationContext::getBean;
    }

    @Subscribe
    public void handleEvent(MessageCreateEvent event) {
        Message message = event.getMessage();
        if (message.getAuthor().map(User::isBot).orElse(true)) {
            return;
        }

        CommandContext context = new CommandContext(beanProvider, message);
        commandHandler.executeAsync(message.getContent(), context).flatMap(result -> {
            CommandMessageResult messageResult = (CommandMessageResult) result;
            return message.getChannel().flatMap(channel -> channel.createMessage(messageResult.getMessage()));
        }).subscribe();
    }
}
