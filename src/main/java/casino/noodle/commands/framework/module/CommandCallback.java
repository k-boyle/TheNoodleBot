package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.command.CommandResult;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface CommandCallback {
     Mono<CommandResult> execute(CommandContext context, Object[] services, Object[] parameters);
}
