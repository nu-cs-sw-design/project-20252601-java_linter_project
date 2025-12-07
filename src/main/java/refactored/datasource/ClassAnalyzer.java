package refactored.datasource;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClassAnalyzer {

    public List<ClassData> analyzeFromPaths(List<String> filePaths) throws IOException {
        List<ClassData> classDataList = new ArrayList<>();
        for (String classFile : filePaths) {
            byte[] classBytes = Files.readAllBytes(Paths.get(classFile));
            ClassReader reader = new ClassReader(classBytes);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.EXPAND_FRAMES);
            classDataList.add(parseClassNode(classNode));
        }

        return classDataList;
    }

    public List<ClassData> analyzeFromNames(List<String> classNames) throws IOException {
        List<ClassData> classDataList = new ArrayList<>();

        for (String className : classNames) {
            ClassReader reader = new ClassReader(className);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.EXPAND_FRAMES);
            classDataList.add(parseClassNode(classNode));
        }

        return classDataList;
    }

    private ClassData parseClassNode(ClassNode classNode) {
        ClassData classData = new ClassData(classNode.name);
        classData.setSuperName(classNode.superName);

        List<String> interfaces = new ArrayList<>();
        if (classNode.interfaces != null) {
            for (Object iface : classNode.interfaces) {
                interfaces.add((String) iface);
            }
        }
        classData.setInterfaces(interfaces);

        List<MethodData> methods = new ArrayList<>();
        for (Object methodObj : classNode.methods) {
            methods.add(parseMethod((MethodNode) methodObj));
        }
        classData.setMethods(methods);

        List<FieldData> fields = new ArrayList<>();
        for (Object fieldObj : classNode.fields) {
            fields.add(parseField((FieldNode) fieldObj));
        }
        classData.setFields(fields);

        return classData;
    }

    private MethodData parseMethod(MethodNode methodNode) {
        MethodData methodData = new MethodData(
                methodNode.name,
                methodNode.desc,
                methodNode.access
        );

        InsnList instructions = methodNode.instructions;
        for (int i = 0; i < instructions.size(); i++) {
            AbstractInsnNode insn = instructions.get(i);

            Printer printer = new Textifier();
            TraceMethodVisitor mv = new TraceMethodVisitor(printer);
            insn.accept(mv);
            StringWriter sw = new StringWriter();
            printer.print(new PrintWriter(sw));
            String insnText = sw.toString().trim();

            if (!insnText.isEmpty()) {
                methodData.addInstruction(new InstructionData(insn.getOpcode(), insnText));
            }

            if (insn instanceof MethodInsnNode) {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;
                methodData.addCalledMethod(new MethodReference(
                        methodInsn.owner,
                        methodInsn.name,
                        methodInsn.desc
                ));
            }
        }

        return methodData;
    }

    private FieldData parseField(FieldNode fieldNode) {
        return new FieldData(fieldNode.name, fieldNode.desc, fieldNode.access);
    }
}
