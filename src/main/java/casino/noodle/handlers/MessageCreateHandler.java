package casino.noodle.handlers;

import casino.noodle.commands.NoodleCommandContext;
import casino.noodle.configurations.CommandConfiguration;
import casino.noodle.configurations.ModuleConfiguration;
import com.google.common.eventbus.Subscribe;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import kboyle.octane.core.BeanProvider;
import kboyle.octane.core.CommandHandler;
import kboyle.octane.core.results.FailedResult;
import kboyle.octane.core.results.command.CommandMessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Component
@Import({CommandConfiguration.class, ModuleConfiguration.class})
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
