package casino.noodle.commands.framework.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Module {
    String[] groups() default "";
    String description() default "";
}
