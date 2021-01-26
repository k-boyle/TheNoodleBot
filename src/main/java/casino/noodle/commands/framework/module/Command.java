package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.PreconditionResult;
import casino.noodle.commands.framework.results.PreconditionsFailedResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.stream.Collectors;

/* todo
    - name (in build() Precondition too)
 */
public record Command(
        ImmutableSet<String> aliases,
        String description, //todo optional
        CommandCallback commandCallback,
        ImmutableList<CommandParameter> parameters,
        ImmutableList<Precondition> preconditions,
        Signature signature,
        Module module) {
    static Builder builder() {
        return new Builder();
    }

    public PreconditionResult check(CommandContext context) {
        PreconditionResult moduleResult = module.check(context);

        if (!moduleResult.isSuccess()) {
            return moduleResult;
        }

        if (preconditions.isEmpty()) {
            return PreconditionResult.Success.get();
        }

        ImmutableList.Builder<PreconditionResult.Failure> failedResults = ImmutableList.builder();
        boolean failedResult = false;

        for (Precondition precondition : preconditions) {
            PreconditionResult result = precondition.check(context);
            if (result instanceof PreconditionResult.Failure failed) {
                failedResults.add(failed);
                failedResult = true;
            }
        }

        return failedResult
            ? new PreconditionsFailedResult(failedResults.build())
            : PreconditionResult.Success.get();
    }

    static class Builder {
        private final ImmutableSet.Builder<String> aliases;
        private final ImmutableList.Builder<CommandParameter> parameters;
        private final ImmutableList.Builder<Precondition> preconditions;

        private Module module;
        private String description;
        private CommandCallback commandCallback;

        private Builder() {
            this.aliases = ImmutableSet.builder();
            this.parameters = ImmutableList.builder();
            this.preconditions = ImmutableList.builder();
        }

        public Builder withAliases(String... aliases) {
            this.aliases.add(aliases);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withCallback(CommandCallback commandCallback) {
            this.commandCallback = commandCallback;
            return this;
        }

        public Builder withParameter(CommandParameter commandParameter) {
            this.parameters.add(commandParameter);
            return this;
        }

        public Builder withPrecondition(Precondition precondition) {
            this.preconditions.add(precondition);
            return this;
        }

        public Builder withModule(Module module){
            this.module = module;
            return this;
        }

        public Command build() {
            Preconditions.checkNotNull(commandCallback, "A command callback must be specified");
            Preconditions.checkNotNull(module, "A module must be assigned to this command");

            ImmutableList<CommandParameter> parameters = this.parameters.build();
            for (int i = 0; i < parameters.size(); i++) {
                CommandParameter commandParameter = parameters.get(i);
                Preconditions.checkState(
                    !commandParameter.remainder() || i == parameters.size() - 1,
                    "Parameter %s of Command %s cannot be remainder only the final parameter can be remainder",
                    commandParameter.name(),
                    ""
                );
            }

            Signature commandSignature = new Signature(
                !parameters.isEmpty() && parameters.get(parameters.size() - 1).remainder(),
                parameters.stream()
                    .map(CommandParameter::type)
                    .map(Class::toString)
                    .collect(Collectors.joining(";"))
            );

            return new Command(
                this.aliases.build(),
                this.description,
                this.commandCallback,
                parameters,
                this.preconditions.build(),
                commandSignature,
                this.module);
        }
    }

    public static record Signature(boolean remainder, String parameters) {
    }
}
