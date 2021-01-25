package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.module.annotations.CommandDescription;
import casino.noodle.commands.framework.module.annotations.ModuleDescription;
import casino.noodle.commands.framework.module.annotations.ParameterDescription;
import casino.noodle.commands.framework.results.CommandResult;
import com.google.common.base.Preconditions;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public final class CommandModuleFactory {
    private CommandModuleFactory() {
    }

    public static <T extends CommandModuleBase> Module create(Class<T> clazz) {
        Module.Builder moduleBuilder = Module.builder();
        ModuleDescription moduleDescriptionAnnotation = clazz.getAnnotation(ModuleDescription.class);
        if (moduleDescriptionAnnotation != null) {
            moduleBuilder
                .withGroups(moduleDescriptionAnnotation.groups())
                .withDescription(moduleDescriptionAnnotation.description());
        }

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            CommandDescription commandDescriptionAnnotation = method.getAnnotation(CommandDescription.class);
            if (commandDescriptionAnnotation != null) {
                Preconditions.checkState(
                    isValidCommandSignature(method),
                    "Method %s has invalid signature",
                    method
                );
                Preconditions.checkState(
                    isValidAliases(moduleDescriptionAnnotation, commandDescriptionAnnotation),
                    "A command must have aliases if the module has no groups"
                );

                Command.Builder commandBuilder = Command.builder()
                    .withAliases(commandDescriptionAnnotation.aliases())
                    .withDescription(commandDescriptionAnnotation.description())
                    .withCallback(createCommandCallback(clazz, method));

                for (Parameter parameter : method.getParameters()) {
                    Class<?> parameterType = parameter.getType();
                    CommandParameter.Builder cmdParameterBuilder = CommandParameter.builder()
                        .withType(parameterType)
                        .withName(parameter.getName());

                    ParameterDescription parameterDescriptionAnnotation = parameter.getAnnotation(ParameterDescription.class);
                    if (parameterDescriptionAnnotation != null) {
                        cmdParameterBuilder
                            .withDescription(parameterDescriptionAnnotation.description())
                            .withRemainder(parameterDescriptionAnnotation.remainder())
                            .withName(parameterDescriptionAnnotation.name());
                    }

                    commandBuilder.withParameter(cmdParameterBuilder.build());
                }

                moduleBuilder.withCommand(commandBuilder.build());
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

    private static boolean isValidAliases(ModuleDescription moduleDescriptionAnnotation, CommandDescription commandDescriptionAnnotation) {
        return commandDescriptionAnnotation.aliases().length > 0
            || moduleDescriptionAnnotation != null
            && moduleDescriptionAnnotation.groups().length > 0;
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
