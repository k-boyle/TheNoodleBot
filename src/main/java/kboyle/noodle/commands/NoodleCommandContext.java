package kboyle.noodle.commands;

import kboyle.oktane.core.BeanProvider;
import kboyle.oktane.core.CommandContext;

public class NoodleCommandContext extends CommandContext {
    public NoodleCommandContext(BeanProvider beanProvider) {
        super(beanProvider);
    }
}
