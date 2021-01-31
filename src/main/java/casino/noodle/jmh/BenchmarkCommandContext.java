package casino.noodle.jmh;

import casino.noodle.commands.framework.BeanProvider;
import casino.noodle.commands.framework.CommandContext;

public class BenchmarkCommandContext extends CommandContext {
    public BenchmarkCommandContext() {
        super(BeanProvider.get());
    }
}
