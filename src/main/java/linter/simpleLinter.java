package linter;
import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.Type;

public class simpleLinter {
    public static void main(String[] args) throws IOException {
        for (String className : args) {
            ClassReader reader = new ClassReader(className);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.EXPAND_FRAMES);
            List<MethodNode> methods = classNode.methods;
            for (MethodNode method : methods) {
                tooManyArgumentsMethodCheck(method);
            }

            List<FieldNode> fields = classNode.fields;
            for (FieldNode field : fields) {
                publicAndNotFinalFieldCheck(field);
            }
        }
    }

    private static void tooManyArgumentsMethodCheck(MethodNode method) {
        Type[] argTypes = Type.getArgumentTypes(method.desc);
        if (argTypes.length > 3) {
            System.out.println("Warning: Method " + method.name + " has too many arguments: " + argTypes.length);
        }
    }

    private static void publicAndNotFinalFieldCheck(FieldNode field) {
        boolean isPublic = (field.access & org.objectweb.asm.Opcodes.ACC_PUBLIC) != 0;
        boolean isFinal = (field.access & org.objectweb.asm.Opcodes.ACC_FINAL) != 0;
        if (isPublic && !isFinal) {
            System.out.println("Warning: Field " + field.name + " is public and not final.");
        }
    }


}
