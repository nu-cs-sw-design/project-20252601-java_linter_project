package linter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

public class simpleLinter {
    static final boolean verbose = false;
    public static void main(String[] args) throws IOException {        
        for (String className : args) {
            ClassReader reader = new ClassReader(className);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.EXPAND_FRAMES);
            System.out.println("\n=== Analyzing class: " + className + " ===\n");
            List<MethodNode> methods = classNode.methods;
            for (MethodNode method : methods) {
                tooManyArgumentsMethodCheck(method);
                checkMethodNameAppropriatenessWithLLM(method);
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
    
    private static void checkMethodNameAppropriatenessWithLLM(MethodNode method) {
        if (method.name.equals("<init>") || method.name.equals("<clinit>")) {
            return;
        }

        Type returnType = Type.getReturnType(method.desc);
        Type[] argTypes = Type.getArgumentTypes(method.desc);
        
        StringBuilder methodInfo = new StringBuilder();
        methodInfo.append("Method Name: ").append(method.name).append("\n");
        methodInfo.append("Return Type: ").append(returnType.getClassName()).append("\n");
        methodInfo.append("Parameters: ");
        for (int i = 0; i < argTypes.length; i++) {
            if (i > 0) methodInfo.append(", ");
            methodInfo.append(argTypes[i].getClassName());
        }
        methodInfo.append("\n\nMethod Instructions:\n");

        // Get method instructions
        InsnList instructions = method.instructions;
        for (int i = 0; i < instructions.size(); i++) {
            AbstractInsnNode insn = instructions.get(i);
            Printer printer = new Textifier();
            TraceMethodVisitor mv = new TraceMethodVisitor(printer);
            insn.accept(mv);
            StringWriter sw = new StringWriter();
            printer.print(new PrintWriter(sw));
            String insnText = sw.toString().trim();
            if (!insnText.isEmpty()) {
                methodInfo.append("  ").append(insnText).append("\n");
            }
        }

        // Create prompt for LLM
        String prompt = "Analyze this Java method and determine if the method name is appropriate for what it does.\n\n"
                + methodInfo.toString() + "\n"
                + "Based on the bytecode instructions above, does the method name '" + method.name 
                + "' accurately describe what this method does? "
                + "Respond with:\n"
                + "1. 'APPROPRIATE: <reason>' if the name is good\n"
                + "2. 'INAPPROPRIATE: <reason> - SUGGEST: <better_name>' if the name should be changed\n"
                + "Keep your response concise and specific.";
        if (verbose) {
            System.out.println("Prompt: " + prompt);
        }
        // Get LLM response
        llmAccess llm = llmAccess.getInstance(verbose);
        String response = llm.getResponse(prompt);
        if (verbose) {
            System.out.println("LLM Response: " + response);
        }
        if (response != null && response.contains("INAPPROPRIATE")) {
            System.out.println("Warning: " + response);
        }
    }


}
