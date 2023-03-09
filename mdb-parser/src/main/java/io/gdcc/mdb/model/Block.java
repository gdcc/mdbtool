package io.gdcc.mdb.model;

import java.net.URI;
import java.util.function.Predicate;

public final class Block {
    
    private final String name;
    private final String dataverseAlias;
    private final String displayName;
    private final URI blockUri;
    
    private Block(String name, String dataverseAlias, String displayName, URI blockUri) {
        this.name = name;
        this.dataverseAlias = dataverseAlias;
        this.displayName = displayName;
        this.blockUri = blockUri;
    }
    
    public static final class Builder {

        
        private String name;
        private final Predicate<String> nameValidator = Predicate.not(String::isBlank).and(Validators.BLOCK_NAME);
        
        private String dataverseAlias = "";
        private final Predicate<String> aliasValidator = Validators.DV_COLLECTION_ALIAS.or(String::isEmpty);
        
        private String displayName;
        private final Predicate<String> displayNameValidator = Predicate.not(String::isBlank).and(h -> h.length() < 257);
        
        private URI blockUri;
        private final Predicate<String> blockUriValidator = Validators::isValidUrl;
    
        /**
         * Set a name for this new block. Will validate value.
         * @param name The name
         * @return The builder
         * @throws IllegalArgumentException When the name is null, blank or not matching {@link Validators#BLOCK_NAME_PATTERN}
         */
        public Builder withName(String name) {
            if (name != null && nameValidator.test(name)) {
                this.name = name;
            } else {
                throw new IllegalArgumentException("Name must not be blank and match regex pattern " + Validators.BLOCK_NAME_PATTERN);
            }
            return this;
        }
    
        /**
         * Set a dataverse alias for this new block. Will validate value.
         * @param dataverseAlias The name of the dataverse collection for which this block is only valid for
         * @return The builder
         * @throws IllegalArgumentException When the name is null or not matching {@link Validators#DV_COLLECTION_ALIAS_PATTERN}
         */
        public Builder withDataverseAlias(String dataverseAlias) {
            if (dataverseAlias != null && aliasValidator.test(dataverseAlias)) {
                this.dataverseAlias = dataverseAlias;
            } else {
                throw new IllegalArgumentException("Dataverse alias must be either empty or match " + Validators.DV_COLLECTION_ALIAS_PATTERN);
            }
            return this;
        }
    
        public Builder withDisplayName(String displayName) {
            if (displayName != null && displayNameValidator.test(displayName)) {
                this.displayName = displayName;
            } else {
                throw new IllegalArgumentException("Display name must not be blank and shorter than 256 chars");
            }
            return this;
        }
        
        public Builder withBlockUri(String blockUri) {
            if (blockUri != null && blockUriValidator.test(blockUri)) {
                this.blockUri = URI.create(blockUri);
            } else {
                throw new IllegalArgumentException("Block URI must be a valid URI");
            }
            return this;
        }
        
        public Builder withBlockUri(URI blockUri) {
            this.blockUri = blockUri;
            return this;
        }
        
        public Block build() {
            if (this.name != null && this.dataverseAlias != null && this.displayName != null && this.blockUri != null) {
                return new Block(this.name, this.dataverseAlias, this.displayName, this.blockUri);
            } else {
                throw new IllegalStateException("Cannot initialize block as builder misses data.");
            }
        }
    }
    
    /**
     * Create a new Block Builder instance with nicer code.
     * @return A Block Builder
     */
    public static Builder create() {
        return new Builder();
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDataverseAlias() {
        return this.dataverseAlias;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public URI getBlockUri() {
        return this.blockUri;
    }
}
