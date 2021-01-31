package casino.noodle.commands.framework;

public class TestCommandContext extends CommandContext {
    public TestCommandContext() {
        super(BeanProvider.get());
    }
}
