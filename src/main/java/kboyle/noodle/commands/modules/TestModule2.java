package kboyle.noodle.commands.modules;

import kboyle.noodle.commands.NoodleCommandContext;
import kboyle.oktane.core.module.CommandModuleBase;
import kboyle.oktane.core.module.annotations.CommandDescription;
import kboyle.oktane.core.module.annotations.ModuleDescription;
import kboyle.oktane.core.results.command.CommandResult;

@ModuleDescription(singleton = true, synchronised = true)
public class TestModule2 extends CommandModuleBase<NoodleCommandContext> {
    public TestModule2() {
        System.out.println("constructed");
    }

    @CommandDescription(aliases = { "pong" }, synchronised = true)
    public CommandResult pong() {
        return message("ping");
    }
}
