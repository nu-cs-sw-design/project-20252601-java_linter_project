package goodLinter.domain;

public interface LlmService {
    public String getResponse(String prompt);
    public boolean validateAPIKey();
    public LlmService getInstance();
}
