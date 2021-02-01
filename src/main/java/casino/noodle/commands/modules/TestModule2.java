package casino.noodle.commands.modules;

import casino.noodle.commands.NoodleCommandContext;
import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.annotations.CommandDescription;
import casino.noodle.commands.framework.module.annotations.ModuleDescription;
import casino.noodle.commands.framework.results.command.CommandResult;
import reactor.core.publisher.Mono;

@ModuleDescription(singleton = true, synchronised = true)
public class TestModule2 extends CommandModuleBase<NoodleCommandContext> {
    public TestModule2() {
        System.out.println("constructed");
    }

    @CommandDescription(aliases = { "pong" }, synchronised = true)
    public Mono<CommandResult> pong() {
        return message("ping");
    }
}
