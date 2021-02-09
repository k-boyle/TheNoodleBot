package kboyle.noodle.handlers;

import com.google.common.eventbus.Subscribe;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import kboyle.noodle.commands.NoodleCommandContext;
import kboyle.noodle.configurations.CommandConfiguration;
import kboyle.noodle.configurations.ModuleConfiguration;
import kboyle.oktane.core.BeanProvider;
import kboyle.oktane.core.CommandHandler;
import kboyle.oktane.core.results.FailedResult;
import kboyle.oktane.core.results.Result;
import kboyle.oktane.core.results.command.CommandMessageResult;
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
        Result result = commandHandler.execute(message.getContent(), context);
        if (result instanceof FailedResult failed) {
            message.getChannel().flatMap(channel -> channel.createMessage(failed.reason())).subscribe();
        } else if (result instanceof CommandMessageResult messageResult) {
            message.getChannel().flatMap(channel -> channel.createMessage(messageResult.message())).subscribe();
        }
    }
}
