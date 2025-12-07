package refactored.domain;

import refactored.datasource.internal_model.ClassData;
import refactored.datasource.internal_model.FieldData;
import refactored.presentation.LintResult;
import refactored.presentation.Severity;
import java.util.ArrayList;
import java.util.List;

public class PublicNonFinalFieldCheck extends AbstractLintCheck {

    public PublicNonFinalFieldCheck() {
        super(CheckType.PUBLIC_NON_FINAL_FIELD,
              "Public Non-Final Field",
              "Checks for public fields that are not final");
    }

    @Override
    public List<LintResult> check(ClassData classData) {
        List<LintResult> results = new ArrayList<>();

        for (FieldData field : classData.getFields()) {
            if (field.isPublic() && !field.isFinal()) {
                String message = String.format("Field '%s' is public but not final", field.getName());
                String location = classData.getClassName() + "." + field.getName();
                results.add(createResult(Severity.WARNING, message, location));
            }
        }

        return results;
    }
}
