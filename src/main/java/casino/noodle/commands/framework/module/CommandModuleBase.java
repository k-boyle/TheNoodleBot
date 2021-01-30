package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.command.CommandMessageResult;
import casino.noodle.commands.framework.results.command.CommandResult;
import reactor.core.publisher.Mono;

public abstract class CommandModuleBase<T extends CommandContext> {
    private T context;

    protected T context() {
        return context;
    }

    public void setContext(T context) {
        this.context = context;
    }

    protected Mono<CommandResult> reply(String reply) {
        return Mono.just(CommandMessageResult.from(context.command(), reply));
    }

    protected Mono<CommandResult> empty() {
        return Mono.empty();
    }
}
