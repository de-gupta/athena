package de.gupta.commons.utility.map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SequencedMapBuilder.addAll Tests")
final class SequencedMapBuilderAddAllTest
{
	private static SequencedMap<String, Integer> sequencedMapOf(final Object... keyValuePairs)
	{
		LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
		for (int i = 0; i < keyValuePairs.length; i += 2)
		{
			map.put((String) keyValuePairs[i], (Integer) keyValuePairs[i + 1]);
		}
		return map;
	}

	private record AddAllTestCase(
			SequencedMap<String, Integer> input,
			SequencedMap<String, Integer> expected,
			String description
	)
	{
		private static AddAllTestCase of(final SequencedMap<String, Integer> input,
		                                 final SequencedMap<String, Integer> expected,
		                                 final String description)
		{
			return new AddAllTestCase(input, expected, description);
		}

		private Arguments toArguments()
		{
			return Arguments.of(input, expected, description);
		}
	}

	@Nested
	@DisplayName("Normal Cases")
	final class NormalCasesTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("normalCases")
		@DisplayName("addAll merges entries correctly into the built map")
		void addAllMergesCorrectly(final SequencedMap<String, Integer> input,
		                           final SequencedMap<String, Integer> expected,
		                           final String description)
		{
			SequencedMap<String, Integer> result = MapFactory.<String, Integer>sequencedMapBuilder()
			                                                 .addAll(input)
			                                                 .build();

			assertThat(result)
					.as(description)
					.containsExactlyEntriesOf(expected);
		}

		private static Stream<Arguments> normalCases()
		{
			return Stream.of(
					AddAllTestCase.of(
							sequencedMapOf("a", 1, "b", 2, "c", 3),
							sequencedMapOf("a", 1, "b", 2, "c", 3),
							"All entries from input map should appear in built map"
					).toArguments(),

					AddAllTestCase.of(
							sequencedMapOf(),
							sequencedMapOf(),
							"Empty input map should produce an empty built map"
					).toArguments(),

					AddAllTestCase.of(
							sequencedMapOf("only", 42),
							sequencedMapOf("only", 42),
							"Single entry input map should produce a single-entry built map"
					).toArguments()
			);
		}
	}

	@Nested
	@DisplayName("Insertion Order Tests")
	final class InsertionOrderTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("orderCases")
		@DisplayName("addAll preserves insertion order from the input SequencedMap")
		void addAllPreservesOrder(final SequencedMap<String, Integer> input,
		                          final List<String> expectedKeyOrder,
		                          final String description)
		{
			SequencedMap<String, Integer> result = MapFactory.<String, Integer>sequencedMapBuilder()
			                                                 .addAll(input)
			                                                 .build();

			assertThat(result.sequencedKeySet())
					.as(description)
					.containsExactlyElementsOf(expectedKeyOrder);
		}

		@Test
		@DisplayName("Entries added before addAll appear first, followed by addAll entries in order")
		void priorEntriesAppearBeforeAddAll()
		{
			SequencedMap<String, Integer> result = MapFactory.<String, Integer>sequencedMapBuilder()
			                                                 .add("pre", 0)
			                                                 .addAll(sequencedMapOf("a", 1, "b", 2))
			                                                 .build();

			assertThat(result.sequencedKeySet())
					.as("Pre-existing entry should be first, then addAll entries in insertion order")
					.containsExactly("pre", "a", "b");
		}

		@Test
		@DisplayName("Entries added after addAll appear last")
		void subsequentEntriesAppearAfterAddAll()
		{
			SequencedMap<String, Integer> result = MapFactory.<String, Integer>sequencedMapBuilder()
			                                                 .addAll(sequencedMapOf("a", 1, "b", 2))
			                                                 .add("post", 99)
			                                                 .build();

			assertThat(result.sequencedKeySet())
					.as("addAll entries should appear before the subsequently added entry")
					.containsExactly("a", "b", "post");
		}

		private static Stream<Arguments> orderCases()
		{
			return Stream.of(
					Arguments.of(
							sequencedMapOf("first", 1, "second", 2, "third", 3),
							List.of("first", "second", "third"),
							"Keys should appear in the same order as the input SequencedMap"
					),
					Arguments.of(
							sequencedMapOf("z", 26, "a", 1, "m", 13),
							List.of("z", "a", "m"),
							"Insertion order of the source map should be respected, not alphabetical order"
					)
			);
		}
	}

	@Nested
	@DisplayName("Null Value Tests")
	final class NullValueTests
	{
		@Test
		@DisplayName("Null values within the input map are allowed")
		void nullValuesInInputMapAreAllowed()
		{
			LinkedHashMap<String, Integer> input = new LinkedHashMap<>();
			input.put("present", 1);
			input.put("absent", null);

			SequencedMap<String, Integer> result = MapFactory.<String, Integer>sequencedMapBuilder()
			                                                 .addAll(input)
			                                                 .build();

			assertThat(result)
					.as("Entry with null value should be present in the built map")
					.containsKey("absent");
			assertThat(result.get("absent"))
					.as("Null value should be preserved as null")
					.isNull();
		}
	}

	@Nested
	@DisplayName("Null Input Map Tests")
	final class NullInputMapTests
	{
		@Test
		@DisplayName("Passing null as the input map throws NullPointerException")
		void nullInputMapThrows()
		{
			assertThatThrownBy(() -> MapFactory.<String, Integer>sequencedMapBuilder().addAll(null))
					.as("null input map should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}

	@Nested
	@DisplayName("Duplicate Key Tests")
	final class DuplicateKeyTests
	{
		@ParameterizedTest(name = "{1}")
		@MethodSource("duplicatesClashingWithExistingState")
		@DisplayName("addAll throws IllegalArgumentException when a key already exists in the builder")
		void addAllThrowsOnKeyAlreadyInBuilder(final SequencedMap<String, Integer> input,
		                                       final String description)
		{
			var existingKey = input.firstEntry().getKey();

			assertThatThrownBy(() -> MapFactory.<String, Integer>sequencedMapBuilder()
			                                   .add(existingKey, 1)
			                                   .addAll(input))
					.as(description)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining(existingKey);
		}

		@Test
		@DisplayName("addAll throws IllegalArgumentException when input map contains a null key")
		void addAllThrowsOnNullKeyInInput()
		{
			LinkedHashMap<String, Integer> inputWithNullKey = new LinkedHashMap<>();
			inputWithNullKey.put(null, 1);
			inputWithNullKey.put("valid", 2);

			assertThatThrownBy(() -> MapFactory.<String, Integer>sequencedMapBuilder().addAll(inputWithNullKey))
					.as("Null key inside the input map should throw IllegalArgumentException")
					.isInstanceOf(IllegalArgumentException.class);
		}

		private static Stream<Arguments> duplicatesClashingWithExistingState()
		{
			return Stream.of(
					Arguments.of(
							sequencedMapOf("existing", 99),
							"Single-entry input map whose only key clashes with the builder"
					),
					Arguments.of(
							sequencedMapOf("new", 2, "existing", 99),
							"Multi-entry input map where the clashing key is not the first entry"
					),
					Arguments.of(
							sequencedMapOf("existing", 99, "new", 2),
							"Multi-entry input map where the clashing key is the first entry"
					)
			);
		}
	}
}