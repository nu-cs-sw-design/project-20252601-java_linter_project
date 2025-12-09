package refactored.presentation;

import refactored.datasource.ClassInputType;
import refactored.datasource.LLMProviderType;
import refactored.domain.checks.CheckType;
import java.util.List;

public interface UserInterface {
    void start();
    ClassInputType promptForClassInputType();
    List<String> promptForDirectoryOrFiles();
    List<String> promptForFullyQualifiedNames();
    List<CheckType> promptForChecks(List<CheckType> available);
    LLMProviderType promptForLLMProvider(List<LLMProviderType> available);
    String promptForApiKey(LLMProviderType provider);
    void displayError(String message);
}
