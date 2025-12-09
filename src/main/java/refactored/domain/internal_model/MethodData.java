package refactored.domain.internal_model;

import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public class MethodData {
    private String name;
    private String descriptor;
    private int accessFlags;
    private String returnType;
    private List<String> parameterTypes;
    private List<InstructionData> instructions;
    private List<MethodReference> calledMethods;

    public MethodData(String name, String descriptor, int accessFlags, String returnType, List<String> parameterTypes) {
        this.name = name;
        this.descriptor = descriptor;
        this.accessFlags = accessFlags;
        this.instructions = new ArrayList<>();
        this.calledMethods = new ArrayList<>();
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public boolean isPrivate() {
        return (accessFlags & Opcodes.ACC_PRIVATE) != 0;
    }

    public boolean isPublic() {
        return (accessFlags & Opcodes.ACC_PUBLIC) != 0;
    }

    public boolean isStatic() {
        return (accessFlags & Opcodes.ACC_STATIC) != 0;
    }

    public boolean isConstructor() {
        return name.equals("<init>");
    }

    public String getReturnType() {
        return returnType;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public int getParameterCount() {
        return parameterTypes.size();
    }

    public List<MethodReference> getCalledMethods() {
        return calledMethods;
    }

    public void addCalledMethod(MethodReference methodRef) {
        calledMethods.add(methodRef);
    }

    public List<InstructionData> getInstructions() {
        return instructions;
    }

    public void addInstruction(InstructionData instruction) {
        instructions.add(instruction);
    }
}
