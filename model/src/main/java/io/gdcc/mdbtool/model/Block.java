package io.gdcc.mdbtool.model;

import io.gdcc.mdbtool.util.ModelBuildException;
import io.gdcc.mdbtool.util.URIValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is a model class to hold a metadata block for Dataverse with all the abilities Dataverse can
 * offer for such blocks and their content. Using this model allows to create parsers for metadata block definitions
 * and transfer those into some export function or even map to Dataverse internal database model. As such, it is an
 * abstraction to be used as a middleware.
 */
public final class Block {
    
    @NotEmpty(message = "B001: The block's name may not be null or empty.")
    @Pattern(
        regexp = "^[a-z](?!.*__+)(?!.*[A-Z][A-Z]+)[A-Za-z\\d_]+$",
        message = "block.error.name.pattern"
    )
    private final String name;
    
    /**
     * This pattern is derived from edu.harvard.iq.dataverse.Dataverse class field "alias".
     * In addition to not allowing all numbers aliases, it also forbids _ or - at the end using negative lookaheads.
     */
    @Pattern(
        regexp = "^(?!\\d+$)(?!.*[_-]+$)[a-zA-Z0-9_-]+$",
        message = "block.error.alias.pattern")
    private final String dataverseAlias;
    
    @NotBlank(message = "block.error.displayName.blank")
    @Size(max = 256, message = "block.error.displayName.size")
    private final String displayName;
    
    private final URI schemaURI;
    
    private final Set<Field> fields = new CopyOnWriteArraySet<>();
    
    /**
     * Constructs a new instance of the Block class with the specified parameters.
     * Hidden and only to be used by the builder below.
     * See docs/model-blocks.rst for details.
     *
     * @param name The name of the block.
     * @param dataverseAlias The alias for the dataverse associated with the block.
     * @param displayName The display name of the block.
     * @param schemaURI The URI of the schema related to the block.
     * @param fields A set of Field objects representing the fields within the block.
     *               If null, an empty set is used.
     */
    private Block(String name, String dataverseAlias, String displayName, URI schemaURI, Set<Field> fields) {
        this.name = name;
        this.dataverseAlias = dataverseAlias;
        this.displayName = displayName;
        this.schemaURI = schemaURI;
        if (fields != null)
            this.fields.addAll(fields);
    }
    
    /**
     * A builder to create a {@link Block} with all necessary details.
     * Adding details while require validation to pass on the given values.
     */
    public static final class Builder {
        
        private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        private static final Validator validator = factory.getValidator();
        
        private String name;
        private String dataverseAlias;
        private String displayName;
        @URIValidator(message = "block.error.schemaURI.invalid")
        private String schemaURI;
        private final Set<Field> fields = new HashSet<>();
        
        /**
         * Set a name for this new block.
         * @param name The name
         * @return The builder
         */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
    
        /**
         * Set a dataverse collection alias for this new block.
         * This is an optional step, as the alias may be empty.
         * @param dataverseAlias The name of the dataverse collection for which this block is only valid for
         * @return The builder
         */
        public Builder withDataverseAlias(String dataverseAlias) {
            this.dataverseAlias = dataverseAlias;
            return this;
        }
    
        /**
         * Set a display name for this block.
         * @param displayName The display name
         * @return The builder
         */
        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }
    
        /**
         * Set a URI for this block. Not necessarily resolvable, but providing a default namespace for the schema
         * of this block. Term names will be attached to this namespace as subpaths.
         * @param schemaURI The valid URI to use as a namespace
         * @return The builder
         */
        public Builder withSchemaURI(String schemaURI) {
            this.schemaURI = schemaURI;
            return this;
        }
    
        /**
         * Set a namespace URI for the block (no validation necessary). Term names will be attached to this namespace.
         * @param schemaURI The URI
         * @return The builder
         */
        public Builder withSchemaURI(URI schemaURI) {
            if (schemaURI != null)
                this.schemaURI = schemaURI.toString();
            else
                this.schemaURI = null;
            return this;
        }
        
        /**
         * Replace current fields with a new list of fields. Will reset any fields already present.
         *
         * @param fields The list of fields to be added
         * @return The builder
         */
        public Builder withFields(Set<Field> fields) {
            if (fields != null) {
                this.fields.clear();
                this.fields.addAll(fields);
            }
            return this;
        }
        
        /**
         * Add a list of fields to this block. Will keep any fields already present.
         *
         * @param fields The list of fields to be added
         * @return The builder
         * @throws IllegalArgumentException When the set cannot be added (most likely duplicates)
         */
        public Builder addFields(Set<Field> fields) {
            if (fields != null) {
                this.fields.addAll(fields);
            }
            return this;
        }
        
        /**
         * Add a field to this block.
         *
         * @param field The field to be added
         * @return The builder
         * @throws IllegalArgumentException When the field cannot be added (most likely duplicates)
         */
        public Builder addField(Field field) {
            if (field != null) {
                this.fields.add(field);
            }
            return this;
        }
    
        /**
         * Build the {@link Block}. Will only succeed if all details present as necessary.
         * @return The metadata block
         * @throws ModelBuildException If some required detail is missing or invalid data has been added
         */
        public Block build() {
            URI tempSchemaUri = null;
            
            // Check the URI by using the validator on ourselves (this is null and empty save)
            Set<ConstraintViolation<Builder>> builderViolations = validator.validate(this);
            // In case the
            if (this.schemaURI != null && builderViolations.isEmpty()) {
                URI uri = URI.create(this.schemaURI);
            }
            
            // Create the block
            Block block = new Block(this.name, this.dataverseAlias, this.displayName, tempSchemaUri, this.fields);
            
            // Now use Bean Validation to check all the constraints
            Set<ConstraintViolation<Block>> blockViolations = validator.validate(block);
            
            // TODO: we still need to add all the consistency checks for the block model before handing it out!
            
            // Create a nice error message if sth is wrong
            if (!builderViolations.isEmpty() || !blockViolations.isEmpty()) {
                throw new ModelBuildException(
                    Stream.concat(builderViolations.stream(), blockViolations.stream())
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet())
                );
            } else {
                return block;
            }
        }
    }
    
    /**
     * Create a new {@link Builder} instance with nicer to write and read code.
     * @return A {@link Builder}
     */
    public static Builder create() {
        return new Builder();
    }
    
    /**
     * Create a new {@link Builder} instance from an existing {@link Block}.
     * @return A {@link Builder}
     */
    public static Builder create(Block block) {
        return new Builder()
            .withName(block.name())
            .withDisplayName(block.displayName())
            .withDataverseAlias(block.dataverseAlias().orElse(null))
            .withSchemaURI(block.schemaURI().orElse(null))
            .withFields(block.fields());
    }
    
    /**
     * Create a copy of this block as {@link Builder}, inheriting the values
     * from this one and ready to be altered.
     * @return The builder with the cloned block.
     */
    public Builder copy() {
        return create()
            .withName(this.name)
            .withDataverseAlias(this.dataverseAlias)
            .withDisplayName(this.displayName)
            .withSchemaURI(this.schemaURI)
            .withFields(this.fields);
    }
    
    public String name() {
        return this.name;
    }
    
    public Optional<String> dataverseAlias() {
        return Optional.ofNullable(this.dataverseAlias);
    }
    
    public String displayName() {
        return this.displayName;
    }
    
    public Optional<URI> schemaURI() {
        return Optional.ofNullable(this.schemaURI);
    }
    
    public Set<Field> fields() {
        return Set.copyOf(this.fields);
    }
    
    // TODO: Maybe add a "seal" function that will also cascade to seal fields against manipulation?
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block block)) return false;
        return Objects.equals(name(), block.name());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name());
    }
}
