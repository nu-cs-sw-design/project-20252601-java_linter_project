package refactored.domain;

import refactored.datasource.ClassAnalyzer;
import refactored.domain.internal_model.ClassData;
import refactored.datasource.DependencyGraph;
import refactored.datasource.llm.LLMService;
import refactored.presentation.LintResult;
import refactored.presentation.LintResultObserver;
import java.io.IOException;
import java.util.*;

public class Linter {
    private final ClassAnalyzer classAnalyzer;
    private final DependencyGraph dependencyGraph;
    private final List<LintResultObserver> observers;
    private LLMService llmService;
    private static final int MAX_ARGUMENTS = 3;

    public Linter() {
        this.classAnalyzer = new ClassAnalyzer();
        this.observers = new ArrayList<>();
        this.dependencyGraph = DependencyGraph.getInstance();
    }

    public void addObserver(LintResultObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LintResultObserver observer) {
        observers.remove(observer);
    }

    public void analyzeFromPaths(List<String> filePaths, List<CheckType> selectedChecks) {
        try {
            List<ClassData> classDataList = classAnalyzer.analyzeFromPaths(filePaths);
            runChecks(classDataList, selectedChecks);
        } catch (IOException e) {
            notifyObservers(new LintResult(
                    refactored.presentation.Severity.ERROR,
                    "Failed to analyze files: " + e.getMessage(),
                    "",
                    "System"
            ));
        }
    }

    public void analyzeFromNames(List<String> classNames, List<CheckType> selectedChecks) {
        try {
            List<ClassData> classDataList = classAnalyzer.analyzeFromNames(classNames);
            runChecks(classDataList, selectedChecks);
        } catch (IOException e) {
            notifyObservers(new LintResult(
                    refactored.presentation.Severity.ERROR,
                    "Failed to analyze classes: " + e.getMessage(),
                    "",
                    "System"
            ));
        }
    }

    private void runChecks(List<ClassData> classDataList, List<CheckType> selectedChecks) {
        CircularDependencyCheck circularCheck = null;
        int totalIssues = 0;

        for (CheckType checkType : selectedChecks) {
            LintCheck check = createCheck(checkType);

            if (check instanceof CircularDependencyCheck) {
                circularCheck = (CircularDependencyCheck) check;
            }

            int issuesForCheck = 0;
            for (ClassData classData : classDataList) {
                List<LintResult> results = check.check(classData);
                issuesForCheck += results.size();
                for (LintResult result : results) {
                    notifyObservers(result);
                }
            }

            if (issuesForCheck == 0 && !(check instanceof CircularDependencyCheck)) {
                notifyPass(check.getName(), classDataList.size());
            }
            totalIssues += issuesForCheck;
        }

        if (circularCheck != null) {
            List<LintResult> cycleResults = circularCheck.detectAllCycles();
            if (cycleResults.isEmpty()) {
                notifyPass(circularCheck.getName(), classDataList.size());
            } else {
                for (LintResult result : cycleResults) {
                    notifyObservers(result);
                }
                totalIssues += cycleResults.size();
            }
        }

        notifyAnalysisComplete(totalIssues, classDataList.size());
    }

    private void notifyObservers(LintResult result) {
        for (LintResultObserver observer : observers) {
            observer.onLintResult(result);
        }
    }

    private void notifyPass(String checkName, int classCount) {
        for (LintResultObserver observer : observers) {
            observer.onCheckPassed(checkName, classCount);
        }
    }

    private void notifyAnalysisComplete(int totalIssues, int classCount) {
        for (LintResultObserver observer : observers) {
            observer.onAnalysisComplete(totalIssues, classCount);
        }
    }

    public void registerLLMService(LLMService llmService) {
        this.llmService = llmService;
    }

    private LintCheck createCheck(CheckType type) {
        switch (type) {
            case TOO_MANY_ARGUMENTS:
                return new TooManyArgumentsCheck(MAX_ARGUMENTS);

            case PUBLIC_NON_FINAL_FIELD:
                return new PublicNonFinalFieldCheck();

            case UNUSED_PRIVATE_METHOD:
                return new UnusedPrivateMethodCheck();

            case METHOD_NAME_APPROPRIATENESS:
                return new MethodNameAppropriatenessCheck(llmService);

            case CIRCULAR_DEPENDENCY:
                return new CircularDependencyCheck(dependencyGraph);

            case CONCRETE_DEPENDENCY:
                return new ConcreteDependencyCheck(dependencyGraph);

            default:
                throw new IllegalArgumentException("Unknown check type: " + type);
        }
    }

}
