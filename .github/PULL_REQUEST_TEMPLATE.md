# Pull Request Checklist
## General
* Is this a blocking issue or new feature? If yes, QE needs to +1 this PR

## Code
* Are method-/class-/variable-names meaningful?
* Are methods concise, not too long?
* Are catch blocks catching precise Exceptions only (no catch all)?

## Testing
* Are there unit-tests?
* Are there integration tests (or at least a jira to tackle these)?
* Is the non-happy path working, too?
* Are other parts that use the same component still working fine?

## Function
* Does it work?
