package casino.noodle.commands.framework.module;

import com.google.common.base.Preconditions;

public record Parameter(
        Class<?> type,
        String description,
        String name) {
    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private Class<?> type;
        private String description;
        private String name;

        private Builder() {
        }

        public Builder withType(Class<?> type) {
            this.type = type;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Parameter build() {
            Preconditions.checkState(type != null, "A parameter type must be specified");
            Preconditions.checkState(name != null, "A parameter name must be specified");
            return new Parameter(type, description, name);
        }
    }
}
