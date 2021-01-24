package casino.noodle.commands.framework.module.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ParametersDescriptor {
    String name() default "";
    String description() default "";
    boolean remainder() default false;
}
