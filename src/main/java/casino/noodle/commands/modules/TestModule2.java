package casino.noodle.commands.modules;

import casino.noodle.commands.NoodleCommandContext;
import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.annotations.CommandDescription;
import casino.noodle.commands.framework.results.command.CommandResult;
import reactor.core.publisher.Mono;

public class TestModule2 extends CommandModuleBase<NoodleCommandContext> {
    @CommandDescription(aliases = { "pong" })
    public Mono<CommandResult> pong() {
        return message("ping");
    }
}
