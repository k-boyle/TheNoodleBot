package casino.noodle.commands.framework.module;

public @interface Command {
    String[] aliases();
    String description() default "";
}
