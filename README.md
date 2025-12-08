# Custom Java Linter

## Contributors
Dev Shah, Vihaan Shah

## Demo

Watch a video demonstration of the project (including the LLM check that requires api access): [Loom Recording](https://www.loom.com/share/f89a30cc2e9b4a8183b48c3f2a5b605f)

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

*Unix*
```bash
./gradlew run
```

*Windows*
```cmd
gradlew.bat run
```

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


## Example Test Files

The project includes example test classes in the `badCode` package that demonstrate various linting issues. After building the project, you can test the linter with these classes.

### Using Compiled Class Files

After building, copy the compiled class files from the build directory:

*Unix*
```bash
# Class files are located at:
build/classes/java/main/badCode/
```

*Windows*
```cmd
REM Class files are located at:
build\classes\java\main\badCode\
```

When prompted for input type, select option 1 (Compiled class files) and provide the full paths to the `.class` files:
- `build/classes/java/main/badCode/BadClass.class`
- `build/classes/java/main/badCode/OtherClass.class`
- `build/classes/java/main/badCode/YetAnotherClass.class`
- `build/classes/java/main/badCode/someInterface.class`

### Using Fully Qualified Class Names

Alternatively, when prompted for input type, select option 2 (Fully qualified class names) and enter:

- `badCode.BadClass`
- `badCode.OtherClass`
- `badCode.YetAnotherClass`
- `badCode.someInterface`

These example classes contain various code smells that the linter will detect:
- **BadClass**: Too many arguments, public non-final fields, unused private methods, inappropriate method names
- **OtherClass**, **YetAnotherClass**, **someInterface**: Circular dependencies and concrete class dependencies

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
