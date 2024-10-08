package io.gdcc.mdb.tsv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class FieldTest {
    
    private static final Logger logger = Logger.getLogger(FieldTest.class.getCanonicalName());
    
    static final Configuration config = Configuration.defaultConfig();
    static final String validHeaderLine = "#datasetField\tname\ttitle\tdescription\twatermark\tfieldType" +
        "\tdisplayOrder\tdisplayFormat\tadvancedSearchField\tallowControlledVocabulary\tallowmultiples\tfacetable" +
        "\tdisplayoncreate\trequired\tparent\tmetadatablock_id\ttermURI";
    static final String validContainingBlockName = "citation";
    static final String validFieldDef = "\ttitle\tTitle\tThe main title of the Dataset\t\ttext" +
        "\t0\t\tTRUE\tFALSE\tFALSE\tFALSE\tTRUE\tTRUE\t\tcitation\thttp://purl.org/dc/terms/title";
    
    @Nested
    class TypesTest {
        Predicate<String> allowedTypes = Field.Types.matchesTypes();
        
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"foobar", "hello_hello", "NONE", "DATE"})
        void failing(String subject) {
            assertFalse(allowedTypes.test(subject));
        }
    
        @ParameterizedTest
        @ValueSource(strings = {"none", "text", "textbox", "date", "email", "int", "float"})
        void succeeding(String subject) {
            assertTrue(allowedTypes.test(subject));
        }
    }
    
    @Nested
    class HeaderFieldValueValidationTest {
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "_foobar_", "_foo_bar_"})
        void invalidNames(String subject) {
            assertFalse(Field.Header.NAME.isValid(subject));
        }
    
        @ParameterizedTest
        @ValueSource(strings = {"foobar_", "foo_bar_", "_foobar", "_foo_bar", "foobar", "foobar1234", "foo_bar_1234"})
        void validNames(String subject) {
            assertTrue(Field.Header.NAME.isValid(subject));
            assertTrue(Field.Header.PARENT.isValid(subject));
        }
    
        @ParameterizedTest
        @EmptySource
        void validParentName(String subject) {
            assertTrue(Field.Header.PARENT.isValid(subject));
        }
    
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"   ", "\t"})
        void invalidEmptyOrText(String subject) {
            assertFalse(Field.Header.WATERMARK.isValid(subject));
            assertFalse(Field.Header.DISPLAY_FORMAT.isValid(subject));
        }
    
        @ParameterizedTest
        @ValueSource(strings = { "", "foobar", "My name is Hase, I know about nothing."})
        void validEmptyOrText(String subject) {
            assertTrue(Field.Header.WATERMARK.isValid(subject));
            assertTrue(Field.Header.DISPLAY_FORMAT.isValid(subject));
        }
    
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "true", "false", "0", "1", "foobar"})
        void invalidBoolean(String subject) {
            assertFalse(Field.Header.ADVANCED_SEARCH_FIELD.isValid(subject));
            assertFalse(Field.Header.ALLOW_CONTROLLED_VOCABULARY.isValid(subject));
            assertFalse(Field.Header.ALLOW_MULTIPLES.isValid(subject));
            assertFalse(Field.Header.FACETABLE.isValid(subject));
            assertFalse(Field.Header.DISPLAY_ON_CREATE.isValid(subject));
            assertFalse(Field.Header.REQUIRED.isValid(subject));
        }
    
        @ParameterizedTest
        @ValueSource(strings = { "TRUE", "FALSE" })
        void validBoolean(String subject) {
            assertTrue(Field.Header.ADVANCED_SEARCH_FIELD.isValid(subject));
            assertTrue(Field.Header.ALLOW_CONTROLLED_VOCABULARY.isValid(subject));
            assertTrue(Field.Header.ALLOW_MULTIPLES.isValid(subject));
            assertTrue(Field.Header.FACETABLE.isValid(subject));
            assertTrue(Field.Header.DISPLAY_ON_CREATE.isValid(subject));
            assertTrue(Field.Header.REQUIRED.isValid(subject));
        }
    }
    
    @Nested
    class HeaderLineTest {
        @ParameterizedTest
        @ValueSource(strings = {
            validHeaderLine,
            "#datasetfield\tName\tTITLE\tdescription\tWAtermark\tfieldType\tdisplayOrder\tdisplayFormat" +
                "\tadvancedSearchField\tallowControlledVocabulary\tallowmultiples\tfacetable" +
                "\tdisplayOnCreate\trequired\tparent\tmetadataBLOCK_ID\ttermUri"
        })
        void successfulParseAndValidateHeaderLine(String headerLine) throws ParserException {
            List<Field.Header> headers = Field.Header.parseAndValidate(headerLine, config);
            assertFalse(headers.isEmpty());
            assertEquals(List.of(Field.Header.values()), headers);
        }
        
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
            "hello",
            "datasetField",
            "#datasetField test",
            "#datasetField\tname\ttitle\tdescription\twatermark\tfieldType",
            "#datasetField\tname\ttitle\tdescription\twatermark\tfieldType\t\tdisplayoncreate\trequired\tparent\tmetadatablock_id\ttermURI"
        })
        void failingParseAndValidateHeaderLine(String headerLine) throws ParserException {
            ParserException exception = assertThrows(ParserException.class, () -> Field.Header.parseAndValidate(headerLine, config));
            assertTrue(exception.hasSubExceptions());
            logger.log(Level.FINE,
                exception.getSubExceptions().stream().map(Throwable::getMessage).collect(Collectors.joining("\n"))
            );
        }
    }
    
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ParseLineTest {
        Field.FieldsBuilder builder;
        
        @BeforeEach
        void setUp() throws ParserException {
            builder = new Field.FieldsBuilder(validHeaderLine, validContainingBlockName, config);
        }
        
        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("invalidFieldExamples")
        void failingParseLine(String line) throws ParserException {
            ParserException exception = assertThrows(ParserException.class, () -> builder.parseAndValidateLine(0, line));
            //assertFalse(builder.hasSucceeded());
        }
        
        @ParameterizedTest
        @ValueSource(strings = {validFieldDef})
        @MethodSource("validFieldExamples")
        void succeedingParseLine(String line) throws ParserException {
            try {
                System.out.println(line);
                builder.parseAndValidateLine(0, line);
            } catch (ParserException e) {
                e.getSubExceptions().forEach(System.out::println);
                fail(e);
            }
            //assertTrue(builder.hasSucceeded());
        }
    
        Stream<String> validFieldExamples() throws IOException {
            Path file = Path.of("", "src/test/resources", "fields", "valid_fields.csv");
            return Files.readAllLines(file, StandardCharsets.UTF_8).stream().map(s -> s.replaceAll(";", "\t"));
        }
    
        Stream<String> invalidFieldExamples() throws IOException {
            // TODO: write a file with such examples that fail already at parsing the line
            Path file = Path.of("", "src/test/resources", "fields", "invalid_fields.csv");
            return Files.readAllLines(file, StandardCharsets.UTF_8).stream().map(s -> s.replaceAll(";", "\t"));
        }
    }
    
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class BuildFieldListTest {
        // TODO: write tests for the checks at build time
    }
}