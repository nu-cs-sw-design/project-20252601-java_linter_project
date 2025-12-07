Design.puml contains design details. Refer to that for more information but generally there is a 3-layer architecture used:
- Presentation Layer: Handles user interaction, input, and output.
- Domain Layer: Contains core logic, including the Linter and its configuration.
- Data Source Layer: Manages interactions with external services, such as LLM providers and class analysis using the objectweb.asm library.
