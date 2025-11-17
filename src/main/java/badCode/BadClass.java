package badCode;

public class BadClass {

    public int BAD_FIELD_NAME = 10;

    String noAccessModifier = "oops";

    public void BADMethod() {
        System.out.println("Bad code!");
    }

    void defaultAccessMethod(int X, int Y) {
        int z = X + Y;
        System.out.println(z);
    }

    void tooManyArgumentsMethod(int a, int b, int c, int d, int e, int f) {
        System.out.println(a + b + c + d + e + f);
    }

    int addNumbers(int a, int b) {
        return a + b + usedPrivateMethod();
    }

    private int usedPrivateMethod() {
        return 42;
    }

    private int unusedPrivateMethod() {
        return 0;
    }
}
