package refactored.domain;

import refactored.datasource.DependencyGraph;
import refactored.datasource.llm.LLMService;
import refactored.datasource.llm.LLMServiceFactory;
import java.util.Arrays;
import java.util.List;

public class LintCheckRegistry {
    private static LintCheckRegistry instance;
    private LinterConfiguration config;
    private LLMServiceFactory llmServiceFactory;
    private DependencyGraph dependencyGraph;

    private LintCheckRegistry(LinterConfiguration config,
                              LLMServiceFactory llmServiceFactory,
                              DependencyGraph dependencyGraph) {
        this.config = config;
        this.llmServiceFactory = llmServiceFactory;
        this.dependencyGraph = dependencyGraph;
    }

    public static LintCheckRegistry getInstance(LinterConfiguration config,
                                                 LLMServiceFactory llmServiceFactory,
                                                 DependencyGraph dependencyGraph) {
        if (instance == null) {
            instance = new LintCheckRegistry(config, llmServiceFactory, dependencyGraph);
        }
        return instance;
    }

    public LintCheck createCheck(CheckType type) {
        switch (type) {
            case TOO_MANY_ARGUMENTS:
                return new TooManyArgumentsCheck(config.getMaxArguments());

            case PUBLIC_NON_FINAL_FIELD:
                return new PublicNonFinalFieldCheck();

            case UNUSED_PRIVATE_METHOD:
                return new UnusedPrivateMethodCheck();

            case METHOD_NAME_APPROPRIATENESS:
                LLMService llmService = null;
                if (config.getLLMProviderType() != null && config.getApiKey() != null) {
                    llmService = llmServiceFactory.createService(
                            config.getLLMProviderType(),
                            config.getApiKey()
                    );
                }
                return new MethodNameAppropriatenessCheck(llmService);

            case CIRCULAR_DEPENDENCY:
                return new CircularDependencyCheck(dependencyGraph);

            case CONCRETE_DEPENDENCY:
                return new ConcreteDependencyCheck(dependencyGraph);

            default:
                throw new IllegalArgumentException("Unknown check type: " + type);
        }
    }

    public List<CheckType> getAvailableCheckTypes() {
        return Arrays.asList(CheckType.values());
    }
}
