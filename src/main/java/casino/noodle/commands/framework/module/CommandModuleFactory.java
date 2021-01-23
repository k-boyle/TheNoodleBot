package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.CommandResult;
import com.google.common.base.Preconditions;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public final class CommandModuleFactory {
    private CommandModuleFactory() {
    }

    public static <T extends CommandModuleBase> Module create(Class<T> clazz) {
        Module.Builder moduleBuilder = Module.builder();
        ModuleDescriptor moduleDescriptorAnnotation = clazz.getAnnotation(ModuleDescriptor.class);
        if (moduleDescriptorAnnotation != null) {
            moduleBuilder
                .withGroups(moduleDescriptorAnnotation.groups())
                .withDescription(moduleDescriptorAnnotation.description());
        }

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            CommandDescriptor commandDescriptorAnnotation = method.getAnnotation(CommandDescriptor.class);
            if (commandDescriptorAnnotation != null) {
                Preconditions.checkState(
                    isValidCommandSignature(method),
                    "Method %s has invalid signature",
                    method
                );
                Preconditions.checkState(
                    isValidAliases(moduleDescriptorAnnotation, commandDescriptorAnnotation),
                    "A command must have aliases if the module has no groups"
                );

                moduleBuilder.withCommand(
                    Command.builder()
                        .withAliases(commandDescriptorAnnotation.aliases())
                        .withDescription(commandDescriptorAnnotation.description())
                        .withCallback(createCommandCallback(clazz, method))
                        .build()
                );
            }
        }

        return moduleBuilder.build();
    }

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

    private static boolean isValidAliases(ModuleDescriptor moduleDescriptorAnnotation, CommandDescriptor commandDescriptorAnnotation) {
        return commandDescriptorAnnotation.aliases().length > 0
            || moduleDescriptorAnnotation != null
            && moduleDescriptorAnnotation.groups().length > 0;
    }

    @SuppressWarnings("unchecked")
    private static CommandCallback createCommandCallback(Class<?> moduleClass, Method method) {
        return (commandContext, parameters) -> {
            try {
                // todo method handles
                ApplicationContext applicationContext = commandContext.getApplicationContext();
                Constructor<?> constructor = moduleClass.getConstructors()[0];
                Object[] constructorArguments = Arrays.stream(constructor.getParameterTypes())
                    .map(applicationContext::getBean)
                    .toArray();
                Object module = constructor.newInstance(constructorArguments);
                Object[] parametersWithContext = new Object[1 + parameters.length];
                parametersWithContext[0] = commandContext;
                System.arraycopy(parameters, 0, parametersWithContext, 1, parameters.length);
                return (Mono<CommandResult>) method.invoke(module, parametersWithContext);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                return Mono.error(e);
            }
        };
    }
}
