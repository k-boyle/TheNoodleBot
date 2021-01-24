package casino.noodle.commands.framework.module.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandDescriptor {
    String[] aliases();
    String description() default "";
}
