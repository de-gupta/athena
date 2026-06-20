package de.gupta.commons.utility.collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SetUtility Tests")
final class SetUtilityTest
{
	private record RemoveBlankStringsTestCase(
			Set<String> input,
			Set<String> expected,
			String description)
	{
	}

	@Nested
	@DisplayName("removeBlankStrings Normal Cases")
	class RemoveBlankStringsNormalCasesTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("removeBlankStringsNormalCasesProvider")
		@DisplayName("Test removeBlankStrings with normal cases")
		void removeBlankStrings_normalCases(RemoveBlankStringsTestCase testCase)
		{
			Set<String> result = SetUtility.removeBlankStrings(testCase.input());
			assertThat(result)
					.as(testCase.description())
					.containsExactlyInAnyOrderElementsOf(testCase.expected());
		}

		private static Stream<RemoveBlankStringsTestCase> removeBlankStringsNormalCasesProvider()
		{
			return Stream.of(
					new RemoveBlankStringsTestCase(
							Set.of("apple", "banana", "cherry"),
							Set.of("apple", "banana", "cherry"),
							"Set with no blank strings should remain unchanged"),

					new RemoveBlankStringsTestCase(
							Set.of("apple", "", "cherry"),
							Set.of("apple", "cherry"),
							"Empty strings should be removed"),

					new RemoveBlankStringsTestCase(
							Set.of("apple", " ", "cherry"),
							Set.of("apple", "cherry"),
							"Strings with only spaces should be removed"),

					new RemoveBlankStringsTestCase(
							Set.of("apple", "\t", "cherry"),
							Set.of("apple", "cherry"),
							"Strings with only tabs should be removed"),

					new RemoveBlankStringsTestCase(
							Set.of("apple", "\n", "cherry"),
							Set.of("apple", "cherry"),
							"Strings with only newlines should be removed"),

					new RemoveBlankStringsTestCase(
							Set.of("apple", " \t\n\r", "cherry"),
							Set.of("apple", "cherry"),
							"Strings with mixed whitespace should be removed"),

					new RemoveBlankStringsTestCase(
							Set.of("apple", "  banana  ", "cherry"),
							Set.of("apple", "  banana  ", "cherry"),
							"Strings with leading/trailing spaces but non-blank content should be preserved")
			);
		}
	}

	@Nested
	@DisplayName("removeBlankStrings Edge Cases")
	class RemoveBlankStringsEdgeCasesTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("removeBlankStringsEdgeCasesProvider")
		@DisplayName("Test removeBlankStrings with edge cases")
		void removeBlankStrings_edgeCases(RemoveBlankStringsTestCase testCase)
		{
			Set<String> result = SetUtility.removeBlankStrings(testCase.input());
			assertThat(result)
					.as(testCase.description())
					.containsExactlyInAnyOrderElementsOf(testCase.expected());
		}

		private static Stream<RemoveBlankStringsTestCase> removeBlankStringsEdgeCasesProvider()
		{
			return Stream.of(
					new RemoveBlankStringsTestCase(
							Set.of(),
							Set.of(),
							"Empty set should return empty set"),

					new RemoveBlankStringsTestCase(
							Set.of("", " ", "\t", "\n"),
							Set.of(),
							"Set with only blank strings should return empty set"),

					new RemoveBlankStringsTestCase(
							Set.of("\u2000", "\u2001", "\u2002"),  // Unicode whitespace characters
							Set.of(),
							"Unicode whitespace characters should be recognized as blank"),

					new RemoveBlankStringsTestCase(
							Set.of("a", "a ", " a", " a "),
							Set.of("a", "a ", " a", " a "),
							"Similar strings with different whitespace should all be preserved")
			);
		}
	}

	@Nested
	@DisplayName("removeBlankStrings Error Cases")
	class RemoveBlankStringsErrorCasesTests
	{
		@ParameterizedTest(name = "Should throw exception with message: {1}")
		@MethodSource("removeBlankStringsErrorCasesProvider")
		@DisplayName("Test removeBlankStrings with error cases")
		void removeBlankStrings_errorCases(Set<String> input, String expectedErrorMessage)
		{
			assertThatThrownBy(() -> SetUtility.removeBlankStrings(input))
					.as("Method should throw IllegalArgumentException with appropriate message")
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage(expectedErrorMessage);
		}

		private static Stream<Arguments> removeBlankStringsErrorCasesProvider()
		{
			return Stream.of(Arguments.of(null, "Input set cannot be null")
			);
		}
	}

	@Nested
	@DisplayName("unionOf Tests")
	class UnionOfTests
	{
		private record UnionCase<T>(Collection<Set<T>> input, Set<T> expected, String description)
		{
			static <T> UnionCase<T> of(final Collection<Set<T>> input, final Set<T> expected, final String description)
			{
				return new UnionCase<>(input, expected, description);
			}
		}

		@ParameterizedTest(name = "{2}")
		@MethodSource("stringUnionProvider")
		@DisplayName("String union scenarios")
		void stringUnionScenarios(Collection<Set<String>> input, Set<String> expected, String description)
		{
			Set<String> result = SetUtility.unionOf(input);
			assertThat(result).as(description).isEqualTo(expected);
		}

		private static Stream<Arguments> stringUnionProvider()
		{
			return Stream.of(
					UnionCase.of(List.of(), Set.of(), "Empty collection should yield empty union"),
					UnionCase.of(List.of(Set.of(), Set.of()), Set.of(), "Collection of empty sets should yield empty union"),
					UnionCase.of(List.of(Set.of("a", "b")), Set.of("a", "b"), "Single set should be returned as is"),
					UnionCase.of(List.of(Set.of("a", "b"), Set.of("b", "c")), Set.of("a", "b", "c"), "Overlapping sets should merge without duplicates"),
					UnionCase.of(List.of(new HashSet<>(Arrays.asList("a", null)), Set.of("b")), new HashSet<>(Arrays.asList("a", null, "b")), "Null elements should be included in the union")
			).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
		}

		@ParameterizedTest(name = "{2}")
		@MethodSource("integerUnionProvider")
		@DisplayName("Integer union scenarios")
		void integerUnionScenarios(Collection<Set<Integer>> input, Set<Integer> expected, String description)
		{
			Set<Integer> result = SetUtility.unionOf(input);
			assertThat(result).as(description).isEqualTo(expected);
		}

		private static Stream<Arguments> integerUnionProvider()
		{
			return Stream.of(
					UnionCase.of(List.of(Set.of(1, 2), Set.of(2, 3, 4), Set.of(4, 5)), Set.of(1, 2, 3, 4, 5), "Multiple integer sets should be fully merged"),
					UnionCase.of(List.of(Set.of(42)), Set.of(42), "Singleton integer set should be preserved")
			).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
		}

		@Test
		@DisplayName("Null input collection should throw NullPointerException")
		void nullInputCollectionShouldThrow()
		{
			assertThatThrownBy(() -> SetUtility.unionOf(null))
					.as("Null input should cause NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Collection containing null set should throw NullPointerException")
		void collectionContainingNullSetShouldThrow()
		{
			List<Set<String>> input = new ArrayList<>();
			input.add(Set.of("x"));
			input.add(null);
			assertThatThrownBy(() -> SetUtility.unionOf(input))
					.as("Null element inside the collection should cause NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}
}