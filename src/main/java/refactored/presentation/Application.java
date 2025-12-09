package refactored.presentation;

import refactored.datasource.llm.LLMProviderType;
import refactored.datasource.llm.LLMService;
import refactored.domain.LLMServiceFactory;
import refactored.domain.CheckType;
import refactored.domain.Linter;

import java.util.List;

public class Application {
    private UserInterface ui;
    private Linter linter;

    public Application(UserInterface ui) {
        this.ui = ui;
        this.linter = new Linter();
    }

    public void run() {
        ui.start();

        ClassInputType inputType = ui.promptForClassInputType();

        List<CheckType> availableChecks = List.of(CheckType.values());
        List<CheckType> selectedChecks = ui.promptForChecks(availableChecks);

        if (selectedChecks.contains(CheckType.METHOD_NAME_APPROPRIATENESS)) {
            LLMServiceFactory factory = LLMServiceFactory.getInstance();
            List<LLMProviderType> providers = factory.getAvailableProviders();
            LLMProviderType provider = ui.promptForLLMProvider(providers);

            String apiKey = ui.promptForApiKey(provider);
            LLMService llmService = factory.createService(provider, apiKey);
            linter.registerLLMService(llmService);
        }

        if (ui instanceof LintResultObserver) {
            linter.addObserver((LintResultObserver) ui);
        }

        if (inputType == ClassInputType.COMPILED_CLASS_PATH) {
            List<String> paths = ui.promptForDirectoryOrFiles();
            linter.analyzeFromPaths(paths, selectedChecks);
        } else {
            List<String> classNames = ui.promptForFullyQualifiedNames();
            linter.analyzeFromNames(classNames, selectedChecks);
        }
    }

    public static void main(String[] args) {
        UserInterface ui = new CLIUserInterface();
        Application app = new Application(ui);
        app.run();
    }
}
