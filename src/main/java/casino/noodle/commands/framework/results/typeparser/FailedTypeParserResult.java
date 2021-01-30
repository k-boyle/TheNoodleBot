package casino.noodle.commands.framework.results.typeparser;

import casino.noodle.commands.framework.results.FailedResult;

public record FailedTypeParserResult(String reason) implements TypeParserResult, FailedResult {
}
