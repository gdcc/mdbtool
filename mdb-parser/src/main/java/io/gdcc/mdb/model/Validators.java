package io.gdcc.mdb.model;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Validators {
    
    /**
     * This pattern enforces the spec for metadata blocks:
     *   - Must have a small letter at start
     *   - May contain single underscores to separate name parts
     *   - May use camelCase, but not PascalCase or ALLCAPS
     *   - Otherwise combinations of letters, numbers and underscores are allowed
     */
    public static final String BLOCK_NAME_PATTERN = "^[a-z](?!.*__+)(?!.*[A-Z][A-Z]+)[A-Za-z\\d_]+$";
    public static final Predicate<String> BLOCK_NAME = Pattern.compile(BLOCK_NAME_PATTERN).asMatchPredicate();
    
    /**
     * This pattern is derived from edu.harvard.iq.dataverse.Dataverse class field "alias".
     * In addition to not allowing all numbers aliases, it also forbids _ or - at the end using negative lookaheads.
     */
    public static final String DV_COLLECTION_ALIAS_PATTERN = "^(?!\\d+$)(?!.*[_-]+$)[a-zA-Z0-9_-]+$";
    public static final Predicate<String> DV_COLLECTION_ALIAS = Pattern.compile(DV_COLLECTION_ALIAS_PATTERN).asMatchPredicate();
    
    /**
     * Test if a given value is a valid {@link java.net.URL}
     *
     * Remember, Java only supports HTTP/S, file and JAR protocols by default!
     * Any URL not using such a protocol will not be considered a valid URL!
     * {@see <a href="https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URL.html#%3Cinit%3E(java.lang.String,java.lang.String,int,java.lang.String)">URL Constructor Summary</a>}
     *
     * @param url The value to test
     * @return True if valid URL, false otherwise
     */
    public static boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
    
    
    
}
