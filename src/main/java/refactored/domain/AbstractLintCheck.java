package refactored.domain;

import refactored.datasource.internal_model.ClassData;
import refactored.presentation.LintResult;
import refactored.presentation.Severity;
import java.util.List;

public abstract class AbstractLintCheck implements LintCheck {
    protected CheckType checkType;
    protected String name;
    protected String description;

    public AbstractLintCheck(CheckType checkType, String name, String description) {
        this.checkType = checkType;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public CheckType getCheckType() {
        return checkType;
    }

    @Override
    public abstract List<LintResult> check(ClassData classData);

    protected LintResult createResult(Severity severity, String message, String location) {
        return new LintResult(severity, message, location, name);
    }
}
