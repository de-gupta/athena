package de.gupta.commons.utility.collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SequencedCollectionUtility Tests")
final class SequencedCollectionUtilityTest
{
	private static final Predicate<String> IS_NULL_OR_BLANK = s -> s == null || s.isBlank();
	private static final Predicate<String> IS_EMPTY = String::isEmpty;
	private static final Predicate<Integer> IS_ZERO = i -> i == 0;

	// --- Common predicates ---
	private static final Predicate<Integer> IS_NEGATIVE = i -> i < 0;
	private static final Predicate<Integer> IS_EVEN = i -> i % 2 == 0;

	private record FirstNotMatchingStringTestCase(
			SequencedCollection<String> input,
			Predicate<String> predicate,
			Optional<String> expected,
			String description)
	{
	}

	private record FirstNotMatchingIntegerTestCase(
			SequencedCollection<Integer> input,
			Predicate<Integer> predicate,
			Optional<Integer> expected,
			String description)
	{
	}

	private record FirstNotMatchingOrderTestCase<T>(
			List<T> input,
			Predicate<T> predicate,
			Optional<T> expected,
			String description)
	{
	}

	// ========================================================================
	// String tests
	// ========================================================================

	@Nested
	@DisplayName("firstNotMatching with Strings - Normal Cases")
	class StringNormalCasesTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("stringNormalCasesProvider")
		@DisplayName("Should return the first string not matching the predicate")
		void firstNotMatching_stringNormalCases(SequencedCollection<String> input, Predicate<String> predicate,
		                                        Optional<String> expected, String description)
		{
			Optional<String> result = SequencedCollectionUtility.firstNotMatching(input, predicate);

			assertThat(result)
					.as(description)
					.isEqualTo(expected);
		}

		private static Stream<Arguments> stringNormalCasesProvider()
		{
			return Stream.of(
					new FirstNotMatchingStringTestCase(
							List.of("hello", "world"),
							IS_NULL_OR_BLANK,
							Optional.of("hello"),
							"Two non-blank strings with IS_NULL_OR_BLANK predicate: should return first"),
					new FirstNotMatchingStringTestCase(
							List.of("apple", "banana", "cherry"),
							IS_NULL_OR_BLANK,
							Optional.of("apple"),
							"Three non-blank strings: should return first"),
					new FirstNotMatchingStringTestCase(
							List.of("only"),
							IS_NULL_OR_BLANK,
							Optional.of("only"),
							"Singleton list: should return the element"),
					new FirstNotMatchingStringTestCase(
							List.of("", "found"),
							IS_EMPTY,
							Optional.of("found"),
							"Empty string then non-empty with IS_EMPTY predicate: should skip empty"),
					new FirstNotMatchingStringTestCase(
							List.of("short", "longer", "longest"),
							s -> s.length() > 5,
							Optional.of("short"),
							"Predicate rejects long strings: should return first short one"),
					new FirstNotMatchingStringTestCase(
							List.of("ABC", "def", "GHI"),
							s -> s.equals(s.toUpperCase()),
							Optional.of("def"),
							"Predicate rejects uppercase: should return first lowercase string"),
					new FirstNotMatchingStringTestCase(
							new LinkedList<>(List.of("linked", "list")),
							IS_NULL_OR_BLANK,
							Optional.of("linked"),
							"LinkedList: should return first element"),
					new FirstNotMatchingStringTestCase(
							new ArrayDeque<>(List.of("deque", "element")),
							IS_NULL_OR_BLANK,
							Optional.of("deque"),
							"ArrayDeque: should return first element"),
					new FirstNotMatchingStringTestCase(
							new LinkedHashSet<>(List.of("set", "element")),
							IS_NULL_OR_BLANK,
							Optional.of("set"),
							"LinkedHashSet: should return first element by insertion order")
			).map(tc -> Arguments.of(tc.input(), tc.predicate(), tc.expected(), tc.description()));
		}
	}

	@Nested
	@DisplayName("firstNotMatching with Strings - Edge Cases")
	class StringEdgeCasesTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("stringEdgeCasesProvider")
		@DisplayName("Should handle edge cases appropriately")
		void firstNotMatching_stringEdgeCases(SequencedCollection<String> input, Predicate<String> predicate,
		                                      Optional<String> expected, String description)
		{
			Optional<String> result = SequencedCollectionUtility.firstNotMatching(input, predicate);

			assertThat(result)
					.as(description)
					.isEqualTo(expected);
		}

		private static Stream<Arguments> stringEdgeCasesProvider()
		{
			return Stream.of(
					new FirstNotMatchingStringTestCase(
							List.of(),
							IS_NULL_OR_BLANK,
							Optional.empty(),
							"Empty list: should return empty Optional"),
					new FirstNotMatchingStringTestCase(
							new LinkedList<>(),
							IS_NULL_OR_BLANK,
							Optional.empty(),
							"Empty LinkedList: should return empty Optional"),
					new FirstNotMatchingStringTestCase(
							new ArrayDeque<>(),
							IS_NULL_OR_BLANK,
							Optional.empty(),
							"Empty ArrayDeque: should return empty Optional"),
					new FirstNotMatchingStringTestCase(
							List.of(""),
							IS_EMPTY,
							Optional.empty(),
							"Singleton empty string with IS_EMPTY predicate: all match, should return empty"),
					new FirstNotMatchingStringTestCase(
							List.of("", "", ""),
							IS_EMPTY,
							Optional.empty(),
							"All empty strings with IS_EMPTY predicate: all match, should return empty"),
					new FirstNotMatchingStringTestCase(
							List.of(" ", "\t", "\n"),
							IS_NULL_OR_BLANK,
							Optional.empty(),
							"All blank strings: all match IS_NULL_OR_BLANK, should return empty"),
					new FirstNotMatchingStringTestCase(
							List.of("a", "b", "c"),
							_ -> true,
							Optional.empty(),
							"Always-true predicate: all elements match, should return empty"),
					new FirstNotMatchingStringTestCase(
							List.of("a", "b", "c"),
							_ -> false,
							Optional.of("a"),
							"Always-false predicate: no elements match, should return first element"),
					new FirstNotMatchingStringTestCase(
							new ArrayList<>(Arrays.asList(null, null, null)),
							IS_NULL_OR_BLANK,
							Optional.empty(),
							"All nulls with IS_NULL_OR_BLANK: all match, should return empty"),
					new FirstNotMatchingStringTestCase(
							new ArrayList<>(Arrays.asList(null, "found")),
							IS_NULL_OR_BLANK,
							Optional.of("found"),
							"Null then non-blank: should skip null and return non-blank"),
					new FirstNotMatchingStringTestCase(
							new ArrayList<>(Arrays.asList(null, "", " ", "found")),
							IS_NULL_OR_BLANK,
							Optional.of("found"),
							"Null, empty, blank then non-blank: should skip all and return non-blank")
			).map(tc -> Arguments.of(tc.input(), tc.predicate(), tc.expected(), tc.description()));
		}
	}

	// ========================================================================
	// Integer tests
	// ========================================================================

	@Nested
	@DisplayName("firstNotMatching with Integers - Normal Cases")
	class IntegerNormalCasesTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("integerNormalCasesProvider")
		@DisplayName("Should return the first integer not matching the predicate")
		void firstNotMatching_integerNormalCases(SequencedCollection<Integer> input, Predicate<Integer> predicate,
		                                         Optional<Integer> expected, String description)
		{
			Optional<Integer> result = SequencedCollectionUtility.firstNotMatching(input, predicate);

			assertThat(result)
					.as(description)
					.isEqualTo(expected);
		}

		private static Stream<Arguments> integerNormalCasesProvider()
		{
			return Stream.of(
					new FirstNotMatchingIntegerTestCase(
							List.of(1, 2, 3),
							IS_ZERO,
							Optional.of(1),
							"No zeros: should return first element"),
					new FirstNotMatchingIntegerTestCase(
							List.of(0, 0, 5),
							IS_ZERO,
							Optional.of(5),
							"Two zeros then non-zero: should skip zeros and return 5"),
					new FirstNotMatchingIntegerTestCase(
							List.of(0, 1, 2),
							IS_ZERO,
							Optional.of(1),
							"Zero then non-zeros: should skip zero and return 1"),
					new FirstNotMatchingIntegerTestCase(
							List.of(-3, -2, -1, 0, 1),
							IS_NEGATIVE,
							Optional.of(0),
							"Negatives then non-negatives with IS_NEGATIVE: should return 0"),
					new FirstNotMatchingIntegerTestCase(
							List.of(2, 4, 7, 8),
							IS_EVEN,
							Optional.of(7),
							"Even numbers then odd with IS_EVEN: should return first odd (7)"),
					new FirstNotMatchingIntegerTestCase(
							List.of(1, 3, 5),
							IS_EVEN,
							Optional.of(1),
							"All odd with IS_EVEN: should return first element"),
					new FirstNotMatchingIntegerTestCase(
							List.of(2, 4, 6),
							IS_EVEN,
							Optional.empty(),
							"All even with IS_EVEN: all match, should return empty"),
					new FirstNotMatchingIntegerTestCase(
							List.of(0, 0, 0),
							IS_ZERO,
							Optional.empty(),
							"All zeros with IS_ZERO: all match, should return empty"),
					new FirstNotMatchingIntegerTestCase(
							List.of(42),
							IS_ZERO,
							Optional.of(42),
							"Singleton non-zero with IS_ZERO: should return the element"),
					new FirstNotMatchingIntegerTestCase(
							List.of(0),
							IS_ZERO,
							Optional.empty(),
							"Singleton zero with IS_ZERO: matches, should return empty"),
					new FirstNotMatchingIntegerTestCase(
							List.of(Integer.MAX_VALUE, Integer.MIN_VALUE),
							IS_NEGATIVE,
							Optional.of(Integer.MAX_VALUE),
							"MAX_VALUE and MIN_VALUE with IS_NEGATIVE: should return MAX_VALUE"),
					new FirstNotMatchingIntegerTestCase(
							List.of(Integer.MIN_VALUE, Integer.MAX_VALUE),
							IS_NEGATIVE,
							Optional.of(Integer.MAX_VALUE),
							"MIN_VALUE then MAX_VALUE with IS_NEGATIVE: should skip MIN and return MAX")
			).map(tc -> Arguments.of(tc.input(), tc.predicate(), tc.expected(), tc.description()));
		}
	}

	@Nested
	@DisplayName("firstNotMatching with Integers - Edge Cases")
	class IntegerEdgeCasesTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("integerEdgeCasesProvider")
		@DisplayName("Should handle integer edge cases appropriately")
		void firstNotMatching_integerEdgeCases(SequencedCollection<Integer> input, Predicate<Integer> predicate,
		                                       Optional<Integer> expected, String description)
		{
			Optional<Integer> result = SequencedCollectionUtility.firstNotMatching(input, predicate);

			assertThat(result)
					.as(description)
					.isEqualTo(expected);
		}

		private static Stream<Arguments> integerEdgeCasesProvider()
		{
			return Stream.of(
					new FirstNotMatchingIntegerTestCase(
							List.of(),
							IS_ZERO,
							Optional.empty(),
							"Empty integer list: should return empty Optional"),
					new FirstNotMatchingIntegerTestCase(
							List.of(1, 2, 3),
							i -> true,
							Optional.empty(),
							"Always-true predicate on integers: all match, should return empty"),
					new FirstNotMatchingIntegerTestCase(
							List.of(1, 2, 3),
							i -> false,
							Optional.of(1),
							"Always-false predicate on integers: none match, should return first"),
					new FirstNotMatchingIntegerTestCase(
							new ArrayList<>(Arrays.asList(null, 5)),
							Objects::isNull,
							Optional.of(5),
							"Null then integer with null-check predicate: should skip null and return 5"),
					new FirstNotMatchingIntegerTestCase(
							new ArrayList<>(Arrays.asList(null, null)),
							Objects::isNull,
							Optional.empty(),
							"All nulls with null-check predicate: all match, should return empty")
			).map(tc -> Arguments.of(tc.input(), tc.predicate(), tc.expected(), tc.description()));
		}
	}

	// ========================================================================
	// Order preservation tests (String)
	// ========================================================================

	@Nested
	@DisplayName("firstNotMatching - String Order Preservation")
	class StringOrderPreservationTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("stringOrderProvider")
		@DisplayName("Should return the first non-matching string respecting insertion order")
		void firstNotMatching_stringOrder(List<String> input, Predicate<String> predicate,
		                                  Optional<String> expected, String description)
		{
			Optional<String> result = SequencedCollectionUtility.firstNotMatching(input, predicate);

			assertThat(result)
					.as(description)
					.isEqualTo(expected);
		}

		private static Stream<Arguments> stringOrderProvider()
		{
			return Stream.of(
					// --- First non-matching chosen over later non-matching ---
					new FirstNotMatchingOrderTestCase<>(
							List.of("alpha", "beta"),
							IS_NULL_OR_BLANK,
							Optional.of("alpha"),
							"[alpha, beta]: should return alpha (first non-matching)"),
					new FirstNotMatchingOrderTestCase<>(
							List.of("beta", "alpha"),
							IS_NULL_OR_BLANK,
							Optional.of("beta"),
							"[beta, alpha]: should return beta (first non-matching, not alphabetical)"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList("", "winner", "loser")),
							IS_NULL_OR_BLANK,
							Optional.of("winner"),
							"['', winner, loser]: should return winner, not loser"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList(" ", "winner", "loser")),
							IS_NULL_OR_BLANK,
							Optional.of("winner"),
							"[' ', winner, loser]: should return winner, not loser"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList(null, "winner", "loser")),
							IS_NULL_OR_BLANK,
							Optional.of("winner"),
							"[null, winner, loser]: should return winner, not loser"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList("", " ", "winner", "loser")),
							IS_NULL_OR_BLANK,
							Optional.of("winner"),
							"['', ' ', winner, loser]: should return winner, not loser"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList(null, "", " ", "winner", "\t", "loser")),
							IS_NULL_OR_BLANK,
							Optional.of("winner"),
							"[null, '', ' ', winner, '\\t', loser]: should return winner"),

					// --- Non-matching at each index 0 through 5, blanks elsewhere ---
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList("at-0", "", "", "", "", "")),
							IS_NULL_OR_BLANK,
							Optional.of("at-0"),
							"Non-matching at index 0: should return it"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList("", "at-1", "", "", "", "")),
							IS_NULL_OR_BLANK,
							Optional.of("at-1"),
							"Non-matching at index 1: should return it"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList("", "", "at-2", "", "", "")),
							IS_NULL_OR_BLANK,
							Optional.of("at-2"),
							"Non-matching at index 2: should return it"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList("", "", "", "at-3", "", "")),
							IS_NULL_OR_BLANK,
							Optional.of("at-3"),
							"Non-matching at index 3: should return it"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList("", "", "", "", "at-4", "")),
							IS_NULL_OR_BLANK,
							Optional.of("at-4"),
							"Non-matching at index 4: should return it"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList("", "", "", "", "", "at-5")),
							IS_NULL_OR_BLANK,
							Optional.of("at-5"),
							"Non-matching at index 5: should return it"),

					// --- Non-matching at each index with nulls elsewhere ---
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList("at-0", null, null, null)),
							IS_NULL_OR_BLANK,
							Optional.of("at-0"),
							"Non-matching at index 0 among nulls: should return it"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList(null, "at-1", null, null)),
							IS_NULL_OR_BLANK,
							Optional.of("at-1"),
							"Non-matching at index 1 among nulls: should return it"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList(null, null, "at-2", null)),
							IS_NULL_OR_BLANK,
							Optional.of("at-2"),
							"Non-matching at index 2 among nulls: should return it"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList(null, null, null, "at-3")),
							IS_NULL_OR_BLANK,
							Optional.of("at-3"),
							"Non-matching at index 3 among nulls: should return it"),

					// --- Non-matching at each index with mixed blanks ---
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList("at-0", null, "", " ", "\t")),
							IS_NULL_OR_BLANK,
							Optional.of("at-0"),
							"Non-matching at index 0 among mixed blanks: should return it"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList(null, "at-1", "", " ", "\t")),
							IS_NULL_OR_BLANK,
							Optional.of("at-1"),
							"Non-matching at index 1 among mixed blanks: should return it"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList(null, "", "at-2", " ", "\t")),
							IS_NULL_OR_BLANK,
							Optional.of("at-2"),
							"Non-matching at index 2 among mixed blanks: should return it"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList(null, "", " ", "at-3", "\t")),
							IS_NULL_OR_BLANK,
							Optional.of("at-3"),
							"Non-matching at index 3 among mixed blanks: should return it"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList(null, "", " ", "\t", "at-4")),
							IS_NULL_OR_BLANK,
							Optional.of("at-4"),
							"Non-matching at index 4 among mixed blanks: should return it"),

					// --- Interleaved matching/non-matching ---
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList("", "winner", "", "loser", "")),
							IS_NULL_OR_BLANK,
							Optional.of("winner"),
							"Interleaved empties: first non-matching should be returned"),
					new FirstNotMatchingOrderTestCase<>(
							new ArrayList<>(Arrays.asList(null, "winner", null, "loser")),
							IS_NULL_OR_BLANK,
							Optional.of("winner"),
							"Interleaved nulls: first non-matching should be returned")
			).map(tc -> Arguments.of(tc.input(), tc.predicate(), tc.expected(), tc.description()));
		}
	}

	// ========================================================================
	// Order preservation tests (Integer)
	// ========================================================================

	@Nested
	@DisplayName("firstNotMatching - Integer Order Preservation")
	class IntegerOrderPreservationTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("integerOrderProvider")
		@DisplayName("Should return the first non-matching integer respecting insertion order")
		void firstNotMatching_integerOrder(List<Integer> input, Predicate<Integer> predicate,
		                                   Optional<Integer> expected, String description)
		{
			Optional<Integer> result = SequencedCollectionUtility.firstNotMatching(input, predicate);

			assertThat(result)
					.as(description)
					.isEqualTo(expected);
		}

		private static Stream<Arguments> integerOrderProvider()
		{
			return Stream.of(
					// --- First non-zero wins ---
					new FirstNotMatchingOrderTestCase<>(
							List.of(1, 2, 3),
							IS_ZERO,
							Optional.of(1),
							"[1, 2, 3] with IS_ZERO: should return 1"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(3, 2, 1),
							IS_ZERO,
							Optional.of(3),
							"[3, 2, 1] with IS_ZERO: should return 3 (first, not smallest)"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(0, 5, 10),
							IS_ZERO,
							Optional.of(5),
							"[0, 5, 10] with IS_ZERO: should skip 0 and return 5"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(0, 10, 5),
							IS_ZERO,
							Optional.of(10),
							"[0, 10, 5] with IS_ZERO: should skip 0 and return 10, not 5"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(0, 0, 7),
							IS_ZERO,
							Optional.of(7),
							"[0, 0, 7] with IS_ZERO: should skip both zeros and return 7"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(0, 0, 0, 0, 42),
							IS_ZERO,
							Optional.of(42),
							"[0, 0, 0, 0, 42] with IS_ZERO: should skip all zeros and return 42"),

					// --- Non-zero at each index 0 through 4 ---
					new FirstNotMatchingOrderTestCase<>(
							List.of(99, 0, 0, 0, 0),
							IS_ZERO,
							Optional.of(99),
							"Non-zero at index 0: should return 99"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(0, 99, 0, 0, 0),
							IS_ZERO,
							Optional.of(99),
							"Non-zero at index 1: should return 99"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(0, 0, 99, 0, 0),
							IS_ZERO,
							Optional.of(99),
							"Non-zero at index 2: should return 99"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(0, 0, 0, 99, 0),
							IS_ZERO,
							Optional.of(99),
							"Non-zero at index 3: should return 99"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(0, 0, 0, 0, 99),
							IS_ZERO,
							Optional.of(99),
							"Non-zero at index 4: should return 99"),

					// --- First non-even wins ---
					new FirstNotMatchingOrderTestCase<>(
							List.of(2, 4, 7, 9),
							IS_EVEN,
							Optional.of(7),
							"[2, 4, 7, 9] with IS_EVEN: should return 7 (first odd)"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(2, 4, 9, 7),
							IS_EVEN,
							Optional.of(9),
							"[2, 4, 9, 7] with IS_EVEN: should return 9, not 7"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(1, 2, 3, 4),
							IS_EVEN,
							Optional.of(1),
							"[1, 2, 3, 4] with IS_EVEN: should return 1 (first odd)"),

					// --- First non-negative wins ---
					new FirstNotMatchingOrderTestCase<>(
							List.of(-3, -2, -1, 0, 1, 2),
							IS_NEGATIVE,
							Optional.of(0),
							"[-3, -2, -1, 0, 1, 2] with IS_NEGATIVE: should return 0"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(-3, -2, -1, 5, 0, 1),
							IS_NEGATIVE,
							Optional.of(5),
							"[-3, -2, -1, 5, 0, 1] with IS_NEGATIVE: should return 5, not 0"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(-1, 100, 50),
							IS_NEGATIVE,
							Optional.of(100),
							"[-1, 100, 50] with IS_NEGATIVE: should return 100, not 50"),

					// --- Interleaved matching/non-matching ---
					new FirstNotMatchingOrderTestCase<>(
							List.of(0, 1, 0, 2, 0),
							IS_ZERO,
							Optional.of(1),
							"[0, 1, 0, 2, 0] with IS_ZERO: interleaved, should return 1"),
					new FirstNotMatchingOrderTestCase<>(
							List.of(2, 3, 4, 5, 6),
							IS_EVEN,
							Optional.of(3),
							"[2, 3, 4, 5, 6] with IS_EVEN: should return 3, not 5")
			).map(tc -> Arguments.of(tc.input(), tc.predicate(), tc.expected(), tc.description()));
		}
	}

	// ========================================================================
	// SequencedCollection implementation tests
	// ========================================================================

	@Nested
	@DisplayName("firstNotMatching - SequencedCollection Implementations")
	class SequencedCollectionImplementationTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("sequencedCollectionImplProvider")
		@DisplayName("Should work correctly with various SequencedCollection implementations")
		void firstNotMatching_implementations(SequencedCollection<String> input, Predicate<String> predicate,
		                                      Optional<String> expected, String description)
		{
			Optional<String> result = SequencedCollectionUtility.firstNotMatching(input, predicate);

			assertThat(result)
					.as(description)
					.isEqualTo(expected);
		}

		private static Stream<Arguments> sequencedCollectionImplProvider()
		{
			return Stream.of(
					new FirstNotMatchingStringTestCase(
							List.of("list-first", "list-second"),
							IS_NULL_OR_BLANK,
							Optional.of("list-first"),
							"List.of: should respect insertion order"),
					new FirstNotMatchingStringTestCase(
							new ArrayList<>(List.of("arraylist-first", "arraylist-second")),
							IS_NULL_OR_BLANK,
							Optional.of("arraylist-first"),
							"ArrayList: should respect insertion order"),
					new FirstNotMatchingStringTestCase(
							new LinkedList<>(List.of("linked-first", "linked-second")),
							IS_NULL_OR_BLANK,
							Optional.of("linked-first"),
							"LinkedList: should respect insertion order"),
					new FirstNotMatchingStringTestCase(
							new ArrayDeque<>(List.of("deque-first", "deque-second")),
							IS_NULL_OR_BLANK,
							Optional.of("deque-first"),
							"ArrayDeque: should respect insertion order"),
					new FirstNotMatchingStringTestCase(
							new LinkedHashSet<>(List.of("set-first", "set-second")),
							IS_NULL_OR_BLANK,
							Optional.of("set-first"),
							"LinkedHashSet: should respect insertion order"),
					new FirstNotMatchingStringTestCase(
							new TreeSet<>(List.of("b-second", "a-first")),
							IS_NULL_OR_BLANK,
							Optional.of("a-first"),
							"TreeSet: should respect natural (sorted) order"),
					new FirstNotMatchingStringTestCase(
							new LinkedList<>(Arrays.asList("", "linked-second")),
							IS_NULL_OR_BLANK,
							Optional.of("linked-second"),
							"LinkedList with blank first element: should skip to second"),
					new FirstNotMatchingStringTestCase(
							new ArrayDeque<>(List.of(" ", "deque-second")),
							IS_NULL_OR_BLANK,
							Optional.of("deque-second"),
							"ArrayDeque with blank first element: should skip to second"),
					new FirstNotMatchingStringTestCase(
							new LinkedHashSet<>(Arrays.asList("", " ", "set-third")),
							IS_NULL_OR_BLANK,
							Optional.of("set-third"),
							"LinkedHashSet with blank elements: should skip blanks")
			).map(tc -> Arguments.of(tc.input(), tc.predicate(), tc.expected(), tc.description()));
		}
	}

	// ========================================================================
	// Reverse insertion order tests (addFirst)
	// ========================================================================

	@Nested
	@DisplayName("firstNotMatching - Reverse Insertion Order")
	class ReverseInsertionOrderTests
	{
		@Test
		@DisplayName("LinkedList addFirst: firstNotMatching should respect resulting order")
		void firstNotMatching_linkedListAddFirst()
		{
			LinkedList<String> list = new LinkedList<>();
			list.addFirst("third");
			list.addFirst("second");
			list.addFirst("first");

			Optional<String> result = SequencedCollectionUtility.firstNotMatching(list, IS_NULL_OR_BLANK);

			assertThat(result)
					.as("LinkedList built with addFirst should have 'first' at front")
					.isEqualTo(Optional.of("first"));
		}

		@Test
		@DisplayName("ArrayDeque addFirst: firstNotMatching should respect resulting order")
		void firstNotMatching_arrayDequeAddFirst()
		{
			ArrayDeque<String> deque = new ArrayDeque<>();
			deque.addFirst("third");
			deque.addFirst("second");
			deque.addFirst("first");

			Optional<String> result = SequencedCollectionUtility.firstNotMatching(deque, IS_NULL_OR_BLANK);

			assertThat(result)
					.as("ArrayDeque built with addFirst should have 'first' at front")
					.isEqualTo(Optional.of("first"));
		}

		@Test
		@DisplayName("LinkedList addFirst with blanks at head: should skip them")
		void firstNotMatching_linkedListAddFirstWithBlanks()
		{
			LinkedList<String> list = new LinkedList<>();
			list.addFirst("content");
			list.addFirst("   ");
			list.addFirst("");

			Optional<String> result = SequencedCollectionUtility.firstNotMatching(list, IS_NULL_OR_BLANK);

			assertThat(result)
					.as("LinkedList ['', '   ', content] should return 'content'")
					.isEqualTo(Optional.of("content"));
		}

		@Test
		@DisplayName("LinkedList addFirst integers: should respect resulting order")
		void firstNotMatching_linkedListAddFirstIntegers()
		{
			LinkedList<Integer> list = new LinkedList<>();
			list.addFirst(3);
			list.addFirst(0);
			list.addFirst(0);

			Optional<Integer> result = SequencedCollectionUtility.firstNotMatching(list, IS_ZERO);

			assertThat(result)
					.as("LinkedList [0, 0, 3] should return 3")
					.isEqualTo(Optional.of(3));
		}

		@Test
		@DisplayName("ArrayDeque addFirst integers: should respect resulting order")
		void firstNotMatching_arrayDequeAddFirstIntegers()
		{
			ArrayDeque<Integer> deque = new ArrayDeque<>();
			deque.addFirst(3);
			deque.addFirst(0);
			deque.addFirst(0);

			Optional<Integer> result = SequencedCollectionUtility.firstNotMatching(deque, IS_ZERO);

			assertThat(result)
					.as("ArrayDeque [0, 0, 3] should return 3")
					.isEqualTo(Optional.of(3));
		}
	}

	// ========================================================================
	// Custom predicate tests
	// ========================================================================

	@Nested
	@DisplayName("firstNotMatching - Custom Predicates")
	class CustomPredicateTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("customPredicateProvider")
		@DisplayName("Should work with various custom predicates")
		void firstNotMatching_customPredicates(SequencedCollection<String> input, Predicate<String> predicate,
		                                       Optional<String> expected, String description)
		{
			Optional<String> result = SequencedCollectionUtility.firstNotMatching(input, predicate);

			assertThat(result)
					.as(description)
					.isEqualTo(expected);
		}

		private static Stream<Arguments> customPredicateProvider()
		{
			return Stream.of(
					new FirstNotMatchingStringTestCase(
							List.of("aaa", "bbb", "abc"),
							s -> s.chars().distinct().count() == 1,
							Optional.of("abc"),
							"Predicate rejects single-char-repeated strings: should return first mixed-char string"),
					new FirstNotMatchingStringTestCase(
							List.of("a", "ab", "abc", "abcd"),
							s -> s.length() < 3,
							Optional.of("abc"),
							"Predicate rejects strings shorter than 3: should return 'abc'"),
					new FirstNotMatchingStringTestCase(
							List.of("abcd", "abc", "ab"),
							s -> s.length() < 3,
							Optional.of("abcd"),
							"Predicate rejects strings shorter than 3: should return 'abcd' (first)"),
					new FirstNotMatchingStringTestCase(
							List.of("hello", "HELLO", "Hello"),
							s -> s.equals(s.toLowerCase()),
							Optional.of("HELLO"),
							"Predicate rejects lowercase: should return first non-lowercase ('HELLO')"),
					new FirstNotMatchingStringTestCase(
							List.of("Hello", "HELLO", "hello"),
							s -> s.equals(s.toLowerCase()),
							Optional.of("Hello"),
							"Predicate rejects lowercase: should return 'Hello' (first non-lowercase)"),
					new FirstNotMatchingStringTestCase(
							List.of("123", "456", "abc"),
							s -> s.matches("\\d+"),
							Optional.of("abc"),
							"Predicate rejects numeric strings: should return first non-numeric ('abc')"),
					new FirstNotMatchingStringTestCase(
							List.of("abc", "123", "def"),
							s -> s.matches("\\d+"),
							Optional.of("abc"),
							"Predicate rejects numeric: 'abc' is first non-numeric"),
					new FirstNotMatchingStringTestCase(
							List.of("  a  ", "b", "  c  "),
							s -> s.trim().length() != s.length(),
							Optional.of("b"),
							"Predicate rejects strings with leading/trailing whitespace: should return 'b'"),
					new FirstNotMatchingStringTestCase(
							List.of("foo", "bar", "baz"),
							s -> s.startsWith("b"),
							Optional.of("foo"),
							"Predicate rejects strings starting with 'b': should return 'foo'"),
					new FirstNotMatchingStringTestCase(
							List.of("bar", "baz", "foo"),
							s -> s.startsWith("b"),
							Optional.of("foo"),
							"Predicate rejects strings starting with 'b': should return 'foo' (at end)")
			).map(tc -> Arguments.of(tc.input(), tc.predicate(), tc.expected(), tc.description()));
		}
	}

	// ========================================================================
	// Null handling tests
	// ========================================================================

	@Nested
	@DisplayName("firstNotMatching - Null Handling")
	class NullHandlingTests
	{
		@Test
		@DisplayName("Should throw NullPointerException when collection is null")
		void firstNotMatching_nullCollection()
		{
			assertThatThrownBy(() -> SequencedCollectionUtility.firstNotMatching(null, IS_NULL_OR_BLANK))
					.as("Should throw NullPointerException when collection is null")
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Should throw NullPointerException when predicate is null")
		void firstNotMatching_nullPredicate()
		{
			assertThatThrownBy(() -> SequencedCollectionUtility.firstNotMatching(List.of("a"), null))
					.as("Should throw NullPointerException when predicate is null")
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Should throw NullPointerException when both arguments are null")
		void firstNotMatching_bothNull()
		{
			assertThatThrownBy(() -> SequencedCollectionUtility.firstNotMatching(null, null))
					.as("Should throw NullPointerException when both arguments are null")
					.isInstanceOf(NullPointerException.class);
		}
	}

	// ========================================================================
	// Constructor tests
	// ========================================================================

	@Nested
	@DisplayName("Constructor Tests")
	final class ConstructorTests
	{
		@Test
		@DisplayName("Test SequencedCollectionUtility constructor is private and not accessible")
		void testPrivateConstructor()
		{
			assertThatThrownBy(() -> SequencedCollectionUtility.class.getDeclaredConstructor().newInstance())
					.isInstanceOf(IllegalAccessException.class);
		}

		@Test
		@DisplayName("Test SequencedCollectionUtility class is final")
		void testClassIsFinal()
		{
			assertThat(SequencedCollectionUtility.class.getModifiers())
					.as("SequencedCollectionUtility class should be final")
					.satisfies(modifiers -> assertThat(java.lang.reflect.Modifier.isFinal(modifiers)).isTrue());
		}
	}
}