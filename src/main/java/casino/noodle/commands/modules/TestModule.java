package casino.noodle.commands.modules;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.module.CommandModule;
import casino.noodle.commands.framework.results.CommandMessageResult;
import casino.noodle.commands.framework.results.CommandResult;
import reactor.core.publisher.Mono;

@CommandModule
public class TestModule {
    @Command(aliases = "ping")
    public Mono<CommandResult> testCommand(CommandContext context) {
        return Mono.just(CommandMessageResult.from("pong"));
    }
}
