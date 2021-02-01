package casino.noodle.commands.framework;

import casino.noodle.commands.framework.module.Command;

public abstract class CommandContext {
    private final BeanProvider beanProvider;

    Command command;

    public CommandContext(BeanProvider beanProvider) {
        this.beanProvider = beanProvider;
    }

    public BeanProvider beanProvider() {
        return beanProvider;
    }

    public Command command() {
        return command;
    }
}
