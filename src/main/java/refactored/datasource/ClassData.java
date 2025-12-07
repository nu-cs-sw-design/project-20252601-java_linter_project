package refactored.datasource;

import java.util.ArrayList;
import java.util.List;

public class ClassData {
    private String className;
    private String superName;
    private List<String> interfaces;
    private List<MethodData> methods;
    private List<FieldData> fields;

    public ClassData(String className) {
        this.className = className;
        this.interfaces = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.fields = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    public String getSuperName() {
        return superName;
    }

    public void setSuperName(String superName) {
        this.superName = superName;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

    public List<MethodData> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodData> methods) {
        this.methods = methods;
    }

    public List<FieldData> getFields() {
        return fields;
    }

    public void setFields(List<FieldData> fields) {
        this.fields = fields;
    }
}
