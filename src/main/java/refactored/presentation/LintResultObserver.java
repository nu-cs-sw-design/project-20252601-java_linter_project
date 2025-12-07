package refactored.presentation;

public interface LintResultObserver {
    void onLintResult(LintResult result);
    void onCheckPassed(String checkName, int classCount);
    void onAnalysisComplete(int totalIssues, int classCount);
}
