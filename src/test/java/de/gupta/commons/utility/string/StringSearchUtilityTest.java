package de.gupta.commons.utility.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StringSearchUtility Tests")
class StringSearchUtilityTest
{
    record AfterSearchStringTestCase(String input, String searchString, String expected, String description) {}

    private record FirstNonBlankTestCase(SequencedCollection<String> input, Optional<String> expected,
                                         String description)
    {
    }

    private record FirstNonBlankOrderTestCase(List<String> input, Optional<String> expected, String description)
    {
    }

    @Nested
    @DisplayName("afterSearchString - Normal Cases")
    class AfterSearchStringNormalCasesTests
    {
        @ParameterizedTest(name = "{3}")
        @MethodSource("afterSearchStringNormalCasesProvider")
        @DisplayName("Should return substring after search string when found")
        void afterSearchString_normalCases(String input, String searchString, String expected, String description)
        {
            String result = StringSearchUtility.afterSearchString(input, searchString);

            assertThat(result)
                .as("Result for input '%s' with search string '%s'", input, searchString)
                .isEqualTo(expected);
        }

        private static Stream<Arguments> afterSearchStringNormalCasesProvider()
        {
            return Stream.of(
                new AfterSearchStringTestCase("Hello World", "Hello ", "World", "Basic case - search string at beginning"),
                new AfterSearchStringTestCase("Hello World", "o ", "World", "Search string in middle"),
                new AfterSearchStringTestCase("Hello World Hello", "World ", "Hello", "Multiple occurrences - returns after first match"),
                new AfterSearchStringTestCase("prefix-content-suffix", "prefix-", "content-suffix", "Search string with hyphen"),
                new AfterSearchStringTestCase("one,two,three", ",", "two,three", "First delimiter in CSV"),
                new AfterSearchStringTestCase("one,two,three", "two,", "three", "Middle delimiter in CSV"),
                new AfterSearchStringTestCase("  leading whitespace", "  ", "leading whitespace", "Search string with whitespace"),
                new AfterSearchStringTestCase("camelCaseText", "camel", "CaseText", "CamelCase text"),
                new AfterSearchStringTestCase("snake_case_text", "snake_", "case_text", "Snake case text"),
                new AfterSearchStringTestCase("Text with (parentheses)", "(", "parentheses)", "Text with parentheses"),
                new AfterSearchStringTestCase("Text with multiple   spaces", "  ", " spaces", "Multiple spaces in search string")
            ).map(testCase -> Arguments.of(testCase.input(), testCase.searchString(), testCase.expected(), testCase.description()));
        }
    }

    @Nested
    @DisplayName("afterSearchString - Edge Cases")
    class AfterSearchStringEdgeCasesTests
    {
        @ParameterizedTest(name = "{3}")
        @MethodSource("afterSearchStringEdgeCasesProvider")
        @DisplayName("Should handle edge cases appropriately")
        void afterSearchString_edgeCases(String input, String searchString, String expected, String description)
        {
            String result = StringSearchUtility.afterSearchString(input, searchString);

            assertThat(result)
                .as("Result for input '%s' with search string '%s'", input, searchString)
                .isEqualTo(expected);
        }

        private static Stream<Arguments> afterSearchStringEdgeCasesProvider()
        {
            return Stream.of(
                new AfterSearchStringTestCase("Hello World", "NotFound", "Hello World", "Search string not found - returns original input"),
                new AfterSearchStringTestCase("Hello World", "", "Hello World", "Empty search string - returns original input"),
                new AfterSearchStringTestCase("", "Hello", "", "Empty input string - returns empty string"),
                new AfterSearchStringTestCase("", "", "", "Both input and search strings empty - returns empty string"),
                new AfterSearchStringTestCase("Hello", "Hello", "", "Search string equals input - returns empty string"),
                new AfterSearchStringTestCase("Hello World", "World", "", "Search string at end - returns empty string"),
                new AfterSearchStringTestCase("Hello World", "Hello World", "", "Search string equals entire input - returns empty string"),
                new AfterSearchStringTestCase("aaa", "a", "aa", "Single character search string with multiple occurrences"),
                new AfterSearchStringTestCase("Hello World", "o W", "orld", "Search string with space"),
                new AfterSearchStringTestCase("Hello\nWorld", "\n", "World", "Search string is newline character"),
                new AfterSearchStringTestCase("abcdefg", "abc", "defg", "Search string at beginning of input"),
                new AfterSearchStringTestCase("abcdefgabc", "abc", "defgabc", "Search string appears multiple times - first occurrence used")
            ).map(testCase -> Arguments.of(testCase.input(), testCase.searchString(), testCase.expected(), testCase.description()));
        }
    }

    @Nested
    @DisplayName("afterSearchString - Special Character Cases")
    class AfterSearchStringSpecialCharacterCasesTests
    {
        @ParameterizedTest(name = "{3}")
        @MethodSource("afterSearchStringSpecialCharacterCasesProvider")
        @DisplayName("Should handle special characters correctly")
        void afterSearchString_specialCharacterCases(String input, String searchString, String expected, String description)
        {
            String result = StringSearchUtility.afterSearchString(input, searchString);

            assertThat(result)
                .as("Result for input '%s' with search string '%s'", input, searchString)
                .isEqualTo(expected);
        }

        private static Stream<Arguments> afterSearchStringSpecialCharacterCasesProvider()
        {
            return Stream.of(
                new AfterSearchStringTestCase("Text with \ttab", "\t", "tab", "Tab character in search string"),
                new AfterSearchStringTestCase("Line1\nLine2", "\n", "Line2", "Newline character in search string"),
                new AfterSearchStringTestCase("Text with \r\n Windows newline", "\r\n", " Windows newline", "Windows newline in search string"),
                new AfterSearchStringTestCase("Text with \\backslash", "\\", "backslash", "Backslash in search string"),
                new AfterSearchStringTestCase("Text with \"quotes\"", "\"", "quotes\"", "Double quote in search string"),
                new AfterSearchStringTestCase("Text with 'quotes'", "'", "quotes'", "Single quote in search string"),
                new AfterSearchStringTestCase("Text with $pecial characters", "$", "pecial characters", "Dollar sign in search string"),
                new AfterSearchStringTestCase("Text with regex chars: .*+?^${}()|[]", "regex chars: ", ".*+?^${}()|[]", "Regex special characters after search string"),
                new AfterSearchStringTestCase("Unicode: こんにちは世界", "こんにちは", "世界", "Unicode characters in search string"),
                new AfterSearchStringTestCase("Emoji: 😀😃😄", "😀", "😃😄", "Emoji in search string"),
                new AfterSearchStringTestCase("HTML: <div>content</div>", "<div>", "content</div>", "HTML tags in search string"),
                new AfterSearchStringTestCase("XML: <tag attr=\"value\"/>", "attr=\"", "value\"/>", "XML attributes in search string"),
                new AfterSearchStringTestCase("Path: C:\\Program Files\\App", "C:\\", "Program Files\\App", "Windows path with backslashes")
            ).map(testCase -> Arguments.of(testCase.input(), testCase.searchString(), testCase.expected(), testCase.description()));
        }
    }

    @Nested
    @DisplayName("afterSearchString - Case Sensitivity Tests")
    class AfterSearchStringCaseSensitivityTests
    {
        @ParameterizedTest(name = "{3}")
        @MethodSource("afterSearchStringCaseSensitivityProvider")
        @DisplayName("Should respect case sensitivity")
        void afterSearchString_caseSensitivity(String input, String searchString, String expected, String description)
        {
            String result = StringSearchUtility.afterSearchString(input, searchString);

            assertThat(result)
                .as("Result for input '%s' with search string '%s'", input, searchString)
                .isEqualTo(expected);
        }

        private static Stream<Arguments> afterSearchStringCaseSensitivityProvider()
        {
            return Stream.of(
                new AfterSearchStringTestCase("Hello World", "hello", "Hello World", "Lowercase search not found in mixed case input"),
                new AfterSearchStringTestCase("Hello World", "HELLO", "Hello World", "Uppercase search not found in mixed case input"),
                new AfterSearchStringTestCase("HELLO WORLD", "hello", "HELLO WORLD", "Lowercase search not found in uppercase input"),
                new AfterSearchStringTestCase("hello world", "Hello", "hello world", "Mixed case search not found in lowercase input"),
                new AfterSearchStringTestCase("Hello World", "Hello", " World", "Exact case match found"),
                new AfterSearchStringTestCase("camelCaseExample", "camelCase", "Example", "CamelCase exact match"),
                new AfterSearchStringTestCase("camelCaseExample", "CamelCase", "camelCaseExample", "CamelCase wrong case - not found")
            ).map(testCase -> Arguments.of(testCase.input(), testCase.searchString(), testCase.expected(), testCase.description()));
        }
    }

    @Nested
    @DisplayName("afterSearchString - Null Handling Tests")
    class AfterSearchStringNullHandlingTests
    {
        @ParameterizedTest(name = "When input is null")
        @NullSource
        @DisplayName("Should throw NullPointerException when input is null")
        void afterSearchString_nullInput(String input)
        {
            assertThatThrownBy(() -> StringSearchUtility.afterSearchString(input, "search"))
                .as("Should throw NullPointerException when input is null")
                .isInstanceOf(NullPointerException.class);
        }

        @ParameterizedTest(name = "When search string is null")
        @NullSource
        @DisplayName("Should throw NullPointerException when search string is null")
        void afterSearchString_nullSearchString(String searchString)
        {
            assertThatThrownBy(() -> StringSearchUtility.afterSearchString("input", searchString))
                .as("Should throw NullPointerException when search string is null")
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("afterSearchString - Performance Edge Cases")
    class AfterSearchStringPerformanceEdgeCasesTests
    {
        @ParameterizedTest(name = "{3}")
        @MethodSource("afterSearchStringPerformanceEdgeCasesProvider")
        @DisplayName("Should handle performance edge cases correctly")
        void afterSearchString_performanceEdgeCases(String input, String searchString, String expected, String description)
        {
            String result = StringSearchUtility.afterSearchString(input, searchString);

            assertThat(result)
                .as("Result for performance edge case: %s", description)
                .isEqualTo(expected);
        }

        private static Stream<Arguments> afterSearchStringPerformanceEdgeCasesProvider()
        {
            // Create some larger strings for performance testing
            String longInput = "a".repeat(1000) + "MARKER" + "b".repeat(1000);
            String longSearchString = "a".repeat(1000) + "MARKER";
            String longRepeatedChar = "a".repeat(2000);
            String searchInLongRepeated = "a".repeat(1000);
            String veryLongInput = "x".repeat(10000) + "NEEDLE" + "y".repeat(10000);
            String longPrefix = "prefix".repeat(1000);
            String longSuffix = "suffix".repeat(1000);
            String complexInput = longPrefix + "TARGET" + longSuffix;

            return Stream.of(
                new AfterSearchStringTestCase(longInput, "MARKER", "b".repeat(1000), "Long input with marker in middle"),
                new AfterSearchStringTestCase(longInput, longSearchString, "b".repeat(1000), "Long search string"),
                new AfterSearchStringTestCase(longRepeatedChar, searchInLongRepeated, "a".repeat(1000), "Search in long repeated characters"),
                new AfterSearchStringTestCase(longInput, "NOTFOUND", longInput, "Long input with search string not found"),
                new AfterSearchStringTestCase(veryLongInput, "NEEDLE", "y".repeat(10000), "Very long input (20K+ chars)"),
                new AfterSearchStringTestCase(complexInput, "TARGET", "suffix".repeat(1000), "Complex input with long prefix and suffix")
            ).map(testCase -> Arguments.of(testCase.input(), testCase.searchString(), testCase.expected(), testCase.description()));
        }
    }

    @Nested
    @DisplayName("firstNonBlank - Normal Cases")
    class FirstNonBlankNormalCasesTests
    {
        @ParameterizedTest(name = "{2}")
        @MethodSource("firstNonBlankNormalCasesProvider")
        @DisplayName("Should return the first non-blank string from the collection")
        void firstNonBlank_normalCases(SequencedCollection<String> input, Optional<String> expected, String description)
        {
            Optional<String> result = StringSearchUtility.firstNonBlank(input);

            assertThat(result)
                    .as(description)
                    .isEqualTo(expected);
        }

        private static Stream<Arguments> firstNonBlankNormalCasesProvider()
        {
            return Stream.of(
                    new FirstNonBlankTestCase(
                            List.of("hello", "world"),
                            Optional.of("hello"),
                            "List with two non-blank strings should return the first one"),
                    new FirstNonBlankTestCase(
                            List.of("apple", "banana", "cherry"),
                            Optional.of("apple"),
                            "List with three non-blank strings should return the first one"),
                    new FirstNonBlankTestCase(
                            List.of("only"),
                            Optional.of("only"),
                            "Singleton list with non-blank string should return that string"),
                    new FirstNonBlankTestCase(
                            List.of("a"),
                            Optional.of("a"),
                            "Singleton list with single character should return it"),
                    new FirstNonBlankTestCase(
                            List.of("  leading", "trailing  "),
                            Optional.of("  leading"),
                            "Strings with leading/trailing spaces are non-blank and first should be returned"),
                    new FirstNonBlankTestCase(
                            new LinkedList<>(List.of("linked", "list", "elements")),
                            Optional.of("linked"),
                            "LinkedList as SequencedCollection should return first element"),
                    new FirstNonBlankTestCase(
                            new ArrayDeque<>(List.of("deque", "elements")),
                            Optional.of("deque"),
                            "ArrayDeque as SequencedCollection should return first element"),
                    new FirstNonBlankTestCase(
                            List.of("Unicode: こんにちは", "世界"),
                            Optional.of("Unicode: こんにちは"),
                            "Unicode strings should be treated as non-blank"),
                    new FirstNonBlankTestCase(
                            List.of("123", "456"),
                            Optional.of("123"),
                            "Numeric strings should be treated as non-blank"),
                    new FirstNonBlankTestCase(
                            List.of("!@#$%", "^&*()"),
                            Optional.of("!@#$%"),
                            "Special character strings should be treated as non-blank")
            ).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
        }
    }

    @Nested
    @DisplayName("firstNonBlank - Edge Cases")
    class FirstNonBlankEdgeCasesTests
    {
        @ParameterizedTest(name = "{2}")
        @MethodSource("firstNonBlankEdgeCasesProvider")
        @DisplayName("Should handle edge cases appropriately")
        void firstNonBlank_edgeCases(SequencedCollection<String> input, Optional<String> expected, String description)
        {
            Optional<String> result = StringSearchUtility.firstNonBlank(input);

            assertThat(result)
                    .as(description)
                    .isEqualTo(expected);
        }

        private static Stream<Arguments> firstNonBlankEdgeCasesProvider()
        {
            return Stream.of(
                    new FirstNonBlankTestCase(
                            List.of(),
                            Optional.empty(),
                            "Empty list should return empty Optional"),
                    new FirstNonBlankTestCase(
                            new LinkedList<>(),
                            Optional.empty(),
                            "Empty LinkedList should return empty Optional"),
                    new FirstNonBlankTestCase(
                            new ArrayDeque<>(),
                            Optional.empty(),
                            "Empty ArrayDeque should return empty Optional"),
                    new FirstNonBlankTestCase(
                            List.of(""),
                            Optional.empty(),
                            "List with single empty string should return empty Optional"),
                    new FirstNonBlankTestCase(
                            List.of("", "", ""),
                            Optional.empty(),
                            "List with only empty strings should return empty Optional"),
                    new FirstNonBlankTestCase(
                            List.of(" "),
                            Optional.empty(),
                            "List with single space-only string should return empty Optional"),
                    new FirstNonBlankTestCase(
                            List.of(" ", "  ", "   "),
                            Optional.empty(),
                            "List with only space strings of varying lengths should return empty Optional"),
                    new FirstNonBlankTestCase(
                            List.of("\t"),
                            Optional.empty(),
                            "List with tab-only string should return empty Optional"),
                    new FirstNonBlankTestCase(
                            List.of("\n"),
                            Optional.empty(),
                            "List with newline-only string should return empty Optional"),
                    new FirstNonBlankTestCase(
                            List.of("\r"),
                            Optional.empty(),
                            "List with carriage-return-only string should return empty Optional"),
                    new FirstNonBlankTestCase(
                            List.of("\t\n\r "),
                            Optional.empty(),
                            "List with mixed whitespace string should return empty Optional"),
                    new FirstNonBlankTestCase(
                            List.of("", " ", "\t", "\n", "\r\n", "  \t  "),
                            Optional.empty(),
                            "List with various blank strings should return empty Optional"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList(null, null, null)),
                            Optional.empty(),
                            "List with only null elements should return empty Optional"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Collections.singletonList(null)),
                            Optional.empty(),
                            "Singleton list with null should return empty Optional"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList(null, "", " ", "\t")),
                            Optional.empty(),
                            "List with nulls and blank strings should return empty Optional")
            ).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
        }
    }

    @Nested
    @DisplayName("firstNonBlank - Order Preservation Tests")
    class FirstNonBlankOrderPreservationTests
    {
        @ParameterizedTest(name = "{2}")
        @MethodSource("firstNonBlankOrderPreservationProvider")
        @DisplayName("Should return the first non-blank string respecting insertion order")
        void firstNonBlank_orderPreservation(List<String> input, Optional<String> expected, String description)
        {
            Optional<String> result = StringSearchUtility.firstNonBlank(input);

            assertThat(result)
                    .as(description)
                    .isEqualTo(expected);
        }

        private static Stream<Arguments> firstNonBlankOrderPreservationProvider()
        {
            return Stream.of(
                    // --- Blank prefix, single non-blank candidate ---
                    new FirstNonBlankOrderTestCase(
                            List.of("alpha", "beta"),
                            Optional.of("alpha"),
                            "Two non-blank strings: should return the first"),
                    new FirstNonBlankOrderTestCase(
                            List.of("beta", "alpha"),
                            Optional.of("beta"),
                            "Two non-blank strings reversed: should return the first"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "first")),
                            Optional.of("first"),
                            "Empty string before non-blank: should skip empty and return non-blank"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(" ", "first")),
                            Optional.of("first"),
                            "Space-only string before non-blank: should skip blank and return non-blank"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("\t", "first")),
                            Optional.of("first"),
                            "Tab-only string before non-blank: should skip blank and return non-blank"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("\n", "first")),
                            Optional.of("first"),
                            "Newline-only string before non-blank: should skip blank and return non-blank"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, "first")),
                            Optional.of("first"),
                            "Null before non-blank: should skip null and return non-blank"),

                    // --- Multiple blanks before first non-blank ---
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "", "first")),
                            Optional.of("first"),
                            "Two empty strings before non-blank: should skip both"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", " ", "first")),
                            Optional.of("first"),
                            "Empty then space before non-blank: should skip both"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, "", "first")),
                            Optional.of("first"),
                            "Null then empty before non-blank: should skip both"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, " ", "first")),
                            Optional.of("first"),
                            "Null then space before non-blank: should skip both"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, null, "first")),
                            Optional.of("first"),
                            "Two nulls before non-blank: should skip both"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", " ", "\t", "first")),
                            Optional.of("first"),
                            "Empty, space, tab before non-blank: should skip all three"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, "", " ", "\t", "\n", "first")),
                            Optional.of("first"),
                            "Null, empty, space, tab, newline before non-blank: should skip all five"),

                    // --- First non-blank chosen over later non-blanks ---
                    new FirstNonBlankOrderTestCase(
                            List.of("first", "second", "third"),
                            Optional.of("first"),
                            "Three non-blanks: should return the first"),
                    new FirstNonBlankOrderTestCase(
                            List.of("third", "first", "second"),
                            Optional.of("third"),
                            "Three non-blanks scrambled: should return the first in insertion order"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "second", "third")),
                            Optional.of("second"),
                            "Empty then two non-blanks: should return second (first non-blank)"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, "second", "third")),
                            Optional.of("second"),
                            "Null then two non-blanks: should return second (first non-blank)"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(" ", "second", "third")),
                            Optional.of("second"),
                            "Space then two non-blanks: should return second (first non-blank)"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "", "third", "fourth")),
                            Optional.of("third"),
                            "Two empties then two non-blanks: should return third (first non-blank)"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, "", " ", "fourth", "fifth")),
                            Optional.of("fourth"),
                            "Null, empty, space then two non-blanks: should return fourth (first non-blank)"),

                    // --- Non-blank at different positions (index 0 through 5) ---
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("at-index-0", "", "", "", "", "")),
                            Optional.of("at-index-0"),
                            "Non-blank at index 0: should return it"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "at-index-1", "", "", "", "")),
                            Optional.of("at-index-1"),
                            "Non-blank at index 1: should return it"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "", "at-index-2", "", "", "")),
                            Optional.of("at-index-2"),
                            "Non-blank at index 2: should return it"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "", "", "at-index-3", "", "")),
                            Optional.of("at-index-3"),
                            "Non-blank at index 3: should return it"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "", "", "", "at-index-4", "")),
                            Optional.of("at-index-4"),
                            "Non-blank at index 4: should return it"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "", "", "", "", "at-index-5")),
                            Optional.of("at-index-5"),
                            "Non-blank at index 5: should return it"),

                    // --- Non-blank at different positions with nulls ---
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("at-index-0", null, null, null)),
                            Optional.of("at-index-0"),
                            "Non-blank at index 0 among nulls: should return it"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, "at-index-1", null, null)),
                            Optional.of("at-index-1"),
                            "Non-blank at index 1 among nulls: should return it"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, null, "at-index-2", null)),
                            Optional.of("at-index-2"),
                            "Non-blank at index 2 among nulls: should return it"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, null, null, "at-index-3")),
                            Optional.of("at-index-3"),
                            "Non-blank at index 3 among nulls: should return it"),

                    // --- Non-blank at different positions with mixed blanks ---
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("at-index-0", null, "", " ", "\t")),
                            Optional.of("at-index-0"),
                            "Non-blank at index 0 among mixed blanks: should return it"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, "at-index-1", "", " ", "\t")),
                            Optional.of("at-index-1"),
                            "Non-blank at index 1 among mixed blanks: should return it"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, "", "at-index-2", " ", "\t")),
                            Optional.of("at-index-2"),
                            "Non-blank at index 2 among mixed blanks: should return it"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, "", " ", "at-index-3", "\t")),
                            Optional.of("at-index-3"),
                            "Non-blank at index 3 among mixed blanks: should return it"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, "", " ", "\t", "at-index-4")),
                            Optional.of("at-index-4"),
                            "Non-blank at index 4 among mixed blanks: should return it"),

                    // --- Verifying first non-blank wins when multiple non-blanks present ---
                    new FirstNonBlankOrderTestCase(
                            List.of("winner", "loser"),
                            Optional.of("winner"),
                            "First of two non-blanks should be returned"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "winner", "loser")),
                            Optional.of("winner"),
                            "After one blank, first non-blank should be returned, not the second"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", " ", "winner", "loser")),
                            Optional.of("winner"),
                            "After two blanks, first non-blank should be returned, not the second"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, "winner", null, "loser")),
                            Optional.of("winner"),
                            "Interleaved nulls: first non-blank should be returned"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "winner", "", "loser", "")),
                            Optional.of("winner"),
                            "Interleaved empties: first non-blank should be returned"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, "", " ", "winner", "\t", "loser", "\n")),
                            Optional.of("winner"),
                            "Complex interleaving of blanks and non-blanks: first non-blank should be returned"),

                    // --- Strings that look blank but are not ---
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", " a")),
                            Optional.of(" a"),
                            "String with leading space and content is non-blank"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "a ")),
                            Optional.of("a "),
                            "String with trailing space and content is non-blank"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(" ", "\t", ".", "x")),
                            Optional.of("."),
                            "Single dot is non-blank and should be returned before 'x'"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "\u00A0")),
                            Optional.of("\u00A0"),
                            "Non-breaking space (U+00A0) is not blank per Java's isBlank(), should be returned")
            ).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
        }
    }

    @Nested
    @DisplayName("firstNonBlank - SequencedCollection Implementations")
    class FirstNonBlankSequencedCollectionTests
    {
        @ParameterizedTest(name = "{2}")
        @MethodSource("firstNonBlankSequencedCollectionProvider")
        @DisplayName("Should work correctly with various SequencedCollection implementations")
        void firstNonBlank_sequencedCollections(SequencedCollection<String> input, Optional<String> expected,
                                                String description)
        {
            Optional<String> result = StringSearchUtility.firstNonBlank(input);

            assertThat(result)
                    .as(description)
                    .isEqualTo(expected);
        }

        private static Stream<Arguments> firstNonBlankSequencedCollectionProvider()
        {
            return Stream.of(
                    new FirstNonBlankTestCase(
                            List.of("list-first", "list-second"),
                            Optional.of("list-first"),
                            "ArrayList (via List.of): should respect insertion order"),
                    new FirstNonBlankTestCase(
                            new LinkedList<>(List.of("linked-first", "linked-second")),
                            Optional.of("linked-first"),
                            "LinkedList: should respect insertion order"),
                    new FirstNonBlankTestCase(
                            new ArrayDeque<>(List.of("deque-first", "deque-second")),
                            Optional.of("deque-first"),
                            "ArrayDeque: should respect insertion order"),
                    new FirstNonBlankTestCase(
                            new LinkedHashSet<>(List.of("set-first", "set-second")),
                            Optional.of("set-first"),
                            "LinkedHashSet: should respect insertion order"),
                    new FirstNonBlankTestCase(
                            new TreeSet<>(List.of("b-second", "a-first")),
                            Optional.of("a-first"),
                            "TreeSet: should respect natural (sorted) order, returning 'a-first'"),
                    new FirstNonBlankTestCase(
                            new LinkedList<>(Arrays.asList("", "linked-second")),
                            Optional.of("linked-second"),
                            "LinkedList with blank first element: should skip to second"),
                    new FirstNonBlankTestCase(
                            new ArrayDeque<>(List.of(" ", "deque-second")),
                            Optional.of("deque-second"),
                            "ArrayDeque with blank first element: should skip to second"),
                    new FirstNonBlankTestCase(
                            new LinkedHashSet<>(Arrays.asList("", " ", "set-third")),
                            Optional.of("set-third"),
                            "LinkedHashSet with blank elements: should skip blanks and return first non-blank")
            ).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
        }
    }

    @Nested
    @DisplayName("firstNonBlank - Blank String Variations")
    class FirstNonBlankBlankVariationsTests
    {
        @ParameterizedTest(name = "{2}")
        @MethodSource("firstNonBlankBlankVariationsProvider")
        @DisplayName("Should correctly identify various forms of blank strings")
        void firstNonBlank_blankVariations(SequencedCollection<String> input, Optional<String> expected,
                                           String description)
        {
            Optional<String> result = StringSearchUtility.firstNonBlank(input);

            assertThat(result)
                    .as(description)
                    .isEqualTo(expected);
        }

        private static Stream<Arguments> firstNonBlankBlankVariationsProvider()
        {
            return Stream.of(
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("", "content")),
                            Optional.of("content"),
                            "Empty string is blank: should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList(" ", "content")),
                            Optional.of("content"),
                            "Single space is blank: should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("  ", "content")),
                            Optional.of("content"),
                            "Two spaces is blank: should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("   ", "content")),
                            Optional.of("content"),
                            "Three spaces is blank: should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("\t", "content")),
                            Optional.of("content"),
                            "Tab is blank: should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("\n", "content")),
                            Optional.of("content"),
                            "Newline is blank: should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("\r", "content")),
                            Optional.of("content"),
                            "Carriage return is blank: should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("\r\n", "content")),
                            Optional.of("content"),
                            "CRLF is blank: should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("\f", "content")),
                            Optional.of("content"),
                            "Form feed is blank: should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("\t\n\r\f ", "content")),
                            Optional.of("content"),
                            "Mixed whitespace characters is blank: should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("\u2000", "content")),
                            Optional.of("content"),
                            "Unicode EN QUAD (U+2000) is blank per isBlank(): should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("\u2001", "content")),
                            Optional.of("content"),
                            "Unicode EM QUAD (U+2001) is blank per isBlank(): should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("\u2003", "content")),
                            Optional.of("content"),
                            "Unicode EM SPACE (U+2003) is blank per isBlank(): should skip it"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("\u200B", "content")),
                            Optional.of("\u200B"),
                            "Unicode ZERO WIDTH SPACE (U+200B) is not blank per isBlank(): should be returned")
            ).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
        }
    }

    @Nested
    @DisplayName("firstNonBlank - Null Handling Tests")
    class FirstNonBlankNullHandlingTests
    {
        @Test
        @DisplayName("Should throw NullPointerException when collection is null")
        void firstNonBlank_nullCollection()
        {
            assertThatThrownBy(() -> StringSearchUtility.firstNonBlank(null))
                    .as("Should throw NullPointerException when collection is null")
                    .isInstanceOf(NullPointerException.class);
        }

        @ParameterizedTest(name = "{2}")
        @MethodSource("firstNonBlankNullElementsProvider")
        @DisplayName("Should handle null elements within the collection")
        void firstNonBlank_nullElements(SequencedCollection<String> input, Optional<String> expected,
                                        String description)
        {
            Optional<String> result = StringSearchUtility.firstNonBlank(input);

            assertThat(result)
                    .as(description)
                    .isEqualTo(expected);
        }

        private static Stream<Arguments> firstNonBlankNullElementsProvider()
        {
            return Stream.of(
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList(null, "after-null")),
                            Optional.of("after-null"),
                            "Null followed by non-blank: should skip null and return non-blank"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList(null, null, "after-two-nulls")),
                            Optional.of("after-two-nulls"),
                            "Two nulls followed by non-blank: should skip nulls and return non-blank"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList(null, "", null, "found")),
                            Optional.of("found"),
                            "Mixed nulls and empty strings before non-blank: should skip all and return non-blank"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList(null, " ", null, "\t", "found")),
                            Optional.of("found"),
                            "Mixed nulls and whitespace before non-blank: should skip all and return non-blank"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("non-blank", null)),
                            Optional.of("non-blank"),
                            "Non-blank followed by null: should return the non-blank"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList("non-blank", null, "another")),
                            Optional.of("non-blank"),
                            "Non-blank, null, non-blank: should return the first non-blank"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Collections.singletonList(null)),
                            Optional.empty(),
                            "Singleton list with null: should return empty Optional"),
                    new FirstNonBlankTestCase(
                            new ArrayList<>(Arrays.asList(null, null, null)),
                            Optional.empty(),
                            "List with only nulls: should return empty Optional")
            ).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
        }
    }

    @Nested
    @DisplayName("firstNonBlank - Order Preservation with LinkedList")
    class FirstNonBlankLinkedListOrderTests
    {
        @ParameterizedTest(name = "{2}")
        @MethodSource("firstNonBlankLinkedListOrderProvider")
        @DisplayName("Should preserve insertion order in LinkedList")
        void firstNonBlank_linkedListOrder(List<String> input, Optional<String> expected, String description)
        {
            LinkedList<String> linkedList = new LinkedList<>(input);

            Optional<String> result = StringSearchUtility.firstNonBlank(linkedList);

            assertThat(result)
                    .as(description)
                    .isEqualTo(expected);
        }

        private static Stream<Arguments> firstNonBlankLinkedListOrderProvider()
        {
            return Stream.of(
                    new FirstNonBlankOrderTestCase(
                            List.of("first", "second"),
                            Optional.of("first"),
                            "LinkedList [first, second]: should return 'first'"),
                    new FirstNonBlankOrderTestCase(
                            List.of("second", "first"),
                            Optional.of("second"),
                            "LinkedList [second, first]: should return 'second' (insertion order)"),
                    new FirstNonBlankOrderTestCase(
                            List.of("z", "a", "m"),
                            Optional.of("z"),
                            "LinkedList [z, a, m]: should return 'z' (insertion order, not sorted)"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList("", "z", "a")),
                            Optional.of("z"),
                            "LinkedList ['', z, a]: should return 'z' (first non-blank by insertion order)"),
                    new FirstNonBlankOrderTestCase(
                            new ArrayList<>(Arrays.asList(null, " ", "\t", "z", "a", "m")),
                            Optional.of("z"),
                            "LinkedList [null, ' ', '\\t', z, a, m]: should return 'z' (first non-blank)")
            ).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
        }
    }

    @Nested
    @DisplayName("firstNonBlank - Order Preservation with ArrayDeque")
    class FirstNonBlankArrayDequeOrderTests
    {
        @ParameterizedTest(name = "{2}")
        @MethodSource("firstNonBlankArrayDequeOrderProvider")
        @DisplayName("Should preserve insertion order in ArrayDeque")
        void firstNonBlank_arrayDequeOrder(List<String> elementsToAdd, Optional<String> expected, String description)
        {
            ArrayDeque<String> deque = new ArrayDeque<>();
            for (String element : elementsToAdd)
            {
                if (element != null)
                {
                    deque.addLast(element);
                }
            }

            Optional<String> result = StringSearchUtility.firstNonBlank(deque);

            assertThat(result)
                    .as(description)
                    .isEqualTo(expected);
        }

        private static Stream<Arguments> firstNonBlankArrayDequeOrderProvider()
        {
            return Stream.of(
                    new FirstNonBlankOrderTestCase(
                            List.of("first", "second"),
                            Optional.of("first"),
                            "ArrayDeque addLast [first, second]: should return 'first'"),
                    new FirstNonBlankOrderTestCase(
                            List.of("second", "first"),
                            Optional.of("second"),
                            "ArrayDeque addLast [second, first]: should return 'second'"),
                    new FirstNonBlankOrderTestCase(
                            List.of("z", "a", "m"),
                            Optional.of("z"),
                            "ArrayDeque addLast [z, a, m]: should return 'z' (FIFO order)"),
                    new FirstNonBlankOrderTestCase(
                            List.of("", "z", "a"),
                            Optional.of("z"),
                            "ArrayDeque addLast ['', z, a]: should return 'z' (first non-blank in FIFO order)"),
                    new FirstNonBlankOrderTestCase(
                            List.of(" ", "\t", "z", "a"),
                            Optional.of("z"),
                            "ArrayDeque addLast [' ', '\\t', z, a]: should return 'z' (first non-blank)")
            ).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
        }
    }

    @Nested
    @DisplayName("firstNonBlank - Order Preservation with addFirst (reverse insertion)")
    class FirstNonBlankReverseInsertionOrderTests
    {
        @Test
        @DisplayName("LinkedList addFirst should result in reversed order, and firstNonBlank should respect that")
        void firstNonBlank_linkedListAddFirst()
        {
            LinkedList<String> list = new LinkedList<>();
            list.addFirst("third");
            list.addFirst("second");
            list.addFirst("first");

            Optional<String> result = StringSearchUtility.firstNonBlank(list);

            assertThat(result)
                    .as("LinkedList built with addFirst [third, second, first] should have 'first' at the front")
                    .isEqualTo(Optional.of("first"));
        }

        @Test
        @DisplayName("ArrayDeque addFirst should result in reversed order, and firstNonBlank should respect that")
        void firstNonBlank_arrayDequeAddFirst()
        {
            ArrayDeque<String> deque = new ArrayDeque<>();
            deque.addFirst("third");
            deque.addFirst("second");
            deque.addFirst("first");

            Optional<String> result = StringSearchUtility.firstNonBlank(deque);

            assertThat(result)
                    .as("ArrayDeque built with addFirst [third, second, first] should have 'first' at the front")
                    .isEqualTo(Optional.of("first"));
        }

        @Test
        @DisplayName("LinkedList addFirst with blanks at head: firstNonBlank should skip them")
        void firstNonBlank_linkedListAddFirstWithBlanks()
        {
            LinkedList<String> list = new LinkedList<>();
            list.addFirst("content");
            list.addFirst("   ");
            list.addFirst("");

            Optional<String> result = StringSearchUtility.firstNonBlank(list);

            assertThat(result)
                    .as("LinkedList ['', '   ', content] should return 'content'")
                    .isEqualTo(Optional.of("content"));
        }

        @Test
        @DisplayName("ArrayDeque addFirst with blanks at head: firstNonBlank should skip them")
        void firstNonBlank_arrayDequeAddFirstWithBlanks()
        {
            ArrayDeque<String> deque = new ArrayDeque<>();
            deque.addFirst("content");
            deque.addFirst("   ");
            deque.addFirst("");

            Optional<String> result = StringSearchUtility.firstNonBlank(deque);

            assertThat(result)
                    .as("ArrayDeque ['', '   ', content] should return 'content'")
                    .isEqualTo(Optional.of("content"));
        }
    }
}