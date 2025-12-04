package goodLinter.domain;

import goodLinter.datasource.Model;
import goodLinter.datasource.MethodModel;

public class TooManyArgumentsCheck implements Check {
    private int numberOfArguments;
    private String methodName;
    public boolean doCheck(Model model) {
        if (!(model instanceof MethodModel)) {
            throw new IllegalArgumentException("Model is not a MethodModel");
        }
        MethodModel methodModel = (MethodModel) model;
        numberOfArguments = methodModel.getNumArgs();
        methodName = methodModel.getName();
        return numberOfArguments <= 3;
    }
    public String getCheckResult() {
        if (numberOfArguments > 3) {
            return "Method " + methodName + " has too many arguments: " + numberOfArguments;
        }
        return "Method " + methodName + " has " + numberOfArguments + " arguments, which is acceptable.";
    }
}
