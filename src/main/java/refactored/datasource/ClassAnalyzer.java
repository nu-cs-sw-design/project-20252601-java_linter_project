package refactored.datasource;

import refactored.domain.internal_model.ClassData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassAnalyzer {
    private final BytecodeAdapter adapter;

    public ClassAnalyzer() {
        this.adapter = new ASMBytecodeAdapter();
    }

    public List<ClassData> analyzeFromPaths(List<String> filePaths) throws IOException {
        List<ClassData> classDataList = new ArrayList<>();
        for (String classFile : filePaths) {
            classDataList.add(adapter.readClassFromPath(classFile));
        }
        return classDataList;
    }

    public List<ClassData> analyzeFromNames(List<String> classNames) throws IOException {
        List<ClassData> classDataList = new ArrayList<>();
        for (String className : classNames) {
            classDataList.add(adapter.readClassFromName(className));
        }
        return classDataList;
    }
}
