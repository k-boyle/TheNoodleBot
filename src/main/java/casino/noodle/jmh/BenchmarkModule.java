package casino.noodle.jmh;

import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.annotations.CommandDescription;
import casino.noodle.commands.framework.module.annotations.ParameterDescription;
import casino.noodle.commands.framework.results.command.CommandResult;
import reactor.core.publisher.Mono;

public class BenchmarkModule extends CommandModuleBase<BenchmarkCommandContext> {
    @CommandDescription(aliases = "a")
    public Mono<CommandResult> a() {
        return Mono.empty();
    }

    @CommandDescription(aliases = "b")
    public Mono<CommandResult> b(String arg1) {
        return Mono.empty();
    }

    @CommandDescription(aliases = "c")
    public Mono<CommandResult> c(@ParameterDescription(remainder = true) String arg1) {
        return Mono.empty();
    }

    @CommandDescription(aliases = "e")
    public Mono<CommandResult> e(int arg1) {
        return Mono.empty();
    }
}
