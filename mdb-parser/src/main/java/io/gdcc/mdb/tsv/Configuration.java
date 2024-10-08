package io.gdcc.mdb.tsv;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Configuration {
    
    private final String comment;
    private final String trigger;
    private final String column;
    private final Matcher rtrimMatcher;
    private final Boolean allowDeepFieldNesting;
    
    public Configuration(
        String comment,
        String trigger,
        String column,
        boolean allowDeepFieldNesting
    ) {
        notNullNotEmpty("Comment indicator", comment);
        this.comment = comment;
    
        notNullNotEmpty("Triggering indicator (keyword prefix)", trigger);
        this.trigger = trigger;
    
        notNullNotEmpty("Column separator", column);
        this.column = column;
        
        this.rtrimMatcher = Pattern.compile("(" + this.column + ")+$").matcher("");
        
        this.allowDeepFieldNesting = allowDeepFieldNesting;
    }
    
    public static Configuration defaultConfig() {
        return new Configuration("%%", "#", "\t", false);
    }
    
    private static void notNullNotEmpty(String optionName, String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(optionName + " may not be null or empty");
        }
    }
    
    public String commentIndicator() {
        return comment;
    }
    
    public String triggerIndicator() {
        return trigger;
    }
    
    public String columnSeparator() {
        return column;
    }
    
    public String rtrimColumns(String line) {
        return line == null ? null : rtrimMatcher.reset(line).replaceAll("");
    }
    
    public String trigger(String keyword) {
        return this.triggerIndicator() + keyword;
    }
    
    public boolean deepFieldNestingEnabled() {
        return this.allowDeepFieldNesting;
    }
}
