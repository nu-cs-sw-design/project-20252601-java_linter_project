package refactored.domain.checks;

import refactored.domain.internal_model.ClassData;
import refactored.domain.DependencyGraph;
import refactored.domain.lint_result.LintResult;
import refactored.domain.lint_result.Severity;
import java.util.ArrayList;
import java.util.List;

public class CircularDependencyCheck extends LintCheck {
    private DependencyGraph dependencyGraph;

    public CircularDependencyCheck(DependencyGraph dependencyGraph) {
        super(CheckType.CIRCULAR_DEPENDENCY,
              "Circular Dependency",
              "Detects circular dependencies between classes");
        this.dependencyGraph = dependencyGraph;
    }

    @Override
    public List<LintResult> check(ClassData classData) {
        dependencyGraph.collectDependencies(classData);
        return new ArrayList<>();
    }

    public List<LintResult> detectAllCycles() {
        List<LintResult> results = new ArrayList<>();
        List<List<String>> cycles = dependencyGraph.findCycles();

        for (List<String> cycle : cycles) {
            StringBuilder message = new StringBuilder("Circular dependency detected: ");
            for (int i = 0; i < cycle.size(); i++) {
                if (i > 0) message.append(" -> ");
                message.append(cycle.get(i).replace('/', '.'));
            }

            results.add(createResult(Severity.ERROR, message.toString(), cycle.get(0)));
        }

        return results;
    }
}
