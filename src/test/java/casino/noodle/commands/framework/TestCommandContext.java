package casino.noodle.commands.framework;

import casino.noodle.commands.framework.module.Command;

public class TestCommandContext extends CommandContext {
    public TestCommandContext() {
        super(BeanProvider.get());
    }

    public TestCommandContext(Command command) {
        super(BeanProvider.get());
        super.command = command;
    }
}
