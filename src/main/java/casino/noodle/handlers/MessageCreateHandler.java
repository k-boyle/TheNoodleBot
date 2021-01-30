package casino.noodle.handlers;

import casino.noodle.commands.NoodleCommandContext;
import casino.noodle.commands.framework.BeanProvider;
import casino.noodle.commands.framework.CommandHandler;
import casino.noodle.commands.framework.results.FailedResult;
import casino.noodle.commands.framework.results.command.CommandMessageResult;
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
    private final CommandHandler<NoodleCommandContext> commandHandler;
    private final BeanProvider beanProvider;

    @Autowired
    public MessageCreateHandler(CommandHandler<NoodleCommandContext> commandHandler, ApplicationContext applicationContext) {
        this.commandHandler = commandHandler;
        this.beanProvider = applicationContext::getBean;
    }

    @Subscribe
    public void handleEvent(MessageCreateEvent event) {
        Message message = event.getMessage();
        if (message.getAuthor().map(User::isBot).orElse(true)) {
            return;
        }

        NoodleCommandContext context = new NoodleCommandContext(beanProvider);
        commandHandler.executeAsync(message.getContent(), context).flatMap(result -> {
            if (result instanceof FailedResult failed) {
                return message.getChannel().flatMap(channel -> channel.createMessage(failed.reason()));
            } else if (result instanceof CommandMessageResult messageResult) {
                return message.getChannel().flatMap(channel -> channel.createMessage(messageResult.message()));
            }

            return message.getChannel().flatMap(channel -> channel.createMessage(result.getClass().toString()));
        }).subscribe();
    }
}
