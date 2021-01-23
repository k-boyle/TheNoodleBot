package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.CommandResult;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class CommandModuleFactory {
    private CommandModuleFactory() {
    }

    public static <T extends CommandModuleBase> CommandModule create(Class<T> clazz) {
        CommandModule.Builder moduleBuilder = CommandModule.builder();
        Module moduleAnnotation = clazz.getAnnotation(Module.class);
        if (moduleAnnotation != null) {
            moduleBuilder
                .withGroups(moduleAnnotation.groups())
                .withDescription(moduleAnnotation.description());
        }

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Command commandAnnotation = method.getAnnotation(Command.class);
            // todo check alias.length > 1 if no group
            if (commandAnnotation != null) {
                boolean valid = isValidCommandSignature(method);
            }
        }


        return moduleBuilder.build();
    }

    // todo unit test this
    private static boolean isValidCommandSignature(Method method) {
        Type returnType = method.getGenericReturnType();
        Class<?>[] parameterTypes = method.getParameterTypes();

        return returnType instanceof ParameterizedType parameterizedType
            && parameterizedType.getRawType() instanceof Class<?> rawTypeClazz
            && rawTypeClazz.isAssignableFrom(Mono.class)
            && parameterizedType.getActualTypeArguments().length == 1
            && parameterizedType.getActualTypeArguments()[0] instanceof Class<?> typeArgumentClazz
            && typeArgumentClazz.isAssignableFrom(CommandResult.class)
            && parameterTypes.length > 0
            && parameterTypes[0].isAssignableFrom(CommandContext.class);
    }
}
