package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.PreconditionResult;
import reactor.core.publisher.Mono;

public interface CommandPrecondition {
    Mono<PreconditionResult> check(CommandContext context);
}
