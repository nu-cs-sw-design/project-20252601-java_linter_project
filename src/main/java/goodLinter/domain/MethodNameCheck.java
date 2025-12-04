package goodLinter.domain;

import goodLinter.datasource.Model;
import goodLinter.datasource.MethodModel;

public class MethodNameCheck implements Check {
    private String methodName;
    private String checkResult;

    public MethodNameCheck(LlmAccess llmAccess) {

    public boolean doCheck(Model model) {
        if (!(model instanceof MethodModel)) {
            throw new IllegalArgumentException("Model is not a MethodModel");
        }
        MethodModel methodModel = (MethodModel) model;
        
    }
    public String getCheckResult() {

    }
}
