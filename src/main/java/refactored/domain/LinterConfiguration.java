package refactored.domain;

import refactored.datasource.llm.LLMProviderType;
import java.util.HashSet;
import java.util.Set;

public class LinterConfiguration {
    private int maxArguments;
    private LLMProviderType llmProviderType;
    private String apiKey;
    private Set<CheckType> enabledChecks;

    public LinterConfiguration() {
        this.maxArguments = 3;
        this.enabledChecks = new HashSet<>();
    }

    public int getMaxArguments() {
        return maxArguments;
    }

    public void setMaxArguments(int maxArguments) {
        this.maxArguments = maxArguments;
    }

    public LLMProviderType getLLMProviderType() {
        return llmProviderType;
    }

    public void setLLMProviderType(LLMProviderType llmProviderType) {
        this.llmProviderType = llmProviderType;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Set<CheckType> getEnabledChecks() {
        return enabledChecks;
    }

    public void setEnabledChecks(Set<CheckType> enabledChecks) {
        this.enabledChecks = enabledChecks;
    }
}
