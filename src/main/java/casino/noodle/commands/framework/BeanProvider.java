package casino.noodle.commands.framework;

@FunctionalInterface
public interface BeanProvider {
    <T> T getBean(Class<T> clazz);

    class EmptyBeanProvider implements BeanProvider {
        public static final BeanProvider INSTANCE = new EmptyBeanProvider();

        private EmptyBeanProvider() {
        }

        @Override
        public <T> T getBean(Class<T> clazz) {
            return null;
        }
    }
}
