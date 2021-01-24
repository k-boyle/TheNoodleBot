package casino.noodle.commands.modules;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.annotations.CommandDescriptor;
import casino.noodle.commands.framework.results.CommandResult;
import reactor.core.publisher.Mono;

public class TestModule2 extends CommandModuleBase {
    @CommandDescriptor(aliases = { "pong" })
    public Mono<CommandResult> pong(CommandContext context) {
        return reply("ping");
    }
}
