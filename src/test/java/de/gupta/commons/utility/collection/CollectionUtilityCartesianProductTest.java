package de.gupta.commons.utility.collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CollectionUtility#cartesianProduct")
final class CollectionUtilityCartesianProductTest
{
	private static LinkedHashMap<String, List<String>> axesOf(
			final String k1, final List<String> v1)
	{
		var m = new LinkedHashMap<String, List<String>>();
		m.put(k1, v1);
		return m;
	}

	private static LinkedHashMap<String, List<String>> axesOf(
			final String k1, final List<String> v1,
			final String k2, final List<String> v2)
	{
		var m = axesOf(k1, v1);
		m.put(k2, v2);
		return m;
	}

	private static LinkedHashMap<String, List<String>> axesOf(
			final String k1, final List<String> v1,
			final String k2, final List<String> v2,
			final String k3, final List<String> v3)
	{
		var m = axesOf(k1, v1, k2, v2);
		m.put(k3, v3);
		return m;
	}

	private static LinkedHashMap<String, List<String>> axesOf(
			final String k1, final List<String> v1,
			final String k2, final List<String> v2,
			final String k3, final List<String> v3,
			final String k4, final List<String> v4)
	{
		var m = axesOf(k1, v1, k2, v2, k3, v3);
		m.put(k4, v4);
		return m;
	}

	private static Map<String, String> mapOf(final String k1, final String v1)
	{
		var m = new LinkedHashMap<String, String>();
		m.put(k1, v1);
		return m;
	}

	private static Map<String, String> mapOf(final String k1, final String v1,
	                                         final String k2, final String v2)
	{
		var m = new LinkedHashMap<String, String>();
		m.put(k1, v1);
		m.put(k2, v2);
		return m;
	}

	// --- Helpers ---

	private static Map<String, String> mapOf(final String k1, final String v1,
	                                         final String k2, final String v2,
	                                         final String k3, final String v3)
	{
		var m = new LinkedHashMap<String, String>();
		m.put(k1, v1);
		m.put(k2, v2);
		m.put(k3, v3);
		return m;
	}

	@Nested
	@DisplayName("when axes is empty")
	final class WhenAxesIsEmpty
	{
		@Test
		@DisplayName("returns a list containing a single empty map")
		void returnsAListContainingASingleEmptyMap()
		{
			var axes = new LinkedHashMap<String, List<String>>();

			var result = CollectionUtility.cartesianProduct(axes);

			assertThat(result)
					.as("empty axes should yield exactly one combination")
					.hasSize(1);
			assertThat(result.getFirst())
					.as("the single combination should be an empty map")
					.isEmpty();
		}
	}

	@Nested
	@DisplayName("when an axis has an empty list")
	final class WhenAnAxisHasAnEmptyList
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsAnEmptyListCases")
		@DisplayName("returns an empty list")
		void returnsAnEmptyList(final String as, final EmptyAxisCase tc)
		{
			var result = CollectionUtility.cartesianProduct(tc.axes());

			assertThat(result)
					.as(as)
					.isEmpty();
		}

		private static Stream<Arguments> returnsAnEmptyListCases()
		{
			return Stream.of(
					EmptyAxisCase.of("single axis with empty values",
							axesOf("A", List.of())),
					EmptyAxisCase.of("first of two axes is empty",
							axesOf("A", List.of(), "B", List.of("x", "y"))),
					EmptyAxisCase.of("last of two axes is empty",
							axesOf("A", List.of("1", "2"), "B", List.of())),
					EmptyAxisCase.of("middle axis is empty in three axes",
							axesOf("A", List.of("1"), "B", List.of(), "C", List.of("x")))
			).map(tc -> Arguments.of(tc.as(), tc));
		}

		private record EmptyAxisCase(String as, LinkedHashMap<String, List<String>> axes)
		{
			private static EmptyAxisCase of(final String as, final LinkedHashMap<String, List<String>> axes)
			{
				return new EmptyAxisCase(as, axes);
			}
		}
	}

	@Nested
	@DisplayName("when axes has a single axis")
	final class WhenAxesHasASingleAxis
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsOneMapPerValueInTheAxisCases")
		@DisplayName("returns one map per value in the axis")
		void returnsOneMapPerValueInTheAxis(final String as, final SingleAxisCase tc)
		{
			var result = CollectionUtility.cartesianProduct(tc.axes());

			assertThat(result)
					.as(as)
					.isEqualTo(tc.expected());
		}

		private static Stream<Arguments> returnsOneMapPerValueInTheAxisCases()
		{
			return Stream.of(
					SingleAxisCase.of("one axis with one value",
							axesOf("A", List.of("x")),
							List.of(mapOf("A", "x"))),
					SingleAxisCase.of("one axis with two values",
							axesOf("A", List.of("x", "y")),
							List.of(mapOf("A", "x"), mapOf("A", "y"))),
					SingleAxisCase.of("one axis with three values",
							axesOf("A", List.of("1", "2", "3")),
							List.of(mapOf("A", "1"), mapOf("A", "2"), mapOf("A", "3")))
			).map(tc -> Arguments.of(tc.as(), tc));
		}

		private record SingleAxisCase(String as, LinkedHashMap<String, List<String>> axes,
		                              List<Map<String, String>> expected)
		{
			private static SingleAxisCase of(final String as, final LinkedHashMap<String, List<String>> axes,
			                                 final List<Map<String, String>> expected)
			{
				return new SingleAxisCase(as, axes, expected);
			}
		}
	}

	@Nested
	@DisplayName("when axes has multiple axes")
	final class WhenAxesHasMultipleAxes
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsTheFullCartesianProductInKeyThenValueOrderCases")
		@DisplayName("returns the full cartesian product in key-then-value order")
		void returnsTheFullCartesianProductInKeyThenValueOrder(final String as, final MultiAxisCase tc)
		{
			var result = CollectionUtility.cartesianProduct(tc.axes());

			assertThat(result)
					.as(as)
					.isEqualTo(tc.expected());
		}

		@Test
		@DisplayName("combination count equals the product of all axis sizes")
		void combinationCountEqualsTheProductOfAllAxisSizes()
		{
			var axes = axesOf(
					"A", List.of("a1", "a2", "a3"),
					"B", List.of("b1", "b2"),
					"C", List.of("c1", "c2", "c3", "c4"));

			var result = CollectionUtility.cartesianProduct(axes);

			assertThat(result)
					.as("3 x 2 x 4 = 24 combinations expected")
					.hasSize(24);
		}

		private static Stream<Arguments> returnsTheFullCartesianProductInKeyThenValueOrderCases()
		{
			return Stream.of(
					MultiAxisCase.of("two axes one value each — single combination",
							axesOf("A", List.of("1"), "B", List.of("x")),
							List.of(mapOf("A", "1", "B", "x"))),
					MultiAxisCase.of("two axes two values each — four combinations",
							axesOf("A", List.of("1", "2"), "B", List.of("x", "y")),
							List.of(
									mapOf("A", "1", "B", "x"),
									mapOf("A", "1", "B", "y"),
									mapOf("A", "2", "B", "x"),
									mapOf("A", "2", "B", "y"))),
					MultiAxisCase.of("two axes with unequal sizes — 2 x 3 = 6 combinations",
							axesOf("A", List.of("1", "2"), "B", List.of("x", "y", "z")),
							List.of(
									mapOf("A", "1", "B", "x"),
									mapOf("A", "1", "B", "y"),
									mapOf("A", "1", "B", "z"),
									mapOf("A", "2", "B", "x"),
									mapOf("A", "2", "B", "y"),
									mapOf("A", "2", "B", "z"))),
					MultiAxisCase.of("three axes one value each — single combination",
							axesOf("A", List.of("1"), "B", List.of("x"), "C", List.of("p")),
							List.of(mapOf("A", "1", "B", "x", "C", "p"))),
					MultiAxisCase.of("three axes two values each — eight combinations",
							axesOf("A", List.of("1", "2"), "B", List.of("x", "y"), "C", List.of("p", "q")),
							List.of(
									mapOf("A", "1", "B", "x", "C", "p"),
									mapOf("A", "1", "B", "x", "C", "q"),
									mapOf("A", "1", "B", "y", "C", "p"),
									mapOf("A", "1", "B", "y", "C", "q"),
									mapOf("A", "2", "B", "x", "C", "p"),
									mapOf("A", "2", "B", "x", "C", "q"),
									mapOf("A", "2", "B", "y", "C", "p"),
									mapOf("A", "2", "B", "y", "C", "q")))
			).map(tc -> Arguments.of(tc.as(), tc));
		}

		private record MultiAxisCase(String as, LinkedHashMap<String, List<String>> axes,
		                             List<Map<String, String>> expected)
		{
			private static MultiAxisCase of(final String as, final LinkedHashMap<String, List<String>> axes,
			                                final List<Map<String, String>> expected)
			{
				return new MultiAxisCase(as, axes, expected);
			}
		}
	}

	@Nested
	@DisplayName("when key insertion order is preserved in result maps")
	final class WhenKeyInsertionOrderIsPreservedInResultMaps
	{
		@Test
		@DisplayName("each result map contains keys in the same order as the input axes")
		void eachResultMapContainsKeysInTheSameOrderAsTheInputAxes()
		{
			var axes = axesOf("Z", List.of("z1"), "A", List.of("a1"), "M", List.of("m1"));

			var result = CollectionUtility.cartesianProduct(axes);

			assertThat(result).as("should produce exactly one combination").hasSize(1);
			assertThat(result.getFirst().sequencedKeySet())
					.as("keys should appear in insertion order Z, A, M — not alphabetical")
					.containsExactly("Z", "A", "M");
		}

		@Test
		@DisplayName("key order is consistent across all combinations")
		void keyOrderIsConsistentAcrossAllCombinations()
		{
			var axes = axesOf("Z", List.of("z1", "z2"), "A", List.of("a1", "a2"), "M", List.of("m1", "m2"));

			var result = CollectionUtility.cartesianProduct(axes);

			assertThat(result).as("three axes two values each yields eight combinations").hasSize(8);
			result.forEach(combo ->
					assertThat(combo.sequencedKeySet())
							.as("every combination should carry keys in insertion order Z, A, M")
							.containsExactly("Z", "A", "M"));
		}
	}

	@Nested
	@DisplayName("with null arguments")
	final class WithNullArguments
	{
		@Test
		@DisplayName("throws NullPointerException when axes is null")
		void throwsNullPointerExceptionWhenAxesIsNull()
		{
			assertThatThrownBy(() -> CollectionUtility.cartesianProduct(null))
					.as("null axes should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("throws NullPointerException when a value list within axes is null")
		void throwsNullPointerExceptionWhenAValueListWithinAxesIsNull()
		{
			var axes = new LinkedHashMap<String, List<String>>();
			axes.put("A", null);

			assertThatThrownBy(() -> CollectionUtility.cartesianProduct(axes))
					.as("null value list in axes should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}
}