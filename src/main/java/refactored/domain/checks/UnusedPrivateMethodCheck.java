package refactored.domain.checks;

import refactored.domain.internal_model.ClassData;
import refactored.domain.internal_model.MethodData;
import refactored.domain.internal_model.MethodReference;
import refactored.domain.lint_result.LintResult;
import refactored.domain.lint_result.Severity;
import java.util.ArrayList;
import java.util.List;

public class UnusedPrivateMethodCheck extends LintCheck {

    public UnusedPrivateMethodCheck() {
        super(CheckType.UNUSED_PRIVATE_METHOD,
              "Unused Private Method",
              "Checks for private methods that are never called");
    }

    @Override
    public List<LintResult> check(ClassData classData) {
        List<LintResult> results = new ArrayList<>();
        List<MethodData> allMethods = classData.getMethods();

        for (MethodData method : allMethods) {
            if (method.isPrivate() && !method.isConstructor()) {
                if (!isMethodCalled(method, allMethods)) {
                    String message = String.format("Private method '%s' is never used", method.getName());
                    String location = classData.getClassName() + "." + method.getName();
                    results.add(createResult(Severity.WARNING, message, location));
                }
            }
        }

        return results;
    }

    private boolean isMethodCalled(MethodData targetMethod, List<MethodData> allMethods) {
        for (MethodData method : allMethods) {
            if (method == targetMethod) {
                continue;
            }

            for (MethodReference calledMethod : method.getCalledMethods()) {
                if (calledMethod.matches(targetMethod)) {
                    return true;
                }
            }
        }
        return false;
    }
}
