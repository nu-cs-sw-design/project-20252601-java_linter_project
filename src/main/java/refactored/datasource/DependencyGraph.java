package refactored.datasource;

import refactored.domain.internal_model.FieldData;
import refactored.domain.internal_model.ClassData;
import refactored.domain.internal_model.MethodData;
import refactored.domain.internal_model.MethodReference;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.*;

public class DependencyGraph {
    private static DependencyGraph instance;
    private Map<String, Set<String>> graph;

    private DependencyGraph() {
        this.graph = new HashMap<>();
    }

    public static DependencyGraph getInstance() {
        if (instance == null) {
            instance = new DependencyGraph();
        }
        return instance;
    }

    public void addDependency(String from, String to) {
        if (to == null || from.equals(to)) {
            return;
        }
        if (isLibraryClass(to)) {
            return;
        }

        graph.putIfAbsent(from, new HashSet<>());
        graph.get(from).add(to);
    }

    private boolean isLibraryClass(String internalName) {
        return internalName.startsWith("java/") ||
               internalName.startsWith("javax/") ||
               internalName.startsWith("sun/") ||
               internalName.startsWith("com/sun/") ||
               internalName.startsWith("jdk/") ||
               internalName.startsWith("org/w3c/") ||
               internalName.startsWith("org/xml/") ||
               internalName.startsWith("org/ietf/") ||
               internalName.startsWith("org/omg/");
    }

    public Set<String> getDependencies(String className) {
        return graph.getOrDefault(className, Collections.emptySet());
    }

    public void collectDependencies(ClassData classData) {
        String className = classData.getClassName();
        graph.putIfAbsent(className, new HashSet<>());

        if (classData.getSuperName() != null) {
            addDependency(className, classData.getSuperName());
        }

        for (String iface : classData.getInterfaces()) {
            addDependency(className, iface);
        }

        for (FieldData field : classData.getFields()) {
            addDependencyFromDescriptor(className, field.getDescriptor());
        }

        for (MethodData method : classData.getMethods()) {
            addDependencyFromDescriptor(className, method.getDescriptor());

            for (MethodReference methodRef : method.getCalledMethods()) {
                addDependency(className, methodRef.getOwner());
            }
        }
    }

    private void addDependencyFromDescriptor(String from, String descriptor) {
        Type type = Type.getType(descriptor);
        switch (type.getSort()) {
            case Type.OBJECT:
                addDependency(from, type.getInternalName());
                break;
            case Type.ARRAY:
                if (type.getElementType().getSort() == Type.OBJECT) {
                    addDependency(from, type.getElementType().getInternalName());
                }
                break;
        }
    }

    public List<List<String>> findCycles() {
        List<List<String>> cycles = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> stack = new HashSet<>();
        LinkedList<String> path = new LinkedList<>();

        for (String node : graph.keySet()) {
            if (!visited.contains(node)) {
                dfs(node, visited, stack, path, cycles);
            }
        }

        return cycles;
    }

    private void dfs(String node, Set<String> visited, Set<String> stack,
                     LinkedList<String> path, List<List<String>> cycles) {
        visited.add(node);
        stack.add(node);
        path.addLast(node);

        for (String next : graph.getOrDefault(node, Collections.emptySet())) {
            if (!visited.contains(next)) {
                dfs(next, visited, stack, path, cycles);
            } else if (stack.contains(next)) {
                cycles.add(extractCycle(path, next));
            }
        }

        stack.remove(node);
        path.removeLast();
    }

    private List<String> extractCycle(LinkedList<String> path, String start) {
        List<String> cycle = new ArrayList<>();
        boolean inCycle = false;

        for (String node : path) {
            if (node.equals(start)) {
                inCycle = true;
            }
            if (inCycle) {
                cycle.add(node);
            }
        }
        cycle.add(start);

        return cycle;
    }

    public boolean isConcreteClass(String internalName) {
        if (isLibraryClass(internalName)) {
            return false;
        }
        if (isEnum(internalName)) {
            return false;
        }
        return !isInterface(internalName) && !isAbstractClass(internalName);
    }

    public boolean isUserDefinedClass(String internalName) {
        return !isLibraryClass(internalName);
    }

    private boolean isInterface(String internalName) {
        try {
            ClassReader reader = new ClassReader(internalName);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
            return (classNode.access & Opcodes.ACC_INTERFACE) != 0;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isAbstractClass(String internalName) {
        try {
            ClassReader reader = new ClassReader(internalName);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
            return (classNode.access & Opcodes.ACC_ABSTRACT) != 0;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isEnum(String internalName) {
        try {
            ClassReader reader = new ClassReader(internalName);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
            return (classNode.access & Opcodes.ACC_ENUM) != 0;
        } catch (IOException e) {
            return false;
        }
    }

    public void clear() {
        graph.clear();
    }
}
