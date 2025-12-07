package refactored.domain;

import refactored.datasource.internal_model.ClassAnalyzer;
import refactored.datasource.internal_model.ClassData;
import refactored.datasource.DependencyGraph;
import refactored.datasource.llm.LLMServiceFactory;
import refactored.presentation.LintResult;
import refactored.presentation.LintResultObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Linter {
    private LintCheckRegistry checkRegistry;
    private ClassAnalyzer classAnalyzer;
    private List<LintResultObserver> observers;

    public Linter(LinterConfiguration config) {
        this.classAnalyzer = new ClassAnalyzer();
        this.observers = new ArrayList<>();

        DependencyGraph dependencyGraph = DependencyGraph.getInstance();
        LLMServiceFactory llmServiceFactory = LLMServiceFactory.getInstance();
        this.checkRegistry = LintCheckRegistry.getInstance(config, llmServiceFactory, dependencyGraph);
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
            LintCheck check = checkRegistry.createCheck(checkType);

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
}
