package io.gdcc.mdb.model;

import java.net.URI;
import java.util.List;

public class Field {
    
    private final String name;
    private final String title;
    private final String description;
    private final String watermark;
    private final FieldType type;
    private final int displayOrder;
    private final String displayFormat;
    private final boolean advancedSearchField;
    private final boolean allowControlledVocabulary;
    private final boolean allowMultiples;
    private final boolean facetable;
    private final boolean displayOnCreate;
    private final boolean required;
    private final Block metadataBlock;
    private final URI termUri;
    
    private Field parent;
    private List<Field> children;
    
    private Field(Builder builder) {
        name = builder.name;
        title = builder.title;
        description = builder.description;
        watermark = builder.watermark;
        type = builder.type;
        displayOrder = builder.displayOrder;
        displayFormat = builder.displayFormat;
        advancedSearchField = builder.advancedSearchField;
        allowControlledVocabulary = builder.allowControlledVocabulary;
        allowMultiples = builder.allowMultiples;
        facetable = builder.facetable;
        displayOnCreate = builder.displayOnCreate;
        required = builder.required;
        metadataBlock = builder.containingBlock;
        termUri = builder.termUri;
    }
    
    
    public static final class Builder {
        private final Block containingBlock;
        
        public Builder(Block containingBlock) {
            this.containingBlock = containingBlock;
        }
        
        // Required fields
        private String name;
        private String title;
        private FieldType type;
        private int displayOrder = -1;
        private String metadataBlockName;
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withTitle(String title) {
            if (title != null && !title.isBlank() && title.length() < 60) {
                this.title = title;
            } else {
                throw new IllegalArgumentException("Title '" + title + "' may not be null, empty, blank or longer than 40 chars");
            }
            return this;
        }
        
        public Builder withType(String typeId) {
            if (FieldType.matchesType(typeId)) {
                this.type = FieldType.getById(typeId);
            } else {
                throw new IllegalArgumentException("Unknown field type '" + typeId + "'");
            }
            return this;
        }
        
        public Builder withType(FieldType type) {
            this.type = type;
            return this;
        }
    
        public Builder withDisplayOrder(int displayOrder) {
            if (displayOrder > -1) {
                this.displayOrder = displayOrder;
            } else {
                throw new IllegalArgumentException("Display order may not be a negative number");
            }
            return this;
        }
        
        public Builder withDisplayOrder(String displayOrder) {
            try {
                this.displayOrder = Integer.parseUnsignedInt(displayOrder);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Given display order '" + displayOrder +
                    "' may not be null, some string or a negative number");
            }
            return this;
        }
    
        public Builder withMetadataBlock(String name) {
            if (this.containingBlock.getName().equals(name)) {
                this.metadataBlockName = name;
            } else {
                throw new IllegalArgumentException("Given field metadata block name '" + name +
                    "' does not match containing block '" + containingBlock.getName() + "'");
            }
            return this;
        }
        
        // Non-required fields
        private String description = "";
        private String watermark = "";
        private String displayFormat = "";
        private boolean advancedSearchField = false;
        private boolean allowControlledVocabulary = false;
        private boolean allowMultiples = false;
        private boolean facetable = false;
        private boolean displayOnCreate = false;
        private boolean required = false;
        private URI termUri; // will default to blockUri + / + fieldName
    
        public Builder withDescription(String val) {
            if (val == null) {
                throw new IllegalArgumentException("Description may not be null");
            }
            this.description = val;
            return this;
        }
    
        public Builder withWatermark(String val) {
            if (val == null) {
                throw new IllegalArgumentException("Watermark may not be null");
            }
            this.watermark = val;
            return this;
        }
        
        public Builder withDisplayFormat(String val) {
            // TODO: may include more validation in the future to check for simple format errors
            if (val == null || (val.isBlank() && !val.isEmpty())) {
                throw new IllegalArgumentException("Display format may not be null or whitespace only");
            }
            displayFormat = val;
            return this;
        }
        
        public Builder withAdvancedSearchField(boolean val) {
            advancedSearchField = val;
            return this;
        }
    
        public Builder withAdvancedSearchField(String val) {
            advancedSearchField = Validators.getBoolean(val, "advancedSearchField");
            return this;
        }
        
        public Builder withAllowControlledVocabulary(boolean val) {
            allowControlledVocabulary = val;
            return this;
        }
    
        public Builder withAllowControlledVocabulary(String val) {
            allowControlledVocabulary = Validators.getBoolean(val, "allowControlledVocabulary");
            return this;
        }
        
        public Builder withAllowMultiples(boolean val) {
            allowMultiples = val;
            return this;
        }
    
        public Builder withAllowMultiples(String val) {
            allowMultiples = Validators.getBoolean(val, "allowMultiples");
            return this;
        }
        
        public Builder withFacetable(boolean val) {
            facetable = val;
            return this;
        }
    
        public Builder withFacetable(String val) {
            facetable = Validators.getBoolean(val, "facetable");
            return this;
        }
        
        public Builder withDisplayOnCreate(boolean val) {
            displayOnCreate = val;
            return this;
        }
    
        public Builder withDisplayOnCreate(String val) {
            displayOnCreate = Validators.getBoolean(val, "displayOnCreate");
            return this;
        }
        
        public Builder withRequired(boolean val) {
            required = val;
            return this;
        }
    
        public Builder withRequired(String val) {
            required = Validators.getBoolean(val, "required");
            return this;
        }
    
        public Builder withTermUri(URI termUri) {
            this.termUri = termUri;
            return this;
        }
        
        public Builder withTermUri(String val) {
            if (val != null && Validators.isValidUrl(val)) {
                this.termUri = URI.create(val);
                return this;
            }
            
            if (val == null || !val.isEmpty()) {
                throw new IllegalArgumentException("Term URI value must either be a valid URI or empty");
            }
            return this;
        }
        
        // Validate & build
        
        public Field build() {
            return new Field(this);
        }
    }
    
    public static Builder create(Block containingBlock) {
        return new Builder(containingBlock);
    }
}
