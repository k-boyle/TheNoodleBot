package casino.noodle.commands.framework.results.precondition;

import casino.noodle.commands.framework.results.FailedResult;

public record FailurePreconditionResult(String reason) implements FailedResult, PreconditionResult {
}
