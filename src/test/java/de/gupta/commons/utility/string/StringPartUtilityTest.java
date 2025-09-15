package de.gupta.commons.utility.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StringPartUtility Tests")
final class StringPartUtilityTest
{
	@Nested
	@DisplayName("firstN Tests")
	final class FirstNTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("validFirstNTestCases")
		@DisplayName("Test firstN method for valid inputs")
		void testFirstNValidInputs(String input, int n, String description, String expected)
		{
			String result = StringPartUtility.firstN(input, n);

			assertThat(result).as(description).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{2}")
		@MethodSource("edgeCaseTestCases")
		@DisplayName("Test firstN method for edge cases")
		void testFirstNEdgeCases(String input, int n, String description, String expected)
		{
			String result = StringPartUtility.firstN(input, n);

			assertThat(result).as(description).isEqualTo(expected);
		}

		@Test
		@DisplayName("Test firstN throws IllegalArgumentException for null input")
		void testFirstNNullInput()
		{
			assertThatThrownBy(() -> StringPartUtility.firstN(null, 5))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("n must be greater than zero");
		}

		@Test
		@DisplayName("Test firstN throws IllegalArgumentException for zero n")
		void testFirstNZeroN()
		{
			assertThatThrownBy(() -> StringPartUtility.firstN("hello", 0))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("n must be greater than zero");
		}

		@Test
		@DisplayName("Test firstN throws IllegalArgumentException for negative n")
		void testFirstNNegativeN()
		{
			assertThatThrownBy(() -> StringPartUtility.firstN("hello", -1))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("n must be greater than zero");
		}

		@Test
		@DisplayName("Test firstN throws IllegalArgumentException for large negative n")
		void testFirstNLargeNegativeN()
		{
			assertThatThrownBy(() -> StringPartUtility.firstN("hello", -100))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("n must be greater than zero");
		}

		@Test
		@DisplayName("Test firstN consistency - same inputs produce same results")
		void testFirstNConsistency()
		{
			String input = "consistency test";
			int n = 7;
			String firstResult = StringPartUtility.firstN(input, n);
			String secondResult = StringPartUtility.firstN(input, n);

			assertThat(firstResult).as("Same inputs should produce identical results")
								   .isEqualTo(secondResult)
								   .isEqualTo("consist");
		}

		private static Stream<Arguments> validFirstNTestCases()
		{
			return Stream.of(
					Arguments.of("hello", 3, "First 3 characters of 'hello'", "hel"),
					Arguments.of("world", 5, "All characters when n equals string length", "world"),
					Arguments.of("testing", 1, "Single character from string", "t"),
					Arguments.of("Java", 2, "First 2 characters of 'Java'", "Ja"),
					Arguments.of("programming", 7, "First 7 characters of 'programming'", "program"),
					Arguments.of("StringPartUtility", 6, "First 6 characters of class name", "String"),
					Arguments.of("a", 1, "Single character string with n=1", "a"),
					Arguments.of("The quick brown fox", 9, "First 9 characters of sentence", "The quick"),
					Arguments.of("123456789", 5, "First 5 characters of numeric string", "12345"),
					Arguments.of("!@#$%^&*()", 4, "First 4 special characters", "!@#$"),
					Arguments.of("áéíóúñü", 3, "First 3 Unicode characters", "áéí"),
					Arguments.of("🚀🌟💻🎯", 2, "First 2 characters of emoji string (1 emoji)", "🚀"),
					Arguments.of("🚀🌟💻🎯", 4, "First 4 characters of emoji string (2 emojis)", "🚀🌟")
			);
		}

		private static Stream<Arguments> edgeCaseTestCases()
		{
			return Stream.of(
					Arguments.of("", 1, "Empty string should return empty string", ""),
					Arguments.of("", 10, "Empty string with large n should return empty string", ""),
					Arguments.of("short", 10, "n larger than string length should return entire string", "short"),
					Arguments.of("test", 100, "n much larger than string length should return entire string", "test"),
					Arguments.of(" ", 1, "Single space character", " "),
					Arguments.of("   ", 2, "First 2 spaces from multiple spaces", "  "),
					Arguments.of("\n\t\r", 2, "First 2 whitespace characters", "\n\t"),
					Arguments.of("a".repeat(1000), 500, "First 500 characters of very long string", "a".repeat(500)),
					Arguments.of("Mixed123!@#", 8, "First 8 characters of mixed content", "Mixed123"),
					Arguments.of("CamelCaseString", 5, "First 5 characters of camelCase", "Camel"),
					Arguments.of("UPPERCASE", 4, "First 4 characters of uppercase string", "UPPE"),
					Arguments.of("lowercase", 6, "First 6 characters of lowercase string", "lowerc")
			);
		}
	}

	@Nested
	@DisplayName("Constructor Tests")
	final class ConstructorTests
	{
		@Test
		@DisplayName("Test StringPartUtility constructor is private and not accessible")
		void testPrivateConstructor()
		{
			assertThatThrownBy(() -> StringPartUtility.class.getDeclaredConstructor().newInstance())
					.isInstanceOf(IllegalAccessException.class);
		}

		@Test
		@DisplayName("Test StringPartUtility class is final")
		void testClassIsFinal()
		{
			assertThat(StringPartUtility.class.getModifiers())
					.as("StringPartUtility class should be final")
					.satisfies(modifiers -> assertThat(java.lang.reflect.Modifier.isFinal(modifiers)).isTrue());
		}
	}
}