package casino.noodle.commands.framework.mapping;

import casino.noodle.commands.framework.module.Command;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class CommandNode {
    private static final String EMPTY_STRING = "";

    private final ImmutableMap<String, List<Command>> commandsByAlias;
    private final ImmutableMap<String, CommandNode> nodeByAlias;

    private CommandNode(Map<String, List<Command>> commandsByAlias, Map<String, CommandNode> nodeByAlias) {
        this.commandsByAlias = ImmutableMap.copyOf(commandsByAlias);
        this.nodeByAlias = ImmutableMap.copyOf(nodeByAlias);
    }

    public ImmutableList<CommandSearchResult> findCommands(String input) {
        ImmutableList.Builder<CommandSearchResult> results = ImmutableList.builder();
        findCommands(results, new ArrayList<>(), input.split(" "));
        return results.build();
    }

    // todo optimise this
    private void findCommands(ImmutableList.Builder<CommandSearchResult> results, List<String> path, String[] input) {
        if (input.length == 0) {
            return;
        }

        String segment = input[0];
        String[] remainingInput = new String[input.length - 1];
        if (remainingInput.length > 0) {
            System.arraycopy(input, 1, remainingInput, 0, input.length - 1);
        }

        List<Command> commands = commandsByAlias.get(segment);
        if (commands != null) {
            path.add(segment);
            String remaining = toString(remainingInput);
            for (Command command : commands) {
                results.add(new CommandSearchResult(command, ImmutableList.copyOf(path), segment, remaining));
            }
            path.remove(path.size() - 1);
        }

        CommandNode commandNode = nodeByAlias.get(segment);
        if (commandNode != null) {
            path.add(segment);
            commandNode.findCommands(results, path, remainingInput);
            path.remove(path.size() - 1);
        }
    }

    private String toString(String[] input) {
        if (input.length == 0) {
            return EMPTY_STRING;
        }

        return String.join(" ", input);
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private final Map<String, List<Command>> commandsByAlias;
        private final Map<String, Builder> nodeByAlias;

        private Builder() {
            this.commandsByAlias = new HashMap<>();
            this.nodeByAlias = new HashMap<>();
        }

        public Builder addCommand(Command command, List<String> paths, int index) {
            Preconditions.checkState(!paths.isEmpty(), "Cannot map pathless commands to root");

            String path = paths.get(index);
            if (index == paths.size() - 1) {
                commandsByAlias.compute(path, (p, commands) -> {
                    if (commands != null) {
                        assertUniqueCommand(command, p, commands);
                    } else {
                        commands = new ArrayList<>();
                    }

                    commands.add(command);
                    return commands;
                });
            } else {
                nodeByAlias.compute(path, (p, node) -> {
                    if (node == null) {
                        node = CommandNode.builder();
                    }

                    node.addCommand(command, paths, index + 1);
                    return node;
                });
            }

            return this;
        }

        private void assertUniqueCommand(Command command, String path, List<Command> commands) {
            for (Command otherCommand : commands) {
                Command.Signature commandSignature = command.signature();
                Command.Signature otherCommandSignature = otherCommand.signature();

                Preconditions.checkState(
                    !commandSignature.equals(otherCommandSignature),
                    "Multiple matching signatures, %s for path %s",
                    commandSignature,
                    path
                );
            }
        }

        public CommandNode build() {
            Map<String, CommandNode> nodeByAlias = this.nodeByAlias.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, es -> es.getValue().build()));
            return new CommandNode(this.commandsByAlias, nodeByAlias);
        }
    }
}
