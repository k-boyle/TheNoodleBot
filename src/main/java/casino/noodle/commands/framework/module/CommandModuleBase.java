package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.results.CommandMessageResult;
import casino.noodle.commands.framework.results.CommandResult;
import reactor.core.publisher.Mono;

public abstract class CommandModuleBase {
    protected Mono<CommandResult> reply(String reply) {
        return Mono.just(CommandMessageResult.from(reply));
    }
}
