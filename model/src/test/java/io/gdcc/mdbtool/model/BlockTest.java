package io.gdcc.mdbtool.model;

import io.gdcc.mdbtool.util.ModelBuildException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static io.gdcc.mdbtool.model.Block.Builder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BlockTest {

    private Builder builder;
    
    @BeforeEach
    void setUp() {
        builder = Block.create()
            .withName("test")
            .withDisplayName("Test");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "foobar", "myFooBar", "a1234", "codeMeta20", "foo_bar"
    })
    void withValidNames(String name) {
        assertDoesNotThrow(() -> builder.withName(name).build());
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
        "   ", "a b", "1234", "hello.", "_asda", "foo-bar", "1abcd", "PascalCase", "customBLOCK"
    })
    void withInvalidNames(String name) {
        assertThrows(ModelBuildException.class, () -> builder.withName(name).build());
    }
    
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
        "foobar", "myFooBar", "a1234", "codeMeta20", "foo_bar", "foo-bar",
        "_asda", "1abcd", "PascalCase", "ALIAS", "customALIAS"
    })
    void withValidDataverseAlias(String alias) {
        assertDoesNotThrow(() -> builder.withDataverseAlias(alias).build());
    }
    
    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {
        "   ", "a b", "1234", "hello."
    })
    void withInvalidDataverseAlias(String alias) {
        assertThrows(ModelBuildException.class, () -> builder.withDataverseAlias(alias).build());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "test", "Foo Bar Becue"
    })
    void withValidDisplayName(String displayName) {
        assertDoesNotThrow(() -> builder.withDisplayName(displayName).build());
    }
    
    private final static String greater256chars = "EiPhae0ahw6LuoNgiePhohv1Ahr1Zieghie6jax8Aecoo1sosh9iero4quun9we8fiepeiphiechaechahfiequohgifohahkooqu1iaquoo1soo9Shai2lon7Eeph5si8phoChi4Ughek3da7Atie5aethi7Hideigh7wodee2afahkohr9shahr1aetai0shohPh4mohd4Pheipoor4Ien3eid0Haipahyeil7ieb8thai7ahTheemaijo0aih3";
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
        "   ", greater256chars
    })
    void withInvalidDisplayName(String displayName) {
        assertThrows(ModelBuildException.class, () -> builder.withDisplayName(displayName).build());
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
        "http://dataverse.org/citation"
    })
    void withValidSchemaUri(String blockUri) {
        assertDoesNotThrow(() -> builder.withSchemaURI(blockUri).build());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "https://", "://test.com/test"
    })
    void withInvalidSchemaUri(String blockUri) {
        assertThrows(ModelBuildException.class, () -> builder.withSchemaURI(blockUri).build());
    }
    
    @Test
    void failToBuildWithoutInit() {
        assertThrows(ModelBuildException.class, () -> Block.create().build());
    }
    
    @Test
    void succeedToBuildNoAlias() {
        assertDoesNotThrow(() -> Block
            .create()
            .withName("test")
            .withDisplayName("Test")
            .withSchemaURI("http://dataverse.org/test")
            .build());
    }
    
    @Test
    void succeedToBuild() {
        assertDoesNotThrow(() -> Block
            .create()
            .withName("test")
            .withDisplayName("Test")
            .withSchemaURI("http://dataverse.org/test")
            .withDataverseAlias("test")
            .build());
    }
    
}