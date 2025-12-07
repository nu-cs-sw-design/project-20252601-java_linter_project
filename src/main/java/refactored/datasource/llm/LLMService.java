package refactored.datasource.llm;

public interface LLMService {
    String getResponse(String prompt);
    String getProviderName();
    boolean isAvailable();
}
