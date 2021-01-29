package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.BeanProvider;
import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.exceptions.MissingBeanException;
import casino.noodle.commands.framework.module.annotations.CommandDescription;
import casino.noodle.commands.framework.module.annotations.ModuleDescription;
import casino.noodle.commands.framework.module.annotations.ParameterDescription;
import casino.noodle.commands.framework.results.CommandResult;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

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

                Supplier<Object> moduleConstructor = createModuleConstructor(clazz, beanProvider);
                Command.Builder commandBuilder = Command.builder()
                    .withName(method.getName())
                    .withCallback(createCommandCallback(moduleConstructor, method));

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

    private static Supplier<Object> createModuleConstructor(Class<?> moduleClass, BeanProvider beanProvider) {
        Constructor<?>[] constructors = moduleClass.getConstructors();
        Preconditions.checkState(constructors.length > 0, "Public constructor for module %s", moduleClass);
        Constructor<?> reflectedConstructor = constructors[0];
        Class<?>[] parameterTypes = reflectedConstructor.getParameterTypes();
        try {
            return () -> {
                try {
                    if (parameterTypes.length == 0) {
                        return reflectedConstructor.newInstance();
                    }

                    Object[] beans = new Object[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        Class<?> parameterType = parameterTypes[i];
                        Object bean = beanProvider.getBean(parameterType);
                        if (bean == null) {
                            throw new MissingBeanException(parameterType);
                        }

                        beans[i] = bean;
                    }
                    return reflectedConstructor.newInstance(beans);
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            };
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @SuppressWarnings("unchecked")
    private static CommandCallback createCommandCallback(Supplier<Object> moduleConstructor, Method reflectedMethod) {
        try {
            return (commandContext, parameters) -> {
                try {
                    Object module = moduleConstructor.get();
                    if (parameters.length > 0) {
                        Object[] temp = new Object[parameters.length + 1];
                        temp[0] = commandContext;
                        System.arraycopy(parameters, 0, temp, 1, parameters.length);
                        return (Mono<CommandResult>) reflectedMethod.invoke(module, temp);
                    }

                    return (Mono<CommandResult>) reflectedMethod.invoke(module, commandContext);
                } catch (Throwable throwable) {
                    return Mono.error(throwable);
                }
            };
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
