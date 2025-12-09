package refactored.domain;

import refactored.datasource.internal_model.ClassData;
import refactored.presentation.LintResult;
import refactored.presentation.Severity;
import java.util.List;

public abstract class LintCheck {
    protected CheckType checkType;
    protected String name;
    protected String description;

    public LintCheck(CheckType checkType, String name, String description) {
        this.checkType = checkType;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    public abstract List<LintResult> check(ClassData classData);

    protected LintResult createResult(Severity severity, String message, String location) {
        return new LintResult(severity, message, location, name);
    }
}
