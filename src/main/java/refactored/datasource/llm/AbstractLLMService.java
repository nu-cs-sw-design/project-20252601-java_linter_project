package refactored.datasource.llm;

public abstract class AbstractLLMService implements LLMService {
    protected String apiKey;
    protected String baseUrl;
    protected int timeout;

    public AbstractLLMService(String apiKey) {
        this.apiKey = apiKey;
        this.timeout = 30000;
    }

    @Override
    public abstract String getResponse(String prompt);

    @Override
    public abstract String getProviderName();

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }

    protected String handleError(Exception e) {
        return "Error communicating with LLM service: " + e.getMessage();
    }
}
