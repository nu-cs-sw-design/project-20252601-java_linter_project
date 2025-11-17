# Custom Java Linter

## Contributors
Dev Shah, Vihaan Shah

## Dependencies
- Java 21+
- Gradle 8+
- ASM 9.6 (`asm`, `asm-tree`, `asm-analysis`)

## Build Instructions
```bash
./gradlew dependencies
./gradlew build
```

## Run Instructions
Pass fully-qualified class names to the linter:

*Unix*
```bash
./gradlew run --args="badcode.BadClass"
```

*Windows*
```cmd
gradlew.bat run --args="badcode.BadClass"
```

## Project Structure
- `exampleLinter/` — linter source code
- `badCode/` — test classes with intentionally bad patterns

## Linter Checks
- [x] Too many method arguments
- [x] Method name appropriateness (via LLM)
- [x] Public and non-final fields
- [x] Unused private methods
- [ ] Circular dependencies
- [ ] Depends on concrete classes instead of interfaces