package refactored.domain.checks;

import refactored.domain.internal_model.ClassData;
import refactored.domain.internal_model.MethodData;
import refactored.domain.lint_result.LintResult;
import refactored.domain.lint_result.Severity;
import java.util.ArrayList;
import java.util.List;

public class TooManyArgumentsCheck extends LintCheck {
    private int maxArguments;

    public TooManyArgumentsCheck(int maxArguments) {
        super(CheckType.TOO_MANY_ARGUMENTS,
              "Too Many Arguments",
              "Checks if methods have more than the allowed number of arguments");
        this.maxArguments = maxArguments;
    }

    @Override
    public List<LintResult> check(ClassData classData) {
        List<LintResult> results = new ArrayList<>();

        for (MethodData method : classData.getMethods()) {
            if (method.getParameterCount() > maxArguments) {
                String message = String.format("Method '%s' has %d parameters (max: %d)",
                        method.getName(), method.getParameterCount(), maxArguments);
                String location = classData.getClassName() + "." + method.getName();
                results.add(createResult(Severity.WARNING, message, location));
            }
        }

        return results;
    }
}
