package casino.noodle.commands.modules;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.Module;
import casino.noodle.commands.framework.results.CommandMessageResult;
import casino.noodle.commands.framework.results.CommandResult;
import reactor.core.publisher.Mono;

@Module(groups = { "a", "b" }, description = "A test module")
public class TestModule extends CommandModuleBase {
    @Command(aliases = { "ping", "p" }, description = "A test command")
    public Mono<CommandResult> testCommand(CommandContext context, String input) {
        return Mono.just(CommandMessageResult.from("pong " + input));
    }

    @Command(aliases = "test1")
    public void command() {
    }

    @Command(aliases = "test2")
    public void command(CommandContext context) {
    }

    @Command(aliases = "test2")
    public Mono<CommandResult> command2() {
        return Mono.empty();
    }

    @Command(aliases = "test3")
    public Mono<Void> command3() {
        return Mono.empty();
    }
}
