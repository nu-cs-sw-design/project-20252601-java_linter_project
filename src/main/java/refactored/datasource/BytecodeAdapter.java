package refactored.datasource;

import refactored.domain.internal_model.*;

import java.io.IOException;
import java.util.List;

public interface BytecodeAdapter {
    // High-level methods for reading bytecode - returns fully constructed domain models
    ClassData readClassFromPath(String filePath) throws IOException;
    ClassData readClassFromName(String className) throws IOException;
    
    // Descriptor parsing
    List<String> extractTypeReferencesFromDescriptor(String descriptor);
    
    // Class metadata queries by name (for DependencyGraph)
    boolean isInterface(String internalClassName) throws IOException;
    boolean isAbstractClass(String internalClassName) throws IOException;
    boolean isEnum(String internalClassName) throws IOException;
}
