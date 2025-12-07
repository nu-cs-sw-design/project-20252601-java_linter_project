package refactored.datasource;

public class MethodReference {
    private String owner;
    private String name;
    private String descriptor;

    public MethodReference(String owner, String name, String descriptor) {
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public boolean matches(MethodData method) {
        return this.name.equals(method.getName()) &&
               this.descriptor.equals(method.getDescriptor());
    }
}
