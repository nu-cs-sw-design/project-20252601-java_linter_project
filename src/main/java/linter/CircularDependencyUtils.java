package linter;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class CircularDependencyUtils {

    // Graph: class -> set of classes it depends on
    private static final Map<String, Set<String>> dependencyGraph = new HashMap<>();
    private static CircularDependencyUtils instance = null;

    private CircularDependencyUtils() {
        // private constructor to prevent instantiation
    }

    public static CircularDependencyUtils getInstance() {
        if(instance == null) {
            instance = new CircularDependencyUtils();
        }
        return instance;
    }

    public static void analyzeClass(ClassNode cn) {
        String className = cn.name;
        dependencyGraph.putIfAbsent(className, new HashSet<>());
        collectDependencies(cn);
    }

    private static void collectDependencies(ClassNode cn) {
        String A = cn.name;

        if (cn.superName != null) add(A, cn.superName);
        for (Object o : cn.interfaces) add(A, (String) o);
        for (Object fObj : cn.fields) {
            FieldNode f = (FieldNode) fObj;
            addDesc(A, f.desc);
        }
        for (Object mObj : cn.methods) {
            MethodNode mn = (MethodNode) mObj;

            // Params + return types
            addDesc(A, mn.desc);

            // Local variables
            if (mn.localVariables != null) {
                for (Object lvObj : mn.localVariables)
                    addDesc(A, ((LocalVariableNode) lvObj).desc);
            }

            // Method instructions
            for (AbstractInsnNode insn : mn.instructions) {

                // Instantiation: NEW
                if (insn.getOpcode() == Opcodes.NEW) {
                    TypeInsnNode tin = (TypeInsnNode) insn;
                    add(A, tin.desc);
                }

                // Cast: CHECKCAST
                if (insn.getOpcode() == Opcodes.CHECKCAST) {
                    TypeInsnNode tin = (TypeInsnNode) insn;
                    add(A, tin.desc);
                }

                // Method calls: INVOKEVIRTUAL / STATIC / SPECIAL / INTERFACE
                if (insn instanceof MethodInsnNode) {
                    MethodInsnNode min = (MethodInsnNode) insn;
                    add(A, min.owner);
                }
            }
        }
    }

    private static void addDesc(String from, String desc) {
        Type t = Type.getType(desc);
        switch (t.getSort()) {
            case Type.OBJECT:
                add(from, t.getInternalName());
                break;
            case Type.ARRAY:
                if (t.getElementType().getSort() == Type.OBJECT)
                    add(from, t.getElementType().getInternalName());
                break;
            default:
                break;
        }
    }

    private static void add(String from, String to) {
        if (to == null) return;
        if (from.equals(to)) return; // ignore self-dependency
        dependencyGraph.get(from).add(to);

        if (isConcreteClass(to)) {
            System.out.println("[Concrete Dependency] " 
                + from.replace('/', '.') 
                + " -> " 
                + to.replace('/', '.'));
        }
    }

    public static void findCycles() {
        Set<String> visited = new HashSet<>();
        Set<String> stack = new HashSet<>();
        LinkedList<String> path = new LinkedList<>();

        for (String node : dependencyGraph.keySet()) {
            if (!visited.contains(node)) {
                dfs(node, visited, stack, path);
            }
        }
    }

    private static void dfs(String node, Set<String> visited, Set<String> stack, LinkedList<String> path) {
        visited.add(node);
        stack.add(node);
        path.addLast(node);

        for (String next : dependencyGraph.getOrDefault(node, Collections.emptySet())) {
            if (!visited.contains(next)) {
                dfs(next, visited, stack, path);
            } else if (stack.contains(next)) {
                // CYCLE FOUND: extract cycle path
                printCycle(path, next);
            }
        }

        stack.remove(node);
        path.removeLast();
    }

    private static void printCycle(LinkedList<String> path, String start) {
        System.out.print("[Circular Dependency] ");

        boolean inCycle = false;
        for (String s : path) {
            if (s.equals(start)) inCycle = true;
            if (inCycle) System.out.print(s + " -> ");
        }
        System.out.println(start);
    }

    private static boolean isInterface(String internalName) {
        try {
            ClassReader cr = new ClassReader(internalName);
            ClassNode cn = new ClassNode();
            cr.accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
            return (cn.access & Opcodes.ACC_INTERFACE) != 0;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isAbstractClass(String internalName) {
        try {
            ClassReader cr = new ClassReader(internalName);
            ClassNode cn = new ClassNode();
            cr.accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
            return (cn.access & Opcodes.ACC_ABSTRACT) != 0;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isConcreteClass(String internalName) {
        // not interface, not abstract
        return !isInterface(internalName) && !isAbstractClass(internalName);
    }

}
