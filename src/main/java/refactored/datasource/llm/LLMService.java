package refactored.datasource.llm;

public abstract class LLMService {
    protected String apiKey;
    protected String baseUrl;
    protected int timeout;

    public LLMService(String apiKey) {
        this.apiKey = apiKey;
        this.timeout = 30000;
    }

    public abstract String getResponse(String prompt);

    public abstract String getProviderName();

    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }

    protected String handleError(Exception e) {
        return "Error communicating with LLM service: " + e.getMessage();
    }
}
