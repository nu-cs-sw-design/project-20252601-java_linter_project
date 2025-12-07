package refactored.datasource.internal_model;

import org.objectweb.asm.Opcodes;

public class FieldData {
    private String name;
    private String descriptor;
    private int accessFlags;

    public FieldData(String name, String descriptor, int accessFlags) {
        this.name = name;
        this.descriptor = descriptor;
        this.accessFlags = accessFlags;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public boolean isPublic() {
        return (accessFlags & Opcodes.ACC_PUBLIC) != 0;
    }

    public boolean isPrivate() {
        return (accessFlags & Opcodes.ACC_PRIVATE) != 0;
    }

    public boolean isFinal() {
        return (accessFlags & Opcodes.ACC_FINAL) != 0;
    }

    public boolean isStatic() {
        return (accessFlags & Opcodes.ACC_STATIC) != 0;
    }
}
