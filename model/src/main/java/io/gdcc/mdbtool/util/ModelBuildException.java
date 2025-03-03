package io.gdcc.mdbtool.util;

import java.util.HashSet;
import java.util.Set;

public class ModelBuildException extends IllegalArgumentException {
    Set<String> modelViolations = new HashSet<>();
    
    public ModelBuildException(String message) {
        this.modelViolations.add(message);
    }
    
    public ModelBuildException(Set<String> modelViolations) {
        this.modelViolations.addAll(modelViolations);
    }
    
    public Set<String> getModelViolations() {
        return Set.copyOf(modelViolations);
    }
    
    @Override
    public String toString() {
        return "[\"" + String.join("\", \"", modelViolations) + "\"]";
    }
}
