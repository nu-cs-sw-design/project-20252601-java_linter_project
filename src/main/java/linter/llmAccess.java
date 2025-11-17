package linter;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;


public class llmAccess {
    private static llmAccess me;
    OpenAIClient client;

    private String readApiKeyFromFile() {
        try {
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("api_key.txt")));
            return content.trim();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read API key file", e);
        }
    }

    private llmAccess () {
        String apiKey = readApiKeyFromFile();
        client = OpenAIOkHttpClient.builder()
            .apiKey(apiKey)
            .build();
    }

    public static llmAccess getInstance() {
        if(me == null){
            me = new llmAccess();
        }
        return me;
    }

    public static void getResponse(String msg) {
        ResponseCreateParams params = ResponseCreateParams.builder()
                .input(msg)
                .model(ChatModel.GPT_4_1)
                .build();
        Response response = client.responses().create(params);
        System.out.println(response);
    }

}