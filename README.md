# Custom Java Linter

## Contributors
Dev Shah, Vihaan Shah

## Dependencies
- Java 21+
- Gradle 8+
- ASM 9.6 (`asm`, `asm-tree`, `asm-analysis`)
- JUnit 5 (for tests)
- EasyMock (for tests)

## Build Instructions
```bash
./gradlew build
```

## Run Instructions
Pass fully-qualified class names to the linter:
```bash
./gradlew run --args="badcode.BadClass"
```

## Project Structure
- `exampleLinter/` — linter source code
- `badCode/` — test classes with intentionally bad patterns
- `src/test/` — unit tests (none yet)
