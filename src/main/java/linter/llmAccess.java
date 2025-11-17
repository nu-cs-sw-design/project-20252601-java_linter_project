package linter;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;


public class llmAccess {
    private static llmAccess me;
    private static OpenAIClient client;
    private static boolean verbose = false;

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

    public static llmAccess getInstance(boolean verbose_mode) {
        if(me == null){
            me = new llmAccess();
        }
        verbose = verbose_mode;
        return me;
    }

    public static String getResponse(String msg) {
        ResponseCreateParams params = ResponseCreateParams.builder()
                .input(msg)
                .model(ChatModel.GPT_4_1)
                .build();
        Response response = client.responses().create(params);
        var msgOpt = response.output().get(0).message();
        
        // Check if message is present and extract it
        if (msgOpt.isPresent()) {
            var message = msgOpt.get();
            
            // Extract the text from the content
            if (!message.content().isEmpty()) {
                var content = message.content().get(0);
                var outputText = content.outputText();
                if (outputText.isPresent()) {
                    String text = outputText.get().text();
                    if (verbose) {
                        System.out.println("=== EXTRACTED TEXT ===");
                        System.out.println(text);
                    }
                    return text;
                }
            }
            if (verbose) {
                System.out.println("No text content found in message");
            }
            return null;
        } else {
            if (verbose) {
                System.out.println("No message present in response");
            }
            return null;
        }
    }

}