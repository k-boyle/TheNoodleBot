package casino.noodle.commands.framework.module.annotations;

import casino.noodle.commands.framework.module.Precondition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleDescription {
    String name() default "";
    String[] groups() default {};
    String description() default "";
    Class<? extends Precondition>[] preconditions() default {};
    boolean singleton() default false;
    boolean synchronised() default false;
}
