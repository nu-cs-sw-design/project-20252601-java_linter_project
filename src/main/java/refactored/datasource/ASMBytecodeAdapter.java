package refactored.datasource;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import refactored.domain.internal_model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ASMBytecodeAdapter implements BytecodeAdapter {
    
    @Override
    public ClassData readClassFromPath(String filePath) throws IOException {
        byte[] classBytes = Files.readAllBytes(Paths.get(filePath));
        ClassReader reader = new ClassReader(classBytes);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.EXPAND_FRAMES);
        return extractClassData(classNode);
    }
    
    @Override
    public ClassData readClassFromName(String className) throws IOException {
        ClassReader reader = new ClassReader(className);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.EXPAND_FRAMES);
        return extractClassData(classNode);
    }
    
    // Private helper methods - all ASM extraction logic here
    
    private ClassData extractClassData(ClassNode classNode) {
        ClassData classData = new ClassData(classNode.name);
        classData.setSuperName(classNode.superName);
        
        // Set interfaces
        List<String> interfaces = new ArrayList<>();
        if (classNode.interfaces != null) {
            interfaces.addAll(classNode.interfaces);
        }
        classData.setInterfaces(interfaces);
        
        // Set class modifiers
        classData.setInterface((classNode.access & Opcodes.ACC_INTERFACE) != 0);
        classData.setAbstract((classNode.access & Opcodes.ACC_ABSTRACT) != 0);
        classData.setEnum((classNode.access & Opcodes.ACC_ENUM) != 0);
        
        // Extract methods
        List<MethodData> methods = new ArrayList<>();
        for (MethodNode methodNode : classNode.methods) {
            methods.add(extractMethodData(methodNode));
        }
        classData.setMethods(methods);
        
        // Extract fields
        List<FieldData> fields = new ArrayList<>();
        for (FieldNode fieldNode : classNode.fields) {
            fields.add(extractFieldData(fieldNode));
        }
        classData.setFields(fields);
        
        return classData;
    }
    
    private FieldData extractFieldData(FieldNode fieldNode) {
        return new FieldData(fieldNode.name, fieldNode.desc, fieldNode.access);
    }
    
    private MethodData extractMethodData(MethodNode methodNode) {
        String descriptor = methodNode.desc;
        Type returnTypeObj = Type.getReturnType(descriptor);
        String returnType = returnTypeObj.getClassName();
        
        Type[] argTypes = Type.getArgumentTypes(descriptor);
        List<String> parameterTypes = new ArrayList<>();
        for (Type argType : argTypes) {
            parameterTypes.add(argType.getClassName());
        }
        
        MethodData methodData = new MethodData(
            methodNode.name,
            descriptor,
            methodNode.access,
            returnType,
            parameterTypes
        );
        
        // Parse instructions
        InsnList instructions = methodNode.instructions;
        for (int i = 0; i < instructions.size(); i++) {
            AbstractInsnNode insn = instructions.get(i);
            
            InstructionData insnData = extractInstructionData(insn);
            if (insnData != null) {
                methodData.addInstruction(insnData);
            }
            
            // Handle method calls
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
    
    private InstructionData extractInstructionData(AbstractInsnNode insn) {
        Printer printer = new Textifier();
        TraceMethodVisitor mv = new TraceMethodVisitor(printer);
        insn.accept(mv);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        String insnText = sw.toString().trim();
        
        if (insnText.isEmpty()) {
            return null;
        }
        
        return new InstructionData(insn.getOpcode(), insnText);
    }
    
    @Override
    public List<String> extractTypeReferencesFromDescriptor(String descriptor) {
        List<String> references = new ArrayList<>();
        Type type = Type.getType(descriptor);
        
        switch (type.getSort()) {
            case Type.OBJECT:
                references.add(type.getInternalName());
                break;
            case Type.ARRAY:
                if (type.getElementType().getSort() == Type.OBJECT) {
                    references.add(type.getElementType().getInternalName());
                }
                break;
            case Type.METHOD:
                // For method descriptors, extract all parameter and return types
                for (Type argType : Type.getArgumentTypes(descriptor)) {
                    if (argType.getSort() == Type.OBJECT) {
                        references.add(argType.getInternalName());
                    } else if (argType.getSort() == Type.ARRAY && 
                               argType.getElementType().getSort() == Type.OBJECT) {
                        references.add(argType.getElementType().getInternalName());
                    }
                }
                Type returnType = Type.getReturnType(descriptor);
                if (returnType.getSort() == Type.OBJECT) {
                    references.add(returnType.getInternalName());
                } else if (returnType.getSort() == Type.ARRAY && 
                           returnType.getElementType().getSort() == Type.OBJECT) {
                    references.add(returnType.getElementType().getInternalName());
                }
                break;
        }
        
        return references;
    }
    
    @Override
    public boolean isInterface(String internalClassName) throws IOException {
        try {
            ClassReader reader = new ClassReader(internalClassName);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
            return (classNode.access & Opcodes.ACC_INTERFACE) != 0;
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public boolean isAbstractClass(String internalClassName) throws IOException {
        try {
            ClassReader reader = new ClassReader(internalClassName);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
            return (classNode.access & Opcodes.ACC_ABSTRACT) != 0;
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public boolean isEnum(String internalClassName) throws IOException {
        try {
            ClassReader reader = new ClassReader(internalClassName);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
            return (classNode.access & Opcodes.ACC_ENUM) != 0;
        } catch (IOException e) {
            return false;
        }
    }
}
