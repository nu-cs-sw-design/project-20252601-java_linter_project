package refactored.datasource.llm;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

public class OpenAILLMService extends AbstractLLMService {
    private OpenAIClient client;

    public OpenAILLMService(String apiKey) {
        super(apiKey);
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    @Override
    public String getResponse(String prompt) {
        try {
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .input(prompt)
                    .model(ChatModel.GPT_4_1)
                    .build();

            Response response = client.responses().create(params);
            return extractTextFromResponse(response);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    @Override
    public String getProviderName() {
        return "OpenAI";
    }

    private String extractTextFromResponse(Response response) {
        var messageOpt = response.output().get(0).message();

        if (messageOpt.isPresent()) {
            var message = messageOpt.get();

            if (!message.content().isEmpty()) {
                var content = message.content().get(0);
                var outputText = content.outputText();

                if (outputText.isPresent()) {
                    return outputText.get().text();
                }
            }
        }
        return null;
    }
}
