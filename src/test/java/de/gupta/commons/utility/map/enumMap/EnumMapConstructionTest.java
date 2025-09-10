package de.gupta.commons.utility.map.enumMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("EnumMapConstruction Tests")
class EnumMapConstructionTest
{
	enum TestEnum
	{
		FIRST,
		SECOND,
		THIRD
	}

	enum SingleValueEnum
	{
		ONLY_VALUE
	}

	@Nested
	@DisplayName("single Tests")
	class SingleTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("validSingleTestCases")
		@DisplayName("Test single method creates EnumMap with one entry")
		void testSingleValidInputs(TestEnum key, Object value, String description)
		{
			EnumMap<TestEnum, Object> result = EnumMapConstruction.single(key, value);

			assertThat(result).as(description)
							  .isNotNull()
							  .hasSize(1)
							  .containsEntry(key, value)
							  .isInstanceOf(EnumMap.class);
		}

		@Test
		@DisplayName("Test single method throws NullPointerException for null key")
		void testSingleNullKey()
		{
			assertThatThrownBy(() -> EnumMapConstruction.single(null, "value"))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Test single method accepts null value")
		void testSingleNullValue()
		{
			assertThatThrownBy(() -> EnumMapConstruction.single(TestEnum.FIRST, null))
					.as("Single method should reject null value")
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Test single method with different enum types")
		void testSingleDifferentEnumTypes()
		{
			EnumMap<SingleValueEnum, String> result = EnumMapConstruction.single(SingleValueEnum.ONLY_VALUE, "test");

			assertThat(result).as("Single method should work with different enum types")
							  .isNotNull()
							  .hasSize(1)
							  .containsEntry(SingleValueEnum.ONLY_VALUE, "test");
		}

		@ParameterizedTest(name = "{1}")
		@MethodSource("differentValueTypes")
		@DisplayName("Test single method with different value types")
		void testSingleDifferentValueTypes(Object value, String description)
		{
			EnumMap<TestEnum, Object> result = EnumMapConstruction.single(TestEnum.FIRST, value);

			assertThat(result).as(description)
							  .isNotNull()
							  .hasSize(1)
							  .containsEntry(TestEnum.FIRST, value);
		}

		private static Stream<Arguments> validSingleTestCases()
		{
			return Stream.of(
					SingleTestCase.of(TestEnum.FIRST, "string_value",
							"Single method should create EnumMap with string value"),
					SingleTestCase.of(TestEnum.SECOND, 42,
							"Single method should create EnumMap with integer value"),
					SingleTestCase.of(TestEnum.THIRD, true,
							"Single method should create EnumMap with boolean value")
			).map(tc -> Arguments.of(tc.key(), tc.value(), tc.description()));
		}

		private static Stream<Arguments> differentValueTypes()
		{
			return Stream.of(
					Arguments.of("string", "String value should be stored correctly"),
					Arguments.of(123, "Integer value should be stored correctly"),
					Arguments.of(45.67, "Double value should be stored correctly"),
					Arguments.of(true, "Boolean value should be stored correctly"),
					Arguments.of(new Object(), "Object value should be stored correctly")
			);
		}

		private record SingleTestCase(TestEnum key, Object value, String description)
		{
			static SingleTestCase of(TestEnum key, Object value, String description)
			{
				return new SingleTestCase(key, value, description);
			}
		}
	}

	@Nested
	@DisplayName("from Tests")
	class FromTests
	{
		@Test
		@DisplayName("Test from method creates EnumMap from non-empty map")
		void testFromNonEmptyMap()
		{
			Map<TestEnum, String> sourceMap = Map.of(
					TestEnum.FIRST, "first_value",
					TestEnum.SECOND, "second_value"
			);

			EnumMap<TestEnum, String> result = EnumMapConstruction.from(sourceMap, TestEnum.class);

			assertThat(result).as("From method should create EnumMap from non-empty map")
							  .isNotNull()
							  .hasSize(2)
							  .containsEntry(TestEnum.FIRST, "first_value")
							  .containsEntry(TestEnum.SECOND, "second_value")
							  .isInstanceOf(EnumMap.class);
		}

		@Test
		@DisplayName("Test from method creates empty EnumMap from empty map")
		void testFromEmptyMap()
		{
			Map<TestEnum, String> emptyMap = Map.of();

			EnumMap<TestEnum, String> result = EnumMapConstruction.from(emptyMap, TestEnum.class);

			assertThat(result).as("From method should create empty EnumMap from empty map")
							  .isNotNull()
							  .isEmpty();
			assertThat(result).isInstanceOf(EnumMap.class);
		}

		@Test
		@DisplayName("Test from method throws NullPointerException for null map")
		void testFromNullMap()
		{
			assertThatThrownBy(() -> EnumMapConstruction.from(null, TestEnum.class))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Test from method throws NullPointerException for null enum class")
		void testFromNullEnumClass()
		{
			Map<TestEnum, String> sourceMap = Map.of();

			assertThatThrownBy(() -> EnumMapConstruction.from(sourceMap, null))
					.isInstanceOf(NullPointerException.class);
		}

		@ParameterizedTest(name = "{2}")
		@MethodSource("fromTestCases")
		@DisplayName("Test from method with various map sizes")
		void testFromVariousMapSizes(Map<TestEnum, String> sourceMap, int expectedSize, String description)
		{
			EnumMap<TestEnum, String> result = EnumMapConstruction.from(sourceMap, TestEnum.class);

			assertThat(result).as(description)
							  .isNotNull()
							  .hasSize(expectedSize)
							  .isInstanceOf(EnumMap.class);

			sourceMap.forEach((key, value) ->
					assertThat(result).containsEntry(key, value)
			);
		}

		@Test
		@DisplayName("Test from method with single value enum")
		void testFromSingleValueEnum()
		{
			Map<SingleValueEnum, String> sourceMap = Map.of(SingleValueEnum.ONLY_VALUE, "test");

			EnumMap<SingleValueEnum, String> result = EnumMapConstruction.from(sourceMap, SingleValueEnum.class);

			assertThat(result).as("From method should work with single value enum")
							  .isNotNull()
							  .hasSize(1)
							  .containsEntry(SingleValueEnum.ONLY_VALUE, "test");
		}

		@Test
		@DisplayName("Test from method preserves null values")
		void testFromPreservesNullValues()
		{
			Map<TestEnum, String> sourceMap = Map.of(TestEnum.FIRST, "value");
			EnumMap<TestEnum, String> enumMap = new EnumMap<>(sourceMap);
			enumMap.put(TestEnum.SECOND, null);

			EnumMap<TestEnum, String> result = EnumMapConstruction.from(enumMap, TestEnum.class);

			assertThat(result).as("From method should preserve null values")
							  .isNotNull()
							  .hasSize(2)
							  .containsEntry(TestEnum.FIRST, "value")
							  .containsEntry(TestEnum.SECOND, null);
		}

		private static Stream<Arguments> fromTestCases()
		{
			return Stream.of(
					FromTestCase.of(Map.of(), 0, "Empty map should create empty EnumMap"),
					FromTestCase.of(Map.of(TestEnum.FIRST, "value"), 1,
							"Single entry map should create EnumMap with one entry"),
					FromTestCase.of(Map.of(TestEnum.FIRST, "first", TestEnum.SECOND, "second"), 2,
							"Two entry map should create EnumMap with two entries"),
					FromTestCase.of(Map.of(TestEnum.FIRST, "first", TestEnum.SECOND, "second", TestEnum.THIRD, "third"),
							3, "Three entry map should create EnumMap with three entries")
			).map(tc -> Arguments.of(tc.sourceMap(), tc.expectedSize(), tc.description()));
		}

		private record FromTestCase(Map<TestEnum, String> sourceMap, int expectedSize, String description)
		{
			static FromTestCase of(Map<TestEnum, String> sourceMap, int expectedSize, String description)
			{
				return new FromTestCase(sourceMap, expectedSize, description);
			}
		}
	}

	@Nested
	@DisplayName("copyOf Tests")
	class CopyOfTests
	{
		@Test
		@DisplayName("Test copyOf method creates copy of non-empty EnumMap")
		void testCopyOfNonEmptyMap()
		{
			EnumMap<TestEnum, String> originalMap = new EnumMap<>(TestEnum.class);
			originalMap.put(TestEnum.FIRST, "first_value");
			originalMap.put(TestEnum.SECOND, "second_value");

			EnumMap<TestEnum, String> result = EnumMapConstruction.copyOf(originalMap);

			assertThat(result).as("CopyOf method should create copy of non-empty EnumMap")
							  .isNotNull()
							  .isNotSameAs(originalMap)
							  .hasSize(2)
							  .containsEntry(TestEnum.FIRST, "first_value")
							  .containsEntry(TestEnum.SECOND, "second_value")
							  .isInstanceOf(EnumMap.class);
		}

		@Test
		@DisplayName("Test copyOf method creates copy of empty EnumMap")
		void testCopyOfEmptyMap()
		{
			EnumMap<TestEnum, String> originalMap = new EnumMap<>(TestEnum.class);

			EnumMap<TestEnum, String> result = EnumMapConstruction.copyOf(originalMap);

			assertThat(result).as("CopyOf method should create copy of empty EnumMap")
							  .isNotNull()
							  .isNotSameAs(originalMap)
							  .isEmpty();
			assertThat(result).isInstanceOf(EnumMap.class);
		}

		@Test
		@DisplayName("Test copyOf method throws NullPointerException for null map")
		void testCopyOfNullMap()
		{
			assertThatThrownBy(() -> EnumMapConstruction.copyOf(null))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Test copyOf method creates independent copy")
		void testCopyOfIndependentCopy()
		{
			EnumMap<TestEnum, String> originalMap = new EnumMap<>(TestEnum.class);
			originalMap.put(TestEnum.FIRST, "original_value");

			EnumMap<TestEnum, String> result = EnumMapConstruction.copyOf(originalMap);
			result.put(TestEnum.SECOND, "new_value");

			assertThat(originalMap).as("Original map should remain unchanged")
								   .hasSize(1)
								   .containsEntry(TestEnum.FIRST, "original_value")
								   .doesNotContainKey(TestEnum.SECOND);

			assertThat(result).as("Copied map should have modifications")
							  .hasSize(2)
							  .containsEntry(TestEnum.FIRST, "original_value")
							  .containsEntry(TestEnum.SECOND, "new_value");
		}

		@Test
		@DisplayName("Test copyOf method preserves null values")
		void testCopyOfPreservesNullValues()
		{
			EnumMap<TestEnum, String> originalMap = new EnumMap<>(TestEnum.class);
			originalMap.put(TestEnum.FIRST, "value");
			originalMap.put(TestEnum.SECOND, null);

			EnumMap<TestEnum, String> result = EnumMapConstruction.copyOf(originalMap);

			assertThat(result).as("CopyOf method should preserve null values")
							  .isNotNull()
							  .hasSize(2)
							  .containsEntry(TestEnum.FIRST, "value")
							  .containsEntry(TestEnum.SECOND, null);
		}

		@ParameterizedTest(name = "{1}")
		@MethodSource("copyOfTestCases")
		@DisplayName("Test copyOf method with various EnumMap sizes")
		void testCopyOfVariousSizes(EnumMap<TestEnum, String> originalMap, String description)
		{
			EnumMap<TestEnum, String> result = EnumMapConstruction.copyOf(originalMap);

			assertThat(result).as(description)
							  .isNotNull()
							  .isNotSameAs(originalMap)
							  .hasSize(originalMap.size())
							  .isInstanceOf(EnumMap.class);

			originalMap.forEach((key, value) ->
					assertThat(result).containsEntry(key, value)
			);
		}

		@Test
		@DisplayName("Test copyOf method with single value enum")
		void testCopyOfSingleValueEnum()
		{
			EnumMap<SingleValueEnum, String> originalMap = new EnumMap<>(SingleValueEnum.class);
			originalMap.put(SingleValueEnum.ONLY_VALUE, "test");

			EnumMap<SingleValueEnum, String> result = EnumMapConstruction.copyOf(originalMap);

			assertThat(result).as("CopyOf method should work with single value enum")
							  .isNotNull()
							  .isNotSameAs(originalMap)
							  .hasSize(1)
							  .containsEntry(SingleValueEnum.ONLY_VALUE, "test");
		}

		private static Stream<Arguments> copyOfTestCases()
		{
			EnumMap<TestEnum, String> emptyMap = new EnumMap<>(TestEnum.class);

			EnumMap<TestEnum, String> singleEntryMap = new EnumMap<>(TestEnum.class);
			singleEntryMap.put(TestEnum.FIRST, "value");

			EnumMap<TestEnum, String> multiEntryMap = new EnumMap<>(TestEnum.class);
			multiEntryMap.put(TestEnum.FIRST, "first");
			multiEntryMap.put(TestEnum.SECOND, "second");
			multiEntryMap.put(TestEnum.THIRD, "third");

			return Stream.of(
					CopyOfTestCase.of(emptyMap, "Empty EnumMap should be copied correctly"),
					CopyOfTestCase.of(singleEntryMap, "Single entry EnumMap should be copied correctly"),
					CopyOfTestCase.of(multiEntryMap, "Multiple entry EnumMap should be copied correctly")
			).map(tc -> Arguments.of(tc.originalMap(), tc.description()));
		}

		private record CopyOfTestCase(EnumMap<TestEnum, String> originalMap, String description)
		{
			static CopyOfTestCase of(EnumMap<TestEnum, String> originalMap, String description)
			{
				return new CopyOfTestCase(originalMap, description);
			}
		}
	}

	@Nested
	@DisplayName("Constructor Tests")
	class ConstructorTests
	{
		@Test
		@DisplayName("Test EnumMapConstruction constructor is private and not accessible")
		void testPrivateConstructor()
		{
			assertThatThrownBy(() -> EnumMapConstruction.class.getDeclaredConstructor().newInstance())
					.isInstanceOf(IllegalAccessException.class);
		}

		@Test
		@DisplayName("Test EnumMapConstruction class is final")
		void testClassIsFinal()
		{
			assertThat(EnumMapConstruction.class.getModifiers())
					.as("EnumMapConstruction class should be final")
					.satisfies(modifiers -> assertThat(java.lang.reflect.Modifier.isFinal(modifiers)).isTrue());
		}
	}
}