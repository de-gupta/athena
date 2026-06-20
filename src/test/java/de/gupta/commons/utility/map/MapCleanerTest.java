package de.gupta.commons.utility.map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MapCleaner Tests")
final class MapCleanerTest
{
	private static Map<String, Integer> createMap(final Object... keyValuePairs)
	{
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < keyValuePairs.length; i += 2)
		{
			map.put((String) keyValuePairs[i], (Integer) keyValuePairs[i + 1]);
		}
		return map;
	}

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
		FIRST, SECOND, THIRD, FOURTH, FIFTH
	}

	private enum EmptyEnum
	{
	}

	private enum SingleValueEnum
	{
		ONLY
	}

	@Nested
	@DisplayName("Generic Map removeKeyIfValueEquals tests")
	class GenericMapTests
	{
		@ParameterizedTest
		@MethodSource("validGenericMapTestCases")
		@DisplayName("should remove keys with matching values from generic map")
		void testRemoveKeyIfValueEqualsValidInputs(final Map<String, Integer> inputMap, final Integer valueToRemove,
												   final Map<String, Integer> expected, final String description)
		{
			Map<String, Integer> result = MapCleaner.removeKeyIfValueEquals(inputMap, valueToRemove);

			assertThat(result)
					.as(description)
					.isEqualTo(expected)
					.isNotSameAs(inputMap);
		}

		@Test
		@DisplayName("should handle empty map")
		void testRemoveFromEmptyMap()
		{
			Map<String, Integer> emptyMap = new HashMap<>();

			Map<String, Integer> result = MapCleaner.removeKeyIfValueEquals(emptyMap, 42);

			assertThat(result).isEmpty();
			assertThat(result).isNotSameAs(emptyMap);
		}

		@Test
		@DisplayName("should handle map with null values when removing null")
		void testRemoveNullValueFromMapWithNulls()
		{
			Map<String, Integer> mapWithNulls = new HashMap<>();
			mapWithNulls.put("key1", null);
			mapWithNulls.put("key2", 10);
			mapWithNulls.put("key3", null);

			Map<String, Integer> result = MapCleaner.removeKeyIfValueEquals(mapWithNulls, null);

			assertThat(result)
					.containsExactlyInAnyOrderEntriesOf(Map.of("key2", 10))
					.hasSize(1);
		}

		@Test
		@DisplayName("should handle map with null values when removing non-null")
		void testRemoveNonNullFromMapWithNulls()
		{
			Map<String, Integer> mapWithNulls = new HashMap<>();
			mapWithNulls.put("key1", null);
			mapWithNulls.put("key2", 10);
			mapWithNulls.put("key3", 20);

			Map<String, Integer> result = MapCleaner.removeKeyIfValueEquals(mapWithNulls, 10);

			Map<String, Integer> expected = new HashMap<>();
			expected.put("key1", null);
			expected.put("key3", 20);

			assertThat(result)
					.isEqualTo(expected)
					.hasSize(2);
		}

		@Test
		@DisplayName("should create modifiable result map")
		void testResultIsModifiable()
		{
			Map<String, Integer> originalMap = createMap("a", 1, "b", 2, "c", 3);

			Map<String, Integer> result = MapCleaner.removeKeyIfValueEquals(originalMap, 2);

			result.put("new", 99);
			assertThat(result).containsKey("new");
			assertThat(originalMap).doesNotContainKey("new");
		}

		@Test
		@DisplayName("should preserve original map unchanged")
		void testOriginalMapUnchanged()
		{
			Map<String, Integer> originalMap = createMap("a", 1, "b", 2, "c", 2);
			Map<String, Integer> originalCopy = new HashMap<>(originalMap);

			MapCleaner.removeKeyIfValueEquals(originalMap, 2);

			assertThat(originalMap).isEqualTo(originalCopy);
		}

		@Test
		@DisplayName("should handle null input map")
		void testNullInputMap()
		{
			assertThatThrownBy(() -> MapCleaner.removeKeyIfValueEquals((Map<String, Integer>) null, 1))
					.isInstanceOf(NullPointerException.class);
		}

		private static Stream<Arguments> validGenericMapTestCases()
		{
			return Stream.of(
					GenericMapTestCase.of(
							createMap("a", 1, "b", 2, "c", 3),
							2,
							createMap("a", 1, "c", 3),
							"should remove single matching key"
					).toArguments(),

					GenericMapTestCase.of(
							createMap("a", 1, "b", 1, "c", 1),
							1,
							new HashMap<>(),
							"should remove all matching keys"
					).toArguments(),

					GenericMapTestCase.of(
							createMap("a", 1, "b", 2, "c", 3),
							99,
							createMap("a", 1, "b", 2, "c", 3),
							"should return copy when no matches"
					).toArguments(),

					GenericMapTestCase.of(
							createMap("x", 5, "y", 10, "z", 5),
							5,
							createMap("y", 10),
							"should remove multiple matching keys"
					).toArguments()
			);
		}

		private record GenericMapTestCase(
				Map<String, Integer> inputMap,
				Integer valueToRemove,
				Map<String, Integer> expected,
				String description
		)
		{
			private static GenericMapTestCase of(final Map<String, Integer> inputMap, final Integer valueToRemove,
												 final Map<String, Integer> expected, final String description)
			{
				return new GenericMapTestCase(inputMap, valueToRemove, expected, description);
			}

			private Arguments toArguments()
			{
				return Arguments.of(inputMap, valueToRemove, expected, description);
			}
		}
	}

	@Nested
	@DisplayName("EnumMap removeKeyIfValueEquals tests")
	class EnumMapTests
	{
		@ParameterizedTest
		@MethodSource("validEnumMapTestCases")
		@DisplayName("should remove keys with matching values from enum map")
		void testRemoveKeyIfValueEqualsValidInputs(final EnumMap<TestEnum, Integer> inputMap,
												   final Integer valueToRemove,
												   final EnumMap<TestEnum, Integer> expected, final String description)
		{
			EnumMap<TestEnum, Integer> result = MapCleaner.removeKeyIfValueEquals(inputMap, valueToRemove);

			assertThat(result)
					.as(description)
					.isEqualTo(expected)
					.isNotSameAs(inputMap);
		}

		@Test
		@DisplayName("should handle empty enum map")
		void testRemoveFromEmptyEnumMap()
		{
			EnumMap<TestEnum, Integer> emptyMap = new EnumMap<>(TestEnum.class);

			EnumMap<TestEnum, Integer> result = MapCleaner.removeKeyIfValueEquals(emptyMap, 42);

			assertThat(result).isEmpty();
			assertThat(result).isNotSameAs(emptyMap);
		}

		@Test
		@DisplayName("should handle enum map with null values when removing null")
		void testRemoveNullValueFromEnumMapWithNulls()
		{
			EnumMap<TestEnum, Integer> mapWithNulls = new EnumMap<>(TestEnum.class);
			mapWithNulls.put(TestEnum.FIRST, null);
			mapWithNulls.put(TestEnum.SECOND, 10);
			mapWithNulls.put(TestEnum.THIRD, null);

			EnumMap<TestEnum, Integer> result = MapCleaner.removeKeyIfValueEquals(mapWithNulls, null);

			assertThat(result)
					.containsExactlyInAnyOrderEntriesOf(Map.of(TestEnum.SECOND, 10))
					.hasSize(1);
		}

		@Test
		@DisplayName("should handle enum map with null values when removing non-null")
		void testRemoveNonNullFromEnumMapWithNulls()
		{
			EnumMap<TestEnum, Integer> mapWithNulls = new EnumMap<>(TestEnum.class);
			mapWithNulls.put(TestEnum.FIRST, null);
			mapWithNulls.put(TestEnum.SECOND, 10);
			mapWithNulls.put(TestEnum.THIRD, 20);

			EnumMap<TestEnum, Integer> result = MapCleaner.removeKeyIfValueEquals(mapWithNulls, 10);

			EnumMap<TestEnum, Integer> expected = new EnumMap<>(TestEnum.class);
			expected.put(TestEnum.FIRST, null);
			expected.put(TestEnum.THIRD, 20);

			assertThat(result)
					.isEqualTo(expected)
					.hasSize(2);
		}

		@Test
		@DisplayName("should create modifiable result enum map")
		void testResultIsModifiable()
		{
			EnumMap<TestEnum, Integer> originalMap =
					createEnumMap(TestEnum.FIRST, 1, TestEnum.SECOND, 2, TestEnum.THIRD, 3);

			EnumMap<TestEnum, Integer> result = MapCleaner.removeKeyIfValueEquals(originalMap, 2);

			result.put(TestEnum.FOURTH, 99);
			assertThat(result).containsKey(TestEnum.FOURTH);
			assertThat(originalMap).doesNotContainKey(TestEnum.FOURTH);
		}

		@Test
		@DisplayName("should preserve original enum map unchanged")
		void testOriginalEnumMapUnchanged()
		{
			EnumMap<TestEnum, Integer> originalMap =
					createEnumMap(TestEnum.FIRST, 1, TestEnum.SECOND, 2, TestEnum.THIRD, 2);
			EnumMap<TestEnum, Integer> originalCopy = new EnumMap<>(originalMap);

			MapCleaner.removeKeyIfValueEquals(originalMap, 2);

			assertThat(originalMap).isEqualTo(originalCopy);
		}

		@Test
		@DisplayName("should handle null input enum map")
		void testNullInputEnumMap()
		{
			assertThatThrownBy(() -> MapCleaner.removeKeyIfValueEquals((EnumMap<TestEnum, Integer>) null, 1))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("should work with different enum types")
		void testDifferentEnumTypes()
		{
			EnumMap<SingleValueEnum, String> singleEnumMap = new EnumMap<>(SingleValueEnum.class);
			singleEnumMap.put(SingleValueEnum.ONLY, "test");

			EnumMap<SingleValueEnum, String> result = MapCleaner.removeKeyIfValueEquals(singleEnumMap, "test");

			assertThat(result).isEmpty();
		}

		private static Stream<Arguments> validEnumMapTestCases()
		{
			return Stream.of(
					EnumMapTestCase.of(
							createEnumMap(TestEnum.FIRST, 1, TestEnum.SECOND, 2, TestEnum.THIRD, 3),
							2,
							createEnumMap(TestEnum.FIRST, 1, TestEnum.THIRD, 3),
							"should remove single matching enum key"
					).toArguments(),

					EnumMapTestCase.of(
							createEnumMap(TestEnum.FIRST, 1, TestEnum.SECOND, 1, TestEnum.THIRD, 1),
							1,
							new EnumMap<>(TestEnum.class),
							"should remove all matching enum keys"
					).toArguments(),

					EnumMapTestCase.of(
							createEnumMap(TestEnum.FIRST, 1, TestEnum.SECOND, 2, TestEnum.THIRD, 3),
							99,
							createEnumMap(TestEnum.FIRST, 1, TestEnum.SECOND, 2, TestEnum.THIRD, 3),
							"should return copy when no enum matches"
					).toArguments(),

					EnumMapTestCase.of(
							createEnumMap(TestEnum.FIRST, 5, TestEnum.SECOND, 10, TestEnum.THIRD, 5, TestEnum.FOURTH,
									15),
							5,
							createEnumMap(TestEnum.SECOND, 10, TestEnum.FOURTH, 15),
							"should remove multiple matching enum keys"
					).toArguments()
			);
		}

		private record EnumMapTestCase(
				EnumMap<TestEnum, Integer> inputMap,
				Integer valueToRemove,
				EnumMap<TestEnum, Integer> expected,
				String description
		)
		{
			private static EnumMapTestCase of(final EnumMap<TestEnum, Integer> inputMap, final Integer valueToRemove,
											  final EnumMap<TestEnum, Integer> expected, final String description)
			{
				return new EnumMapTestCase(inputMap, valueToRemove, expected, description);
			}

			private Arguments toArguments()
			{
				return Arguments.of(inputMap, valueToRemove, expected, description);
			}
		}
	}

	@Nested
	@DisplayName("MapCleaner class structure tests")
	class ConstructorTests
	{
		@Test
		@DisplayName("should have private constructor")
		void testPrivateConstructor() throws Exception
		{
			Constructor<MapCleaner> constructor = MapCleaner.class.getDeclaredConstructor();
			assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
		}

		@Test
		@DisplayName("should be final class")
		void testClassIsFinal()
		{
			assertThat(Modifier.isFinal(MapCleaner.class.getModifiers())).isTrue();
		}
	}
}