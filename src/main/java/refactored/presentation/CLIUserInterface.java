package refactored.presentation;

import refactored.datasource.llm.LLMProviderType;
import refactored.domain.CheckType;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLIUserInterface implements UserInterface, LintResultObserver {
    private Scanner scanner;

    public CLIUserInterface() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void start() {
        System.out.println("=== Java Linter ===");
        System.out.println();
    }

    @Override
    public ClassInputType promptForClassInputType() {
        System.out.println("Select input type:");
        System.out.println("1. Compiled class files (path)");
        System.out.println("2. Fully qualified class names");
        System.out.print("Enter choice (1 or 2): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        return choice == 1 ? ClassInputType.COMPILED_CLASS_PATH : ClassInputType.FULLY_QUALIFIED_NAME;
    }

    @Override
    public List<String> promptForDirectoryOrFiles() {
        List<String> paths = new ArrayList<>();
        System.out.println("Enter class file paths (one per line, empty line to finish):");

        while (true) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                break;
            }
            paths.add(line);
        }

        return paths;
    }

    @Override
    public List<String> promptForFullyQualifiedNames() {
        List<String> names = new ArrayList<>();
        System.out.println("Enter fully qualified class names (one per line, empty line to finish):");

        while (true) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                break;
            }
            names.add(line);
        }

        return names;
    }

    @Override
    public List<CheckType> promptForChecks(List<CheckType> available) {
        System.out.println("\nAvailable checks:");
        for (int i = 0; i < available.size(); i++) {
            System.out.println((i + 1) + ". " + available.get(i));
        }
        System.out.println("Enter check numbers separated by spaces (or 'all'): ");

        String input = scanner.nextLine().trim();
        List<CheckType> selected = new ArrayList<>();

        if (input.equalsIgnoreCase("all")) {
            return available;
        }

        String[] parts = input.split("\\s+");
        for (String part : parts) {
            try {
                int index = Integer.parseInt(part) - 1;
                if (index >= 0 && index < available.size()) {
                    selected.add(available.get(index));
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number: " + part);
            }
        }

        return selected;
    }

    @Override
    public LLMProviderType promptForLLMProvider(List<LLMProviderType> available) {
        if (available.isEmpty()) {
            return null;
        }

        System.out.println("\nAvailable LLM providers:");
        for (int i = 0; i < available.size(); i++) {
            System.out.println((i + 1) + ". " + available.get(i));
        }
        System.out.print("Enter provider number (or 0 to skip): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 0 || choice > available.size()) {
            return null;
        }

        return available.get(choice - 1);
    }

    @Override
    public String promptForApiKey(LLMProviderType provider) {
        System.out.print("Enter API key for " + provider + ": ");
        return scanner.nextLine().trim();
    }

    @Override
    public void displayError(String message) {
        System.err.println("ERROR: " + message);
    }

    @Override
    public void onLintResult(LintResult result) {
        System.out.println(result.toString());
    }

    @Override
    public void onCheckPassed(String checkName, int classCount) {
        System.out.println("[PASS] " + checkName + ": No issues found in " + classCount + " class(es)");
    }

    @Override
    public void onAnalysisComplete(int totalIssues, int classCount) {
        System.out.println("\n=== Analysis Complete ===");
        System.out.println("Total classes analyzed: " + classCount);
        System.out.println("Total issues found: " + totalIssues);
        if (totalIssues == 0) {
            System.out.println("All checks passed!");
        }
    }
}
