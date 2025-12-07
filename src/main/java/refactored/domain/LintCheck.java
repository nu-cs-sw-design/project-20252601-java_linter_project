package refactored.domain;

import refactored.datasource.internal_model.ClassData;
import refactored.presentation.LintResult;
import java.util.List;

public interface LintCheck {
    String getName();
    String getDescription();
    CheckType getCheckType();
    List<LintResult> check(ClassData classData);
}
