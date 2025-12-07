package refactored.domain;

import refactored.datasource.internal_model.ClassData;
import refactored.datasource.DependencyGraph;
import refactored.presentation.LintResult;
import refactored.presentation.Severity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConcreteDependencyCheck extends AbstractLintCheck {
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
