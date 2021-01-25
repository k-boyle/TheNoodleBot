package casino.noodle.commands.framework.mapping;

import casino.noodle.commands.framework.module.Command;
import com.google.common.collect.ImmutableList;

public record CommandSearchResult(Command command, ImmutableList<String> path, String alias, String arguments) {
}
