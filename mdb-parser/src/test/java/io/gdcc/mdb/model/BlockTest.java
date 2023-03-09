package io.gdcc.mdb.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static io.gdcc.mdb.model.Block.Builder;
import static org.junit.jupiter.api.Assertions.*;

class BlockTest {

    private Builder builder;
    
    @BeforeEach
    void setUp() {
        builder = Block.create();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "foobar", "myFooBar", "a1234", "codeMeta20", "foo_bar"
    })
    void withValidNames(String name) {
        assertDoesNotThrow(() -> builder.withName(name));
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
        "   ", "a b", "1234", "hello.", "_asda", "foo-bar", "1abcd", "PascalCase", "customBLOCK"
    })
    void withInvalidNames(String name) {
        assertThrows(IllegalArgumentException.class, () -> builder.withName(name));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "", "foobar", "myFooBar", "a1234", "codeMeta20", "foo_bar", "foo-bar",
            "_asda", "1abcd", "PascalCase", "ALIAS", "customALIAS"
    })
    void withValidDataverseAlias(String alias) {
        assertDoesNotThrow(() -> builder.withDataverseAlias(alias));
    }
    
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
        "   ", "a b", "1234", "hello."
    })
    void withInvalidDataverseAlias(String alias) {
        assertThrows(IllegalArgumentException.class, () -> builder.withDataverseAlias(alias));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "test", "Foo Bar Becue"
    })
    void withValidDisplayName(String displayName) {
        assertDoesNotThrow(() -> builder.withDisplayName(displayName));
    }
    
    private final static String greater256chars = "EiPhae0ahw6LuoNgiePhohv1Ahr1Zieghie6jax8Aecoo1sosh9iero4quun9we8fiepeiphiechaechahfiequohgifohahkooqu1iaquoo1soo9Shai2lon7Eeph5si8phoChi4Ughek3da7Atie5aethi7Hideigh7wodee2afahkohr9shahr1aetai0shohPh4mohd4Pheipoor4Ien3eid0Haipahyeil7ieb8thai7ahTheemaijo0aih3";
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
        "   ", greater256chars
    })
    void withInvalidDisplayName(String displayName) {
        assertThrows(IllegalArgumentException.class, () -> builder.withDisplayName(displayName));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "http://dataverse.org/citation"
    })
    void withValidBlockUri(String blockUri) {
        assertDoesNotThrow(() -> builder.withBlockUri(blockUri));
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
        "https://", "://test.com/test"
    })
    void withInvalidBlockUri(String blockUri) {
        assertThrows(IllegalArgumentException.class, () -> builder.withBlockUri(blockUri));
    }
    
    @Test
    void failToBuildWithoutInit() {
        assertThrows(IllegalStateException.class, () -> builder.build());
    }
    
    @Test
    void succeedToBuildNoAlias() {
        builder
            .withName("test")
            .withDisplayName("Test")
            .withBlockUri("http://dataverse.org/test");
        assertDoesNotThrow(() -> builder.build());
    }
    
    @Test
    void succeedToBuild() {
        builder
            .withName("test")
            .withDisplayName("Test")
            .withBlockUri("http://dataverse.org/test")
            .withDataverseAlias("test");
        assertDoesNotThrow(() -> builder.build());
    }
    
}