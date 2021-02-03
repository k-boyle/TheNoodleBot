package casino.noodle.commands.modules;

import casino.noodle.commands.NoodleCommandContext;
import kboyle.octane.core.module.CommandModuleBase;
import kboyle.octane.core.module.annotations.CommandDescription;
import kboyle.octane.core.module.annotations.ModuleDescription;
import kboyle.octane.core.results.command.CommandResult;
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
