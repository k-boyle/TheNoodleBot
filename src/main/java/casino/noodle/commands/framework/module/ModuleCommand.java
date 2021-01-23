package casino.noodle.commands.framework.module;

import com.google.common.collect.ImmutableList;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ModuleCommand {
    private final ImmutableList<String> aliases;
    private final Optional<String> description;

    private ModuleCommand(ImmutableList<String> aliases, Optional<String> description) {
        this.aliases = aliases;
        this.description = description;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private final ImmutableList.Builder<String> aliases;

        private String description;

        private Builder() {
            this.aliases = ImmutableList.builder();
        }

        public Builder withAliases(String... aliases) {
            this.aliases.add(aliases);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ModuleCommand build() {
            return new ModuleCommand(this.aliases.build(), Optional.ofNullable(this.description));
        }
    }
}
