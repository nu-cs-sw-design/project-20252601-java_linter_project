package linter;
import java.io.IOException;

public class simpleLinter {
    public static void main(String[] args) throws IOException {
        for (String className : args) {
            System.out.println("Linting class: " + className);
        }
    }
}
