package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.command.CommandResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class CommandCallbackFactory {
    private static final String OBSOLETE = "";

    private static final String IMPORT_TEMPLATE = "import %s.%s;\n";
    private static final String CLASS_NAME_TEMPLATE = "%s%s%d";
    private static final String BEANS_CAST_TEMPLATE = "(%s)beans[%d]";
    private static final String PARAMETER_CAST_TEMPLATE = "(%s)parameters[%d]";

    private static final String CLASS_TEMPLATE = """
        package %1$s;
        
        import %12$s.%11$s;
        import %13$s.%3$s;
        import %14$s.%4$s;
        import %15$s.%5$s;
        import %16$s.%6$s;
        import %19$s.%18$s;
        
        %17$s
        
        public final class %2$s implements %11$s {
            @Override
            public %3$s<%4$s> execute(%5$s context, Object[] beans, Object[] parameters) {
                %8$s
                %6$s module = new %6$s(%7$s);
                module.setContext((%18$s) context);
                try {
                    return module.%9$s(%10$s);
                } catch (Exception ex) {
                    return %3$s.error(ex);
                }
            }
        }        
        """;

    private static final String CLASSPATH = System.getProperty("java.class.path");

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JavaCompiler javaCompiler;
    private final StandardJavaFileManager standardJavaFileManager;

    public CommandCallbackFactory() {
        this.javaCompiler = ToolProvider.getSystemJavaCompiler();
        this.standardJavaFileManager = javaCompiler.getStandardFileManager(null, null, null);
    }

    public <T extends CommandContext> CommandCallback createCommandCallback(
            Class<T> concreteCommandContextClazz,
            Class<? extends CommandModuleBase<T>> moduleClazz,
            Method method) {
        logger.trace("Creating command callback from {}", method.getName());

        Constructor<?>[] constructors = moduleClazz.getConstructors();
        Preconditions.checkState(constructors.length == 1, "There must be only 1 public constructor");

        StringBuilder additionalImports = new StringBuilder();

        Constructor<?> constructor = constructors[0];

        Class<?>[] constructorParameterTypes = constructor.getParameterTypes();
        for (Class<?> constructorParameterType : constructorParameterTypes) {
            if (constructorParameterType.isPrimitive()) {
                continue;
            }
            additionalImports.append(String.format(IMPORT_TEMPLATE, constructorParameterType.getPackageName(), constructorParameterType.getSimpleName()));
        }

        String beansDestructed = Streams.zip(
            Arrays.stream(constructorParameterTypes),
            IntStream.range(0, constructorParameterTypes.length).boxed(),
            (parameter, index) -> String.format(BEANS_CAST_TEMPLATE, parameter.getSimpleName(), index)
        )
            .collect(Collectors.joining(", "));


        Class<?>[] methodParameterTypes = method.getParameterTypes();
        for (Class<?> methodParameterType : methodParameterTypes) {
            if (methodParameterType.isPrimitive()) {
                continue;
            }
            additionalImports.append(String.format(IMPORT_TEMPLATE, methodParameterType.getPackageName(), methodParameterType.getSimpleName()));
        }

        String parametersDestructed = Streams.zip(
            Arrays.stream(methodParameterTypes),
            IntStream.range(0, methodParameterTypes.length).boxed(),
            (parameter, index) -> String.format(PARAMETER_CAST_TEMPLATE, parameter.getSimpleName(), index)
        )
            .collect(Collectors.joining(", "));

        String package0 = CommandCallbackFactory.class.getPackageName();

        String generatedClassName = String.format(
            CLASS_NAME_TEMPLATE,
            moduleClazz.getSimpleName(),
            method.getName(),
            System.nanoTime()
        );

        Class<CommandCallback> commandCallbackClazz = CommandCallback.class;
        String commandCallbackSimpleName = commandCallbackClazz.getSimpleName();
        String commandCallbackPackage = commandCallbackClazz.getPackageName();

        Class<Mono> monoClazz = Mono.class;
        String monoSimpleName = monoClazz.getSimpleName();
        String monoPackage = monoClazz.getPackageName();

        Class<CommandResult> commandResultClazz = CommandResult.class;
        String commandResultSimpleName = commandResultClazz.getSimpleName();
        String commandResultPackage = commandResultClazz.getPackageName();

        Class<CommandContext> commandContextClazz = CommandContext.class;
        String commandContextSimpleName = commandContextClazz.getSimpleName();
        String commandContextPackage = commandContextClazz.getPackageName();

        String moduleSimpleName = moduleClazz.getSimpleName();
        String modulePackage = moduleClazz.getPackageName();

        String concreteCommandContextSimpleName = concreteCommandContextClazz.getSimpleName();
        String concreteCommandContextPackage = concreteCommandContextClazz.getPackageName();

        String methodName = method.getName();

        String code = String.format(
            CLASS_TEMPLATE,
            package0,
            generatedClassName,
            monoSimpleName,
            commandResultSimpleName,
            commandContextSimpleName,
            moduleSimpleName,
            beansDestructed,
            OBSOLETE,
            methodName,
            parametersDestructed,
            commandCallbackSimpleName,
            commandCallbackPackage,
            monoPackage,
            commandResultPackage,
            commandContextPackage,
            modulePackage,
            additionalImports.toString(),
            concreteCommandContextSimpleName,
            concreteCommandContextPackage
        );

        logger.trace("Generated code\n{}", code);

        // based on https://github.com/medallia/javaone2016/blob/master/src/main/java/com/medallia/codegen/JavaCodeGenerator.java
        List<JavaFileObject> compilationUnits = Collections.singletonList(new SourceFile(URI.create(generatedClassName), code));
        FileManager fileManager = new FileManager(standardJavaFileManager);
        StringWriter output = new StringWriter();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        List<String> options = List.of(
            "--release",
            System.getProperty("java.version").replace("-ea", ""),
            "--enable-preview",
            "-g",
            "-proc:none",
            "-classpath",
            CLASSPATH
        );
        CompilationTask compilationTask = javaCompiler.getTask(
            output,
            fileManager,
            diagnosticCollector,
            options,
            null,
            compilationUnits
        );

        logger.trace("Compiling class {}", generatedClassName);

        boolean success = compilationTask.call();
        Preconditions.checkState(
            success,
            "Failed to generate command class due to %s",
            diagnosticCollector.getDiagnostics()
        );
        byte[] byteCode = fileManager.output.outputStream.toByteArray();
        var classLoader = new ClassLoader(moduleClazz.getClassLoader()) {
            Class<?> clazz = defineClass(null, byteCode, 0, byteCode.length);
        };

        logger.trace("Creating generated class {}", generatedClassName);
        Class<? extends CommandCallback> generatedClass = classLoader.clazz.asSubclass(CommandCallback.class);
        try {
            return (CommandCallback) generatedClass.getDeclaredConstructors()[0].newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static class SourceFile extends SimpleJavaFileObject {
        private final String contents;

        private SourceFile(URI uri, String contents) {
            super(uri, Kind.SOURCE);
            this.contents = contents;
        }

        @Override
        public String getName() { return uri.getRawSchemeSpecificPart(); }

        @Override
        public boolean isNameCompatible(String simpleName, Kind kind) { return true; }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return contents;
        }
    }

    private static class ClassFile extends SimpleJavaFileObject {
        private ByteArrayOutputStream outputStream;

        private ClassFile(URI uri) {
            super(uri, Kind.CLASS);
        }

        @Override
        public String getName() {
            return uri.getRawSchemeSpecificPart();
        }

        @Override
        public OutputStream openOutputStream() {
            return outputStream = new ByteArrayOutputStream();
        }
    }

    private static class FileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private ClassFile output;

        private FileManager(StandardJavaFileManager target) {
            super(target);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) {
            output = new ClassFile(URI.create(className));
            return output;
        }
    }
}
