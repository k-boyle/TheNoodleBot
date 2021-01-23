package casino.noodle.commands.framework.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String[] aliases();
    String description() default "";
}
