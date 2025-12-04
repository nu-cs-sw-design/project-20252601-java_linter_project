# Custom Java Linter

## Contributors
Dev Shah, Vihaan Shah

## Dependencies
- Java 21+
- Gradle 8+
- ASM 9.6 (`asm`, `asm-tree`, `asm-analysis`, `asm-util`)
- OpenAI-java 4.8

## Build Instructions
```bash
./gradlew dependencies
./gradlew build
```

## Run Instructions
Use default bad code classes:
```bash
./gradlew run
```

Optionally pass fully-qualified class names to the linter for manual control:

*Unix*
```bash
./gradlew run --args="badcode.BadClass"
```

*Windows*
```cmd
gradlew.bat run --args="badcode.BadClass"
```

## Project Structure
- `exampleLinter/` — linter starter code
- `linter/` — linter code
- `badCode/` — test classes with intentionally bad patterns

## Linter Checks
- [x] Too many method arguments
- [x] Method name appropriateness (via LLM)
- [x] Public and non-final fields
- [x] Unused private methods
- [x] Circular dependencies
- [x] Depends on concrete classes instead of interfaces
