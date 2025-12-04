package goodLinter.datasource;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class ClassModel extends Model {
    private List<MethodModel> methods;

    
    public ClassModel(String className) throws IOException {
        this.name = className;

        ClassReader reader = new ClassReader(className);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.EXPAND_FRAMES);

        List<MethodNode> methodNodes = classNode.methods;
        for (MethodNode method : methodNodes) {
            MethodModel methodModel = new MethodModel(method);
            this.methods.add(methodModel);
        }
    }

}
