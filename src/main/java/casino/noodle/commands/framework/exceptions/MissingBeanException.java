package casino.noodle.commands.framework.exceptions;

public class MissingBeanException extends RuntimeException {
    public MissingBeanException(Class<?> beanType) {
        super(String.format("Missing bean of type %s", beanType));
    }
}
