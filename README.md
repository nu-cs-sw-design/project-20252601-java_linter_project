# Custom Java Linter

## Contributors
Dev Shah, Vihaan Shah

## Dependencies
- Java 21+
- Gradle 8+
- ASM 9.6 (`asm`, `asm-tree`, `asm-analysis`, `asm-util`)
- OpenAI-java 4.8.0

## Build Instructions
```bash
./gradlew dependencies
./gradlew build
```

*Windows*
```cmd
gradlew.bat dependencies
gradlew.bat build
```

## Run Instructions

The linter runs as an interactive CLI application. When you run it, you'll be prompted to:

1. **Select input type:**
   - Compiled class files (file paths)
   - Fully qualified class names

2. **Select which checks to run:**
   - Too many method arguments
   - Public and non-final fields
   - Unused private methods
   - Method name appropriateness (via LLM)
   - Circular dependencies
   - Depends on concrete classes instead of interfaces

3. **If using LLM checks:** Select LLM provider and provide API key

4. **Provide class input:** Enter class file paths or fully qualified class names

*Unix*
```bash
./gradlew run
```

*Windows*
```cmd
gradlew.bat run
```

## Project Structure
- `refactored/` — Main refactored linter code
  - `presentation/` — User interface and application entry point
  - `domain/` — Core linter logic and check implementations
  - `datasource/` — Class analysis, dependency graph, and LLM integration
- `badCode/` — Test classes with intentionally bad patterns
- `linter/` — Legacy linter code (deprecated)

## Linter Checks
- [x] Too many method arguments
- [x] Method name appropriateness (via LLM)
- [x] Public and non-final fields
- [x] Unused private methods
- [x] Circular dependencies
- [x] Depends on concrete classes instead of interfaces
