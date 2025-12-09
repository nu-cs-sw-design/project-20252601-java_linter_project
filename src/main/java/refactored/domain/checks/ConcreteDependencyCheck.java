package refactored.domain.checks;

import refactored.domain.internal_model.ClassData;
import refactored.domain.DependencyGraph;
import refactored.domain.lint_result.LintResult;
import refactored.domain.lint_result.Severity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConcreteDependencyCheck extends LintCheck {
    private DependencyGraph dependencyGraph;

    public ConcreteDependencyCheck(DependencyGraph dependencyGraph) {
        super(CheckType.CONCRETE_DEPENDENCY,
              "Concrete Dependency",
              "Checks for dependencies on concrete classes instead of abstractions");
        this.dependencyGraph = dependencyGraph;
    }

    @Override
    public List<LintResult> check(ClassData classData) {
        List<LintResult> results = new ArrayList<>();
        String className = classData.getClassName();
        Set<String> dependencies = dependencyGraph.getDependencies(className);

        for (String dependency : dependencies) {
            if (dependencyGraph.isConcreteClass(dependency)) {
                String message = String.format("Class depends on concrete class '%s'",
                        dependency.replace('/', '.'));
                String location = className.replace('/', '.');
                LintResult result = createResult(Severity.INFO, message, location);
                results.add(result);
            }
        }

        return results;
    }
}
