package casino.noodle.commands.modules;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.module.CommandDescriptor;
import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.ModuleDescriptor;
import casino.noodle.commands.framework.results.CommandMessageResult;
import casino.noodle.commands.framework.results.CommandResult;
import discord4j.core.GatewayDiscordClient;
import reactor.core.publisher.Mono;

@ModuleDescriptor(groups = { "a", "b" }, description = "A test module")
public class TestModule extends CommandModuleBase {
    private final GatewayDiscordClient gatewayDiscordClient;

    public TestModule(GatewayDiscordClient gatewayDiscordClient) {
        this.gatewayDiscordClient = gatewayDiscordClient;
    }

    @CommandDescriptor(aliases = { "ping", "p" }, description = "A test command")
    public Mono<CommandResult> testCommand(CommandContext context, String input) {
        return Mono.just(CommandMessageResult.from("pong " + input + " " + gatewayDiscordClient.getSelfId()));
    }
}
