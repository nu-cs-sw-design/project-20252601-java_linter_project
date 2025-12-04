package goodLinter.datasource;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.ArrayList;

public class MethodModel extends Model {
    private List<TypeModel> argumentTypes;

    public MethodModel(MethodNode methodNode) {
        this.name = methodNode.name;
        this.argumentTypes = new ArrayList<>();
        for (Type type : Type.getArgumentTypes(methodNode.desc)) {
            this.argumentTypes.add(new TypeModel(type));
        }
    }

    public int getNumArgs() {
        return argumentTypes.size();
    }
    
}
