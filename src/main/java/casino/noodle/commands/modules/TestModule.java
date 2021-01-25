package casino.noodle.commands.modules;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.annotations.CommandDescription;
import casino.noodle.commands.framework.module.annotations.ModuleDescription;
import casino.noodle.commands.framework.module.annotations.ParameterDescription;
import casino.noodle.commands.framework.results.CommandResult;
import discord4j.core.GatewayDiscordClient;
import reactor.core.publisher.Mono;

@ModuleDescription(groups = { "a", "b" }, description = "A test module")
public class TestModule extends CommandModuleBase {
    private final GatewayDiscordClient gatewayDiscordClient;

    public TestModule(GatewayDiscordClient gatewayDiscordClient) {
        this.gatewayDiscordClient = gatewayDiscordClient;
    }

    @CommandDescription(aliases = { "ping", "p" }, description = "A test command")
    public Mono<CommandResult> testCommand(
            CommandContext context,
            @ParameterDescription(
                name = "input",
                description = "We take an input",
                remainder = true
            ) String input) {
        return reply("pong " + input + " " + gatewayDiscordClient.getSelfId());
    }

    @CommandDescription(aliases = { "ping", "p" }, description = "A test command")
    public Mono<CommandResult> testCommand2(CommandContext context,String input) {
        return reply("pong " + input + " " + gatewayDiscordClient.getSelfId());
    }
}
