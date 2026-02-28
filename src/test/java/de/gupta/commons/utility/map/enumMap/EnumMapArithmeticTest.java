package de.gupta.commons.utility.map.enumMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumMap;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("EnumMapArithmetic Tests")
class EnumMapArithmeticTest
{
	private static EnumMap<TestEnum, Integer> createEnumMap(final Object... keyValuePairs)
	{
		EnumMap<TestEnum, Integer> map = new EnumMap<>(TestEnum.class);
		for (int i = 0; i < keyValuePairs.length; i += 2)
		{
			map.put((TestEnum) keyValuePairs[i], (Integer) keyValuePairs[i + 1]);
		}
		return map;
	}

	private enum TestEnum
	{
		ALPHA, BETA, GAMMA, DELTA, EPSILON
	}

	private enum EmptyEnum
	{
	}

	private enum SingleValueEnum
	{
		SINGLE
	}

	@Nested
	@DisplayName("merge Tests")
	class MergeTests
	{
		@ParameterizedTest(name = "{4}")
		@MethodSource("validMergeTestCases")
		@DisplayName("Test merge operation with valid inputs")
		void testMergeValidInputs(final EnumMap<TestEnum, Integer> mapA, final EnumMap<TestEnum, Integer> mapB,
								  final BiFunction<Integer, Integer, Integer> mergeFunction,
								  final EnumMap<TestEnum, Integer> expected, final String description)
		{
			EnumMap<TestEnum, Integer> result = EnumMapArithmetic.merge(mapA, mapB, mergeFunction);

			assertThat(result).as(description)
							  .isNotNull()
							  .isEqualTo(expected)
							  .isNotSameAs(mapA)
							  .isNotSameAs(mapB);
		}

		@Test
		@DisplayName("Test merge with empty maps")
		void testMergeEmptyMaps()
		{
			EnumMap<TestEnum, Integer> emptyA = new EnumMap<>(TestEnum.class);
			EnumMap<TestEnum, Integer> emptyB = new EnumMap<>(TestEnum.class);

			EnumMap<TestEnum, Integer> result = EnumMapArithmetic.merge(emptyA, emptyB, Integer::sum);

			assertThat(result).as("Merging empty maps should return empty map")
							  .isEmpty();
			assertThat(result).isNotSameAs(emptyA);
			assertThat(result).isNotSameAs(emptyB);
		}

		@Test
		@DisplayName("Test merge with null first map throws NullPointerException")
		void testMergeNullFirstMap()
		{
			EnumMap<TestEnum, Integer> mapB = new EnumMap<>(TestEnum.class);
			mapB.put(TestEnum.ALPHA, 1);

			assertThatThrownBy(() -> EnumMapArithmetic.merge(null, mapB, Integer::sum))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Test merge with null second map throws NullPointerException")
		void testMergeNullSecondMap()
		{
			EnumMap<TestEnum, Integer> mapA = new EnumMap<>(TestEnum.class);
			mapA.put(TestEnum.ALPHA, 1);

			assertThatThrownBy(() -> EnumMapArithmetic.merge(mapA, null, Integer::sum))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Test merge with null merge function throws NullPointerException")
		void testMergeNullMergeFunction()
		{
			EnumMap<TestEnum, Integer> mapA = new EnumMap<>(TestEnum.class);
			EnumMap<TestEnum, Integer> mapB = new EnumMap<>(TestEnum.class);
			mapA.put(TestEnum.ALPHA, 1);
			mapB.put(TestEnum.ALPHA, 2);

			assertThatThrownBy(() -> EnumMapArithmetic.merge(mapA, mapB, null))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Test merge creates modifiable result")
		void testMergeCreatesModifiableResult()
		{
			EnumMap<TestEnum, Integer> mapA = new EnumMap<>(TestEnum.class);
			EnumMap<TestEnum, Integer> mapB = new EnumMap<>(TestEnum.class);
			mapA.put(TestEnum.ALPHA, 1);
			mapB.put(TestEnum.BETA, 2);

			EnumMap<TestEnum, Integer> result = EnumMapArithmetic.merge(mapA, mapB, Integer::sum);

			assertThat(result).as("Result should be modifiable")
							  .satisfies(map ->
							  {
								  map.put(TestEnum.GAMMA, 3);
								  assertThat(map).containsKey(TestEnum.GAMMA);
							  });
		}

		private static Stream<Arguments> validMergeTestCases()
		{
			return Stream.of(
					MergeTestCase.of(
							createEnumMap(TestEnum.ALPHA, 1, TestEnum.BETA, 2),
							createEnumMap(TestEnum.BETA, 3, TestEnum.GAMMA, 4),
							Integer::sum,
							createEnumMap(TestEnum.ALPHA, 1, TestEnum.BETA, 5, TestEnum.GAMMA, 4),
							"Maps with overlapping keys should merge values using sum function"
					),
					MergeTestCase.of(
							createEnumMap(TestEnum.ALPHA, 10),
							createEnumMap(TestEnum.BETA, 20),
							Integer::sum,
							createEnumMap(TestEnum.ALPHA, 10, TestEnum.BETA, 20),
							"Maps with non-overlapping keys should combine all entries"
					),
					MergeTestCase.of(
							createEnumMap(TestEnum.ALPHA, 5, TestEnum.BETA, 10),
							createEnumMap(TestEnum.ALPHA, 3, TestEnum.BETA, 7),
							Integer::max,
							createEnumMap(TestEnum.ALPHA, 5, TestEnum.BETA, 10),
							"Merge with max function should keep larger values"
					),
					MergeTestCase.of(
							createEnumMap(TestEnum.ALPHA, 100),
							new EnumMap<>(TestEnum.class),
							Integer::sum,
							createEnumMap(TestEnum.ALPHA, 100),
							"Merging with empty second map should return copy of first map"
					)
			).map(tc -> Arguments.of(tc.mapA(), tc.mapB(), tc.mergeFunction(), tc.expected(), tc.description()));
		}

		private record MergeTestCase(EnumMap<TestEnum, Integer> mapA, EnumMap<TestEnum, Integer> mapB,
									 BiFunction<Integer, Integer, Integer> mergeFunction,
									 EnumMap<TestEnum, Integer> expected, String description)
		{
			static MergeTestCase of(final EnumMap<TestEnum, Integer> mapA, final EnumMap<TestEnum, Integer> mapB,
									final BiFunction<Integer, Integer, Integer> mergeFunction,
									final EnumMap<TestEnum, Integer> expected, final String description)
			{
				return new MergeTestCase(mapA, mapB, mergeFunction, expected, description);
			}
		}
	}

	@Nested
	@DisplayName("mergeAndCleanIfValueEqualsGivenValue Tests")
	class MergeAndCleanTests
	{
		@ParameterizedTest(name = "{5}")
		@MethodSource("validMergeAndCleanTestCases")
		@DisplayName("Test merge and clean operation with valid inputs")
		void testMergeAndCleanValidInputs(final EnumMap<TestEnum, Integer> mapA, final EnumMap<TestEnum, Integer> mapB,
										  final BiFunction<Integer, Integer, Integer> mergeFunction,
										  final Integer givenValue,
										  final EnumMap<TestEnum, Integer> expected, final String description)
		{
			EnumMap<TestEnum, Integer> result = EnumMapArithmetic.mergeAndCleanIfValueEqualsGivenValue(
					mapA, mapB, mergeFunction, givenValue);

			assertThat(result).as(description)
							  .isNotNull()
							  .isEqualTo(expected)
							  .isNotSameAs(mapA)
							  .isNotSameAs(mapB);
		}


		@Test
		@DisplayName("Test merge and clean removes all matching values")
		void testMergeAndCleanRemovesAllMatchingValues()
		{
			EnumMap<TestEnum, Integer> mapA = createEnumMap(TestEnum.ALPHA, 0, TestEnum.BETA, 5);
			EnumMap<TestEnum, Integer> mapB = createEnumMap(TestEnum.GAMMA, 0, TestEnum.DELTA, 10);

			EnumMap<TestEnum, Integer> result = EnumMapArithmetic.mergeAndCleanIfValueEqualsGivenValue(
					mapA, mapB, Integer::sum, 0);

			assertThat(result).as("Should remove all entries with value 0")
							  .containsOnlyKeys(TestEnum.BETA, TestEnum.DELTA)
							  .containsEntry(TestEnum.BETA, 5)
							  .containsEntry(TestEnum.DELTA, 10);
		}

		private static Stream<Arguments> validMergeAndCleanTestCases()
		{
			return Stream.of(
					MergeAndCleanTestCase.of(
							createEnumMap(TestEnum.ALPHA, 1, TestEnum.BETA, 2),
							createEnumMap(TestEnum.BETA, -2, TestEnum.GAMMA, 3),
							Integer::sum,
							0,
							createEnumMap(TestEnum.ALPHA, 1, TestEnum.GAMMA, 3),
							"Should merge and remove entries where sum equals zero"
					),
					MergeAndCleanTestCase.of(
							createEnumMap(TestEnum.ALPHA, 10, TestEnum.BETA, 20),
							createEnumMap(TestEnum.GAMMA, 30),
							Integer::sum,
							999,
							createEnumMap(TestEnum.ALPHA, 10, TestEnum.BETA, 20, TestEnum.GAMMA, 30),
							"Should keep all entries when no values equal given value"
					),
					MergeAndCleanTestCase.of(
							createEnumMap(TestEnum.ALPHA, 5),
							createEnumMap(TestEnum.ALPHA, -5),
							Integer::sum,
							0,
							new EnumMap<>(TestEnum.class),
							"Should result in empty map when all merged values equal given value"
					)
			).map(tc -> Arguments.of(tc.mapA(), tc.mapB(), tc.mergeFunction(), tc.givenValue(), tc.expected(),
					tc.description()));
		}

		private record MergeAndCleanTestCase(EnumMap<TestEnum, Integer> mapA, EnumMap<TestEnum, Integer> mapB,
											 BiFunction<Integer, Integer, Integer> mergeFunction, Integer givenValue,
											 EnumMap<TestEnum, Integer> expected, String description)
		{
			static MergeAndCleanTestCase of(final EnumMap<TestEnum, Integer> mapA,
											final EnumMap<TestEnum, Integer> mapB,
											final BiFunction<Integer, Integer, Integer> mergeFunction,
											final Integer givenValue,
											final EnumMap<TestEnum, Integer> expected, final String description)
			{
				return new MergeAndCleanTestCase(mapA, mapB, mergeFunction, givenValue, expected, description);
			}
		}
	}

	@Nested
	@DisplayName("manipulate Tests")
	class ManipulateTests
	{
		@ParameterizedTest(name = "{4}")
		@MethodSource("validManipulateTestCases")
		@DisplayName("Test manipulate operation with valid inputs")
		void testManipulateValidInputs(final EnumMap<TestEnum, Integer> inputMap, final Integer manipulationArgument,
									   final BiFunction<Integer, Integer, Integer> manipulationFunction,
									   final EnumMap<TestEnum, Integer> expected, final String description)
		{
			EnumMap<TestEnum, Integer> result =
					EnumMapArithmetic.manipulate(inputMap, manipulationArgument, manipulationFunction);

			assertThat(result).as(description)
							  .isNotNull()
							  .isEqualTo(expected)
							  .isNotSameAs(inputMap);
		}

		@Test
		@DisplayName("Test manipulate with empty map")
		void testManipulateEmptyMap()
		{
			EnumMap<TestEnum, Integer> emptyMap = new EnumMap<>(TestEnum.class);

			EnumMap<TestEnum, Integer> result = EnumMapArithmetic.manipulate(emptyMap, 10, Integer::sum);

			assertThat(result).as("Manipulating empty map should return empty map")
							  .isEmpty();
			assertThat(result).isNotSameAs(emptyMap);
		}

		@Test
		@DisplayName("Test manipulate with null input map throws NullPointerException")
		void testManipulateNullInputMap()
		{
			assertThatThrownBy(() -> EnumMapArithmetic.manipulate(null, 5, Integer::sum))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Test manipulate with null manipulation function throws NullPointerException")
		void testManipulateNullManipulationFunction()
		{
			EnumMap<TestEnum, Integer> map = createEnumMap(TestEnum.ALPHA, 1);

			assertThatThrownBy(() -> EnumMapArithmetic.manipulate(map, 5, null))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Test manipulate preserves original map")
		void testManipulatePreservesOriginalMap()
		{
			EnumMap<TestEnum, Integer> originalMap = createEnumMap(TestEnum.ALPHA, 10, TestEnum.BETA, 20);
			EnumMap<TestEnum, Integer> originalCopy = new EnumMap<>(originalMap);

			EnumMapArithmetic.manipulate(originalMap, 5, Integer::sum);

			assertThat(originalMap).as("Original map should remain unchanged")
								   .isEqualTo(originalCopy);
		}

		@Test
		@DisplayName("Test manipulate with different value types")
		void testManipulateWithDifferentValueTypes()
		{
			EnumMap<TestEnum, Double> doubleMap = new EnumMap<>(TestEnum.class);
			doubleMap.put(TestEnum.ALPHA, 10.5);
			doubleMap.put(TestEnum.BETA, 20.7);

			EnumMap<TestEnum, Double> result = EnumMapArithmetic.manipulate(doubleMap, 2.0, (a, b) -> a * b);

			assertThat(result).as("Should work with Double values")
							  .containsEntry(TestEnum.ALPHA, 21.0)
							  .containsEntry(TestEnum.BETA, 41.4);
		}

		private static Stream<Arguments> validManipulateTestCases()
		{
			return Stream.of(
					ManipulateTestCase.of(
							createEnumMap(TestEnum.ALPHA, 10, TestEnum.BETA, 20),
							5,
							Integer::sum,
							createEnumMap(TestEnum.ALPHA, 15, TestEnum.BETA, 25),
							"Should add manipulation argument to all values"
					),
					ManipulateTestCase.of(
							createEnumMap(TestEnum.ALPHA, 100, TestEnum.BETA, 50),
							2,
							(value, arg) -> value / arg,
							createEnumMap(TestEnum.ALPHA, 50, TestEnum.BETA, 25),
							"Should divide all values by manipulation argument"
					),
					ManipulateTestCase.of(
							createEnumMap(TestEnum.ALPHA, 3, TestEnum.BETA, 4),
							2,
							(value, arg) -> value * value + arg,
							createEnumMap(TestEnum.ALPHA, 11, TestEnum.BETA, 18),
							"Should apply complex function to all values"
					),
					ManipulateTestCase.of(
							createEnumMap(TestEnum.ALPHA, -5),
							0,
							Integer::max,
							createEnumMap(TestEnum.ALPHA, 0),
							"Should use max function with manipulation argument"
					)
			).map(tc -> Arguments.of(tc.inputMap(), tc.manipulationArgument(), tc.manipulationFunction(), tc.expected(),
					tc.description()));
		}

		private record ManipulateTestCase(EnumMap<TestEnum, Integer> inputMap, Integer manipulationArgument,
										  BiFunction<Integer, Integer, Integer> manipulationFunction,
										  EnumMap<TestEnum, Integer> expected, String description)
		{
			static ManipulateTestCase of(final EnumMap<TestEnum, Integer> inputMap, final Integer manipulationArgument,
										 final BiFunction<Integer, Integer, Integer> manipulationFunction,
										 final EnumMap<TestEnum, Integer> expected, final String description)
			{
				return new ManipulateTestCase(inputMap, manipulationArgument, manipulationFunction, expected,
						description);
			}
		}
	}

	@Nested
	@DisplayName("manipulateAndCleanIfValueEqualsGivenValue Tests")
	class ManipulateAndCleanTests
	{
		@ParameterizedTest(name = "{5}")
		@MethodSource("validManipulateAndCleanTestCases")
		@DisplayName("Test manipulate and clean operation with valid inputs")
		void testManipulateAndCleanValidInputs(final EnumMap<TestEnum, Integer> inputMap,
											   final Integer manipulationArgument,
											   final BiFunction<Integer, Integer, Integer> manipulationFunction,
											   final Integer givenValue, final EnumMap<TestEnum, Integer> expected,
											   final String description)
		{
			EnumMap<TestEnum, Integer> result = EnumMapArithmetic.manipulateAndCleanIfValueEqualsGivenValue(
					inputMap, manipulationArgument, manipulationFunction, givenValue);

			assertThat(result).as(description)
							  .isNotNull()
							  .isEqualTo(expected)
							  .isNotSameAs(inputMap);
		}

		@Test
		@DisplayName("Test manipulate and clean with null given value")
		void testManipulateAndCleanWithNullGivenValue()
		{
			EnumMap<TestEnum, Integer> inputMap = createEnumMap(TestEnum.ALPHA, 10, TestEnum.BETA, 0);

			EnumMap<TestEnum, Integer> result = EnumMapArithmetic.manipulateAndCleanIfValueEqualsGivenValue(
					inputMap, 0, Integer::min, null);

			assertThat(result).as("Should keep all entries when given value is null but no null results")
							  .containsEntry(TestEnum.ALPHA, 0)
							  .containsEntry(TestEnum.BETA, 0);
		}

		@Test
		@DisplayName("Test manipulate and clean removes all matching values after manipulation")
		void testManipulateAndCleanRemovesAllMatchingValues()
		{
			EnumMap<TestEnum, Integer> inputMap =
					createEnumMap(TestEnum.ALPHA, 10, TestEnum.BETA, 5, TestEnum.GAMMA, 15);

			EnumMap<TestEnum, Integer> result = EnumMapArithmetic.manipulateAndCleanIfValueEqualsGivenValue(
					inputMap, 10, Integer::min, 5);

			assertThat(result).as("Should remove entries where min(value, 10) equals 5")
							  .containsOnlyKeys(TestEnum.ALPHA, TestEnum.GAMMA)
							  .containsEntry(TestEnum.ALPHA, 10)
							  .containsEntry(TestEnum.GAMMA, 10);
		}

		private static Stream<Arguments> validManipulateAndCleanTestCases()
		{
			return Stream.of(
					ManipulateAndCleanTestCase.of(
							createEnumMap(TestEnum.ALPHA, 10, TestEnum.BETA, 20, TestEnum.GAMMA, 30),
							10,
							Integer::min,
							10,
							new EnumMap<>(TestEnum.class),
							"Should remove all entries since min with 10 always equals 10"
					),
					ManipulateAndCleanTestCase.of(
							createEnumMap(TestEnum.ALPHA, 5, TestEnum.BETA, 0),
							1,
							Integer::sum,
							6,
							createEnumMap(TestEnum.BETA, 1),
							"Should remove entries where value + 1 equals 6"
					),
					ManipulateAndCleanTestCase.of(
							createEnumMap(TestEnum.ALPHA, 100, TestEnum.BETA, 200),
							999,
							Integer::max,
							500,
							createEnumMap(TestEnum.ALPHA, 999, TestEnum.BETA, 999),
							"Should keep all entries when no manipulated values equal given value"
					),
					ManipulateAndCleanTestCase.of(
							createEnumMap(TestEnum.ALPHA, 0, TestEnum.BETA, 0),
							5,
							Integer::sum,
							5,
							new EnumMap<>(TestEnum.class),
							"Should result in empty map when all manipulated values equal given value"
					)
			).map(tc -> Arguments.of(tc.inputMap(), tc.manipulationArgument(), tc.manipulationFunction(),
					tc.givenValue(), tc.expected(), tc.description()));
		}

		private record ManipulateAndCleanTestCase(EnumMap<TestEnum, Integer> inputMap, Integer manipulationArgument,
												  BiFunction<Integer, Integer, Integer> manipulationFunction,
												  Integer givenValue, EnumMap<TestEnum, Integer> expected,
												  String description)
		{
			static ManipulateAndCleanTestCase of(final EnumMap<TestEnum, Integer> inputMap,
												 final Integer manipulationArgument,
												 final BiFunction<Integer, Integer, Integer> manipulationFunction,
												 final Integer givenValue, final EnumMap<TestEnum, Integer> expected,
												 final String description)
			{
				return new ManipulateAndCleanTestCase(inputMap, manipulationArgument, manipulationFunction, givenValue,
						expected, description);
			}
		}
	}

	@Nested
	@DisplayName("Constructor Tests")
	class ConstructorTests
	{
		@Test
		@DisplayName("Test EnumMapArithmetic constructor is private and not accessible")
		void testPrivateConstructor()
		{
			assertThatThrownBy(() -> EnumMapArithmetic.class.getDeclaredConstructor().newInstance())
					.isInstanceOf(IllegalAccessException.class);
		}

		@Test
		@DisplayName("Test EnumMapArithmetic class is final")
		void testClassIsFinal()
		{
			assertThat(EnumMapArithmetic.class.getModifiers())
					.as("EnumMapArithmetic class should be final")
					.satisfies(modifiers -> assertThat(java.lang.reflect.Modifier.isFinal(modifiers)).isTrue());
		}
	}
}