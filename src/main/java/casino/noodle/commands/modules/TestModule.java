package casino.noodle.commands.modules;

import casino.noodle.commands.NoodleCommandContext;
import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.annotations.CommandDescription;
import casino.noodle.commands.framework.module.annotations.ModuleDescription;
import casino.noodle.commands.framework.module.annotations.ParameterDescription;
import casino.noodle.commands.framework.results.command.CommandResult;
import casino.noodle.commands.preconditions.FailedPrecondition;
import discord4j.core.GatewayDiscordClient;
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
        return reply("pong " + input + " " + gatewayDiscordClient.getSelfId());
    }

    @CommandDescription(aliases = { "ping", "p" }, description = "A test command")
    public Mono<CommandResult> testCommand2(String a, String input) {
        return reply("pong2 " + input + " " + gatewayDiscordClient.getSelfId());
    }

    @CommandDescription(aliases = { "fail" }, preconditions = FailedPrecondition.class)
    public Mono<CommandResult> testCommand3() {
        return reply("this should never reply");
    }
}
