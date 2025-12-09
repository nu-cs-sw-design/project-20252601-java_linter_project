package refactored.domain;

import refactored.datasource.llm.LLMProviderType;
import refactored.datasource.llm.LLMService;
import refactored.datasource.llm.OpenAILLMService;

import java.util.ArrayList;
import java.util.List;

public class LLMServiceFactory {
    private static LLMServiceFactory instance;

    private LLMServiceFactory() {}

    public static LLMServiceFactory getInstance() {
        if (instance == null) {
            instance = new LLMServiceFactory();
        }
        return instance;
    }

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
