package refactored.presentation;

import refactored.datasource.llm.LLMProviderType;
import refactored.datasource.llm.LLMServiceFactory;
import refactored.domain.CheckType;
import refactored.domain.Linter;
import refactored.domain.LinterConfiguration;
import java.util.List;

public class Application {
    private UserInterface ui;
    private Linter linter;
    private LinterConfiguration config;

    public Application(UserInterface ui) {
        this.ui = ui;
        this.config = new LinterConfiguration();
        this.linter = new Linter(config);
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

            if (provider != null) {
                String apiKey = ui.promptForApiKey(provider);
                config.setLLMProviderType(provider);
                config.setApiKey(apiKey);
            }
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
