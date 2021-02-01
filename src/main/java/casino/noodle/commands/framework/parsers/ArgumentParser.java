package casino.noodle.commands.framework.parsers;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.Result;

public interface ArgumentParser {
    Result parse(CommandContext context, String input, int index);
}
