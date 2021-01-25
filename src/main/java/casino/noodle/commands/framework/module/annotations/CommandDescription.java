package casino.noodle.commands.framework.module.annotations;

import casino.noodle.commands.framework.module.CommandPrecondition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandDescription {
    String[] aliases();
    String description() default "";
    Class<? extends CommandPrecondition>[] preconditions() default {};
}
