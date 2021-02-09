package kboyle.noodle.commands.modules;

import discord4j.core.GatewayDiscordClient;
import kboyle.noodle.commands.NoodleCommandContext;
import kboyle.noodle.commands.preconditions.FailedPrecondition;
import kboyle.oktane.core.module.CommandModuleBase;
import kboyle.oktane.core.module.annotations.CommandDescription;
import kboyle.oktane.core.module.annotations.ModuleDescription;
import kboyle.oktane.core.module.annotations.ParameterDescription;
import kboyle.oktane.core.results.command.CommandResult;

@ModuleDescription(groups = { "a", "b" }, description = "A test module")
public class TestModule extends CommandModuleBase<NoodleCommandContext> {
    private final GatewayDiscordClient gatewayDiscordClient;

    public TestModule(GatewayDiscordClient gatewayDiscordClient) {
        this.gatewayDiscordClient = gatewayDiscordClient;
    }

    @CommandDescription(aliases = { "ping", "p" }, description = "A test command")
    public CommandResult testCommand(
            @ParameterDescription(
                name = "input",
                description = "We take an input",
                remainder = true
            )
            String input) {
        return message("pong " + input + " " + gatewayDiscordClient.getSelfId());
    }

    @CommandDescription(aliases = { "ping", "p" }, description = "A test command")
    public CommandResult testCommand2(String a, String input) {
        return message("pong2 " + input + " " + gatewayDiscordClient.getSelfId());
    }

    @CommandDescription(aliases = { "fail" }, preconditions = FailedPrecondition.class)
    public CommandResult testCommand3() {
        return message("this should never reply");
    }
}
