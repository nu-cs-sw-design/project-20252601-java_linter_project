package refactored.domain.lint_result;

public class LintResult {
    private Severity severity;
    private String message;
    private String location;
    private String checkName;
    private String suggestion;

    public LintResult(Severity severity, String message, String location, String checkName) {
        this.severity = severity;
        this.message = message;
        this.location = location;
        this.checkName = checkName;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public String getLocation() {
        return location;
    }

    public String getCheckName() {
        return checkName;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(severity).append("] ");
        sb.append(checkName).append(": ");
        sb.append(message);
        if (location != null && !location.isEmpty()) {
            sb.append(" at ").append(location);
        }
        if (suggestion != null && !suggestion.isEmpty()) {
            sb.append("\n  Suggestion: ").append(suggestion);
        }
        return sb.toString();
    }
}
