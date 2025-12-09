package refactored.domain;

import refactored.domain.internal_model.ClassData;
import refactored.domain.internal_model.InstructionData;
import refactored.datasource.llm.LLMService;
import refactored.domain.internal_model.MethodData;
import refactored.presentation.LintResult;
import refactored.presentation.Severity;
import java.util.ArrayList;
import java.util.List;

public class MethodNameAppropriatenessCheck extends LintCheck {
    private LLMService llmService;

    public MethodNameAppropriatenessCheck(LLMService llmService) {
        super(CheckType.METHOD_NAME_APPROPRIATENESS,
              "Method Name Appropriateness",
              "Uses LLM to check if method names appropriately describe their behavior");
        this.llmService = llmService;
    }

    @Override
    public List<LintResult> check(ClassData classData) {
        List<LintResult> results = new ArrayList<>();

        if (!llmService.isAvailable()) {
            return results;
        }

        for (MethodData method : classData.getMethods()) {
            if (method.isConstructor()) {
                continue;
            }

            String prompt = buildPrompt(method);
            String response = llmService.getResponse(prompt);

            if (response != null && response.contains("INAPPROPRIATE")) {
                String location = classData.getClassName() + "." + method.getName();
                LintResult result = createResult(Severity.WARNING, response, location);
                results.add(result);
            }
        }

        return results;
    }

    private String buildPrompt(MethodData method) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze this Java method and determine if the method name is appropriate.\n\n");
        prompt.append("Method Name: ").append(method.getName()).append("\n");
        prompt.append("Return Type: ").append(method.getReturnType()).append("\n");
        prompt.append("Parameters: ");

        List<String> params = method.getParameterTypes();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) prompt.append(", ");
            prompt.append(params.get(i));
        }
        prompt.append("\n\nMethod Instructions:\n");

        for (InstructionData instruction : method.getInstructions()) {
            prompt.append("  ").append(instruction.getTextRepresentation()).append("\n");
        }

        prompt.append("\nBased on the bytecode instructions, does the method name '")
              .append(method.getName())
              .append("' accurately describe what this method does?\n");
        prompt.append("Respond with:\n");
        prompt.append("1. 'APPROPRIATE: <reason>' if the name is good\n");
        prompt.append("2. 'INAPPROPRIATE: <reason> - SUGGEST: <better_name>' if the name should be changed\n");
        prompt.append("Keep your response concise and specific.");

        return prompt.toString();
    }
}
