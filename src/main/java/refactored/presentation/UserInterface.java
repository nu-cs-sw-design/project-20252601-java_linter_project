package refactored.presentation;

import refactored.datasource.llm.LLMProviderType;
import refactored.domain.CheckType;
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
