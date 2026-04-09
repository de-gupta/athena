package de.gupta.commons.utility.map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.SequencedMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SequencedMapBuilder Tests")
final class SequencedMapBuilderTest
{
	@Nested
	@DisplayName("Normal Cases")
	final class NormalCasesTests
	{
		@Test
		@DisplayName("Empty builder produces an empty SequencedMap")
		void emptyBuilderProducesEmptyMap()
		{
			SequencedMap<String, Integer> map = MapFactory.<String, Integer>sequencedMapBuilder().build();

			assertThat(map).isEmpty();
		}

		@Test
		@DisplayName("Single pair is present in the built map")
		void singlePairIsPresent()
		{
			SequencedMap<String, Integer> map = MapFactory.<String, Integer>sequencedMapBuilder()
			                                              .add("one", 1)
			                                              .build();

			assertThat(map).containsEntry("one", 1).hasSize(1);
		}

		@Test
		@DisplayName("Multiple pairs are present in the built map")
		void multiplePairsArePresent()
		{
			SequencedMap<String, Integer> map = MapFactory.<String, Integer>sequencedMapBuilder()
			                                              .add("one", 1)
			                                              .add("two", 2)
			                                              .add("three", 3)
			                                              .build();

			assertThat(map)
					.containsEntry("one", 1)
					.containsEntry("two", 2)
					.containsEntry("three", 3)
					.hasSize(3);
		}

		@Test
		@DisplayName("Insertion order is preserved in the built map")
		void insertionOrderIsPreserved()
		{
			SequencedMap<String, Integer> map = MapFactory.<String, Integer>sequencedMapBuilder()
			                                              .add("one", 1)
			                                              .add("two", 2)
			                                              .add("three", 3)
			                                              .build();

			assertThat(map.sequencedKeySet())
					.as("Keys should appear in insertion order")
					.containsExactlyElementsOf(List.of("one", "two", "three"));
		}

		@Test
		@DisplayName("firstEntry() returns the first inserted entry")
		void firstEntryReturnsFirstInserted()
		{
			SequencedMap<String, Integer> map = MapFactory.<String, Integer>sequencedMapBuilder()
			                                              .add("first", 1)
			                                              .add("second", 2)
			                                              .build();

			assertThat(map.firstEntry().getKey()).isEqualTo("first");
			assertThat(map.firstEntry().getValue()).isEqualTo(1);
		}

		@Test
		@DisplayName("lastEntry() returns the last inserted entry")
		void lastEntryReturnsLastInserted()
		{
			SequencedMap<String, Integer> map = MapFactory.<String, Integer>sequencedMapBuilder()
			                                              .add("first", 1)
			                                              .add("last", 99)
			                                              .build();

			assertThat(map.lastEntry().getKey()).isEqualTo("last");
			assertThat(map.lastEntry().getValue()).isEqualTo(99);
		}

		@Test
		@DisplayName("Null values are allowed")
		void nullValueIsAllowed()
		{
			SequencedMap<String, Integer> map = MapFactory.<String, Integer>sequencedMapBuilder()
			                                              .add("nullable", null)
			                                              .build();

			assertThat(map).containsKey("nullable");
			assertThat(map.get("nullable")).isNull();
		}

		@Test
		@DisplayName("Built map is unmodifiable")
		void builtMapIsUnmodifiable()
		{
			SequencedMap<String, Integer> map = MapFactory.<String, Integer>sequencedMapBuilder()
			                                              .add("one", 1)
			                                              .build();

			assertThatThrownBy(() -> map.put("two", 2))
					.isInstanceOf(UnsupportedOperationException.class);
		}
	}

	@Nested
	@DisplayName("Duplicate Key Tests")
	final class DuplicateKeyTests
	{
		@Test
		@DisplayName("Adding a duplicate key throws IllegalArgumentException")
		void duplicateKeyThrows()
		{
			assertThatThrownBy(() -> MapFactory.<String, Integer>sequencedMapBuilder()
			                                   .add("key", 1)
			                                   .add("key", 2))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("key");
		}
	}

	@Nested
	@DisplayName("Null Key Tests")
	final class NullKeyTests
	{
		@Test
		@DisplayName("Adding a null key throws IllegalArgumentException")
		void nullKeyThrows()
		{
			assertThatThrownBy(() -> MapFactory.<String, Integer>sequencedMapBuilder().add(null, 1))
					.isInstanceOf(IllegalArgumentException.class);
		}
	}
}