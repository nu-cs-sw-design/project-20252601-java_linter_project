package refactored.domain;

import refactored.datasource.LLMProviderType;
import refactored.datasource.LLMService;
import refactored.datasource.OpenAILLMService;

import java.util.ArrayList;
import java.util.List;

public class LLMServiceFactory {
    public LLMService createService(LLMProviderType type, String apiKey) {
        switch (type) {
            case OPENAI:
                return new OpenAILLMService(apiKey);
            default:
                throw new IllegalArgumentException("Unsupported LLM provider type: " + type);
        }
    }

    public List<LLMProviderType> getAvailableProviders() {
        List<LLMProviderType> providers = new ArrayList<>();
        providers.add(LLMProviderType.OPENAI);
        return providers;
    }
}
