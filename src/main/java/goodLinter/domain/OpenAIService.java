package goodLinter.domain;

public class OpenAIService implements LlmService {
    public String getResponse(String prompt) {
        return "Response from OpenAI";
    }
    public boolean validateAPIKey() {
        return true;
    }
    public LlmService getInstance() {
        return new OpenAIService();
    }
}
