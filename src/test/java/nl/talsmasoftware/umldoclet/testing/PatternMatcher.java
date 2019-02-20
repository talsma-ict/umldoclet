/*
 * Copyright 2016-2019 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.umldoclet.testing;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * A hamcrest matcher that matches regular expression patterns.
 *
 * @author Sjoerd Talsma
 */
public class PatternMatcher extends TypeSafeMatcher<String> {
    private final Pattern pattern;
    private final boolean fullMatch;

    protected PatternMatcher(Pattern pattern, boolean fullMatch) {
        this.pattern = requireNonNull(pattern, "No pattern to match.");
        this.fullMatch = fullMatch;
    }

    public static PatternMatcher containsPattern(Pattern pattern) {
        return new PatternMatcher(pattern, false);
    }

    public static PatternMatcher containsPattern(CharSequence pattern) {
        return containsPattern(pattern, 0);
    }

    public static PatternMatcher containsPattern(CharSequence pattern, int flags) {
        return containsPattern(Pattern.compile(requireNonNull(pattern, "No pattern to match.").toString(), flags));
    }

    public static PatternMatcher matchesPattern(Pattern pattern) {
        return new PatternMatcher(pattern, true);
    }

    public static PatternMatcher matchesPattern(CharSequence pattern) {
        return matchesPattern(pattern, 0);
    }

    public static PatternMatcher matchesPattern(CharSequence pattern, int flags) {
        return matchesPattern(Pattern.compile(requireNonNull(pattern, "No pattern to match.").toString(), flags));
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("a string " +
                (fullMatch ? "matching" : "containing") +
                " pattern \"" + pattern.pattern() + '"');
    }

    @Override
    public boolean matchesSafely(final String string) {
        final Matcher matcher = pattern.matcher(string);
        return fullMatch ? matcher.matches() : matcher.find();
    }

}
