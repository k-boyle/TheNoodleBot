package casino.noodle.commands.framework.results.typeparser;

import casino.noodle.commands.framework.results.SuccessfulResult;

public record SuccessfulTypeParserResult<T>(T value) implements TypeParserResult, SuccessfulResult {
}
