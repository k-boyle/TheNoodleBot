package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.BeanProvider;
import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.module.annotations.CommandDescription;
import casino.noodle.commands.framework.module.annotations.ModuleDescription;
import casino.noodle.commands.framework.module.annotations.ParameterDescription;
import casino.noodle.commands.framework.results.CommandResult;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public final class CommandModuleFactory {
    private static final String SPACE = " ";

    private CommandModuleFactory() {
    }

    public static <T extends CommandModuleBase> Module create(Class<T> clazz, BeanProvider beanProvider) {
        Module.Builder moduleBuilder = Module.builder()
            .withName(clazz.getSimpleName());
        ModuleDescription moduleDescriptionAnnotation = clazz.getAnnotation(ModuleDescription.class);
        if (moduleDescriptionAnnotation != null) {
            if (!Strings.isNullOrEmpty(moduleDescriptionAnnotation.name())) {
                moduleBuilder.withName(moduleDescriptionAnnotation.name());
            }

            if (!Strings.isNullOrEmpty(moduleDescriptionAnnotation.description())) {
                moduleBuilder.withDescription(moduleDescriptionAnnotation.description());
            }

            for (String group : moduleDescriptionAnnotation.groups()) {
               moduleBuilder.withGroup(group);
            }

            Class<? extends Precondition>[] preconditionClazzes = moduleDescriptionAnnotation.preconditions();
            for (Class<? extends Precondition> preconditionClazz : preconditionClazzes) {
                Precondition precondition = Preconditions.checkNotNull(
                    beanProvider.getBean(preconditionClazz),
                    "A precondition of type %s must be added to the bean provider",
                    preconditionClazz
                );
                moduleBuilder.withPrecondition(precondition);
            }
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
                    .withName(method.getName())
                    .withCallback(createCommandCallback(clazz, method));

                for (String alias : commandDescriptionAnnotation.aliases()) {
                    commandBuilder.withAliases(alias);
                }

                if (!Strings.isNullOrEmpty(commandDescriptionAnnotation.name())) {
                    commandBuilder.withName(commandDescriptionAnnotation.name());
                }

                if (!Strings.isNullOrEmpty(commandDescriptionAnnotation.description())) {
                    commandBuilder.withDescription(commandDescriptionAnnotation.description());
                }

                Class<? extends Precondition>[] preconditionClazzes = commandDescriptionAnnotation.preconditions();
                for (Class<? extends Precondition> preconditionClazz : preconditionClazzes) {
                    Precondition precondition = Preconditions.checkNotNull(
                        beanProvider.getBean(preconditionClazz),
                        "A precondition of type %s must be added to the bean provider",
                        preconditionClazz
                    );
                    commandBuilder.withPrecondition(precondition);
                }

                Parameter[] parameters = method.getParameters();
                for (int i = 1; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
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

                moduleBuilder.withCommand(commandBuilder);
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

    private static boolean isValidAliases(
            ModuleDescription moduleDescriptionAnnotation,
            CommandDescription commandDescriptionAnnotation) {
        for (String alias : commandDescriptionAnnotation.aliases()) {
            Preconditions.checkState(!alias.contains(SPACE), "Alias %s contains a space", alias);
        }

        return commandDescriptionAnnotation.aliases().length > 0
            || moduleDescriptionAnnotation != null
            && moduleDescriptionAnnotation.groups().length > 0;
    }

    @SuppressWarnings("unchecked")
    private static CommandCallback createCommandCallback(Class<?> moduleClass, Method method) {
        return (commandContext, parameters) -> {
            try {
                // todo method handles
                BeanProvider applicationContext = commandContext.beanProvider();
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
