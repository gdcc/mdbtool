package io.gdcc.mdb.model;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This is a model class to hold a metadata block for Dataverse with all the abilities Dataverse can
 * offer for such blocks and their content. Using this model allows to create parsers for metadata block definitions
 * and transfer those into some export function or even map to Dataverse internal database model. As such, it is an
 * abstraction to be used as a middleware.
 */
public final class Block {
    
    private final String name;
    private final String dataverseAlias;
    private final String displayName;
    private final URI blockUri;
    
    /**
     * Create a new block. Hidden and only to be used by the builder below.
     * TODO: add reference to model spec
     * @param name
     * @param dataverseAlias
     * @param displayName
     * @param blockUri
     */
    private Block(String name, String dataverseAlias, String displayName, URI blockUri) {
        this.name = name;
        this.dataverseAlias = dataverseAlias;
        this.displayName = displayName;
        this.blockUri = blockUri;
    }
    
    /**
     * A builder to create a {@link Block} with all necessary details.
     * Adding details while require validation to pass on the given values.
     */
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
         * Set a dataverse collection alias for this new block. Will validate value.
         * This is an optional step, as the alias may be empty.
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
    
        /**
         * Set a display name for this block. Will validate value.
         * @param displayName The display name
         * @return The builder
         * @throws IllegalArgumentException When the display name is null, empty, blank or longer than 256 chars
         */
        public Builder withDisplayName(String displayName) {
            if (displayName != null && displayNameValidator.test(displayName)) {
                this.displayName = displayName;
            } else {
                throw new IllegalArgumentException("Display name must not be blank and shorter than 256 chars");
            }
            return this;
        }
    
        /**
         * Set a URI for this block. Not necessarily resolvable, but providing a default namespace for the schema
         * and fields of this block. Will validate value.
         * @param blockUri The valid URI to use as a namespace
         * @return The builder
         */
        public Builder withBlockUri(String blockUri) {
            if (blockUri != null && blockUriValidator.test(blockUri)) {
                this.blockUri = URI.create(blockUri);
            } else {
                throw new IllegalArgumentException("Block URI must be a valid URI");
            }
            return this;
        }
    
        /**
         * Set a block URI (no validation necessary)
         * @param blockUri The URI
         * @return The builder
         */
        public Builder withBlockUri(URI blockUri) {
            this.blockUri = blockUri;
            return this;
        }
    
        /**
         * Build the {@link Block}. Will only succeed if all details present as necessary.
         * @return The metadata block
         * @throws IllegalStateException If some required detail is missing
         */
        public Block build() {
            if (this.name != null && this.dataverseAlias != null && this.displayName != null && this.blockUri != null) {
                return new Block(this.name, this.dataverseAlias, this.displayName, this.blockUri);
            } else {
                throw new IllegalStateException("Cannot initialize block as builder misses data.");
            }
        }
    }
    
    /**
     * Create a new {@link Block.Builder} instance with nicer to write and read code.
     * @return A {@link Block.Builder}
     */
    public static Builder create() {
        return new Builder();
    }
    
    /**
     * Create a copy of this block as {@link io.gdcc.mdb.model.Block.Builder}, inheriting the values
     * from this one and ready to be altered.
     * @return The builder with the cloned block.
     */
    public Builder copy() {
        return create()
            .withName(this.name)
            .withDataverseAlias(this.dataverseAlias)
            .withDisplayName(this.displayName)
            .withBlockUri(this.blockUri);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Optional<String> getDataverseAlias() {
        if (this.dataverseAlias.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(this.dataverseAlias);
        }
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public URI getBlockUri() {
        return this.blockUri;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;
        Block block = (Block) o;
        return Objects.equals(getName(), block.getName());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
