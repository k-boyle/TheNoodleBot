package casino.noodle.commands.framework.mapping;

import casino.noodle.commands.framework.module.Command;

public record CommandSearchResult(Command command, int pathLength, String input, int offset) {
}
