package casino.noodle.commands.modules;

import casino.noodle.commands.NoodleCommandContext;
import casino.noodle.commands.preconditions.FailedPrecondition;
import discord4j.core.GatewayDiscordClient;
import kboyle.octane.core.module.CommandModuleBase;
import kboyle.octane.core.module.annotations.CommandDescription;
import kboyle.octane.core.module.annotations.ModuleDescription;
import kboyle.octane.core.module.annotations.ParameterDescription;
import kboyle.octane.core.results.command.CommandResult;
import reactor.core.publisher.Mono;

@ModuleDescription(groups = { "a", "b" }, description = "A test module")
public class TestModule extends CommandModuleBase<NoodleCommandContext> {
    private final GatewayDiscordClient gatewayDiscordClient;

    public TestModule(GatewayDiscordClient gatewayDiscordClient) {
        this.gatewayDiscordClient = gatewayDiscordClient;
    }

    @CommandDescription(aliases = { "ping", "p" }, description = "A test command")
    public Mono<CommandResult> testCommand(
            @ParameterDescription(
                name = "input",
                description = "We take an input",
                remainder = true
            )
            String input) {
        return message("pong " + input + " " + gatewayDiscordClient.getSelfId());
    }

    @CommandDescription(aliases = { "ping", "p" }, description = "A test command")
    public Mono<CommandResult> testCommand2(String a, String input) {
        return message("pong2 " + input + " " + gatewayDiscordClient.getSelfId());
    }

    @CommandDescription(aliases = { "fail" }, preconditions = FailedPrecondition.class)
    public Mono<CommandResult> testCommand3() {
        return message("this should never reply");
    }
}
