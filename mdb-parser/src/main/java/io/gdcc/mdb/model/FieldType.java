package io.gdcc.mdb.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A representation of metadata block field types, defining the well known types from the spec.
 * Can be extended by inheriting from this class.
 */
public class FieldType {
    
    public static final FieldType NONE = new FieldType("none");
    public static final FieldType DATE = new FieldType("date");
    public static final FieldType EMAIL = new FieldType("email");
    public static final FieldType TEXT = new FieldType("text");
    public static final FieldType TEXTBOX = new FieldType("textbox");
    public static final FieldType URL = new FieldType("url");
    public static final FieldType INT = new FieldType("int");
    public static final FieldType FLOAT = new FieldType("float");
    
    protected static final Map<String, FieldType> typeMap = new HashMap<>();
    
    static {
        typeMap.put(NONE.id, NONE);
        typeMap.put(DATE.id, DATE);
        typeMap.put(EMAIL.id, EMAIL);
        typeMap.put(TEXT.id, TEXT);
        typeMap.put(TEXTBOX.id, TEXTBOX);
        typeMap.put(URL.id, URL);
        typeMap.put(INT.id, INT);
        typeMap.put(FLOAT.id, FLOAT);
    }
    
    /**
     * Get a type if findable in the map of types.
     * @param id The if of a type
     * @return The type
     * @throws NullPointerException If no type can be found for the given id
     */
    public static FieldType getById(String id) {
        return typeMap.get(id);
    }
    
    /**
     * Get all known types
     * @return A set of all types
     */
    public static Set<FieldType> getTypes() {
        return new HashSet<>(typeMap.values());
    }
    
    /**
     * Check if there is a type matching the given id
     * @param id The id for looking up type existance
     * @return true if exists, false otherwise
     */
    public static boolean matchesType(String id) {
        return typeMap.containsKey(id);
    }
    
    private final String id;
    
    public FieldType(String id) {
        Objects.requireNonNull(id);
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldType)) return false;
        FieldType fieldType = (FieldType) o;
        return getId().equals(fieldType.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
