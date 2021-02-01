package casino.noodle.commands;

import casino.noodle.commands.framework.BeanProvider;
import casino.noodle.commands.framework.CommandContext;

public class NoodleCommandContext extends CommandContext {
    public NoodleCommandContext(BeanProvider beanProvider) {
        super(beanProvider);
    }
}
