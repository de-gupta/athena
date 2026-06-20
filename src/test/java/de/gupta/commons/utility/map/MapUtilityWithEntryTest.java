package de.gupta.commons.utility.map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MapUtility#withEntry")
final class MapUtilityWithEntryTest
{
	private static LinkedHashMap<String, String> mapOf(final String k1, final String v1)
	{
		var m = new LinkedHashMap<String, String>();
		m.put(k1, v1);
		return m;
	}

	private static LinkedHashMap<String, String> mapOf(final String k1, final String v1,
	                                                   final String k2, final String v2)
	{
		var m = mapOf(k1, v1);
		m.put(k2, v2);
		return m;
	}

	private static LinkedHashMap<String, String> mapOf(final String k1, final String v1,
	                                                   final String k2, final String v2,
	                                                   final String k3, final String v3)
	{
		var m = mapOf(k1, v1, k2, v2);
		m.put(k3, v3);
		return m;
	}

	@Nested
	@DisplayName("when map is empty")
	final class WhenMapIsEmpty
	{
		@Test
		@DisplayName("returns a single-entry map containing the given key and value")
		void returnsASingleEntryMapContainingTheGivenKeyAndValue()
		{
			var map = new LinkedHashMap<String, String>();

			var result = MapUtility.withEntry(map, "colour", "red");

			assertThat(result)
					.as("empty map with one entry added should contain exactly that entry")
					.isEqualTo(Map.of("colour", "red"));
		}
	}

	@Nested
	@DisplayName("when map has existing entries")
	final class WhenMapHasExistingEntries
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("preservesAllExistingEntriesAndAddsTheNewOneCases")
		@DisplayName("preserves all existing entries and adds the new one")
		void preservesAllExistingEntriesAndAddsTheNewOne(final String as, final WithEntryCase tc)
		{
			var result = MapUtility.withEntry(tc.source(), tc.key(), tc.value());

			assertThat(result)
					.as(as)
					.isEqualTo(tc.expected());
		}

		private static Stream<Arguments> preservesAllExistingEntriesAndAddsTheNewOneCases()
		{
			return Stream.of(
					WithEntryCase.of("one existing entry — result has both entries",
							mapOf("A", "1"),
							"B", "2",
							Map.of("A", "1", "B", "2")),
					WithEntryCase.of("two existing entries — result has all three entries",
							mapOf("A", "1", "B", "2"),
							"C", "3",
							Map.of("A", "1", "B", "2", "C", "3")),
					WithEntryCase.of("entry with null value is accepted",
							mapOf("A", "1"),
							"B", null,
							mapOf("A", "1", "B", null))
			).map(tc -> Arguments.of(tc.as(), tc));
		}

		private record WithEntryCase(String as, LinkedHashMap<String, String> source,
		                             String key, String value,
		                             Map<String, String> expected)
		{
			private static WithEntryCase of(final String as, final LinkedHashMap<String, String> source,
			                                final String key, final String value,
			                                final Map<String, String> expected)
			{
				return new WithEntryCase(as, source, key, value, expected);
			}
		}
	}

	@Nested
	@DisplayName("when key already exists in the map")
	final class WhenKeyAlreadyExistsInTheMap
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("replacesTheValueForThatKeyCases")
		@DisplayName("replaces the value for that key")
		void replacesTheValueForThatKey(final String as, final ReplaceCase tc)
		{
			var result = MapUtility.withEntry(tc.source(), tc.key(), tc.newValue());

			assertThat(result)
					.as(as)
					.isEqualTo(tc.expected());
		}

		private static Stream<Arguments> replacesTheValueForThatKeyCases()
		{
			return Stream.of(
					ReplaceCase.of("only key in map — value replaced",
							mapOf("A", "old"),
							"A", "new",
							Map.of("A", "new")),
					ReplaceCase.of("first of several keys — its value replaced",
							mapOf("A", "old", "B", "2", "C", "3"),
							"A", "new",
							Map.of("A", "new", "B", "2", "C", "3")),
					ReplaceCase.of("last of several keys — its value replaced",
							mapOf("A", "1", "B", "2", "C", "old"),
							"C", "new",
							Map.of("A", "1", "B", "2", "C", "new")),
					ReplaceCase.of("middle of several keys — its value replaced",
							mapOf("A", "1", "B", "old", "C", "3"),
							"B", "new",
							Map.of("A", "1", "B", "new", "C", "3"))
			).map(tc -> Arguments.of(tc.as(), tc));
		}

		private record ReplaceCase(String as, LinkedHashMap<String, String> source,
		                           String key, String newValue,
		                           Map<String, String> expected)
		{
			private static ReplaceCase of(final String as, final LinkedHashMap<String, String> source,
			                              final String key, final String newValue,
			                              final Map<String, String> expected)
			{
				return new ReplaceCase(as, source, key, newValue, expected);
			}
		}
	}

	// --- Helpers ---

	@Nested
	@DisplayName("when insertion order is preserved")
	final class WhenInsertionOrderIsPreserved
	{
		@Test
		@DisplayName("new entry appears after all existing entries")
		void newEntryAppearsAfterAllExistingEntries()
		{
			var map = mapOf("Z", "z", "A", "a", "M", "m");

			var result = MapUtility.withEntry(map, "NEW", "n");

			assertThat(result.sequencedKeySet())
					.as("new key should appear last, in insertion order Z, A, M, NEW — not alphabetically")
					.containsExactly("Z", "A", "M", "NEW");
		}

		@Test
		@DisplayName("existing entries retain their relative order")
		void existingEntriesRetainTheirRelativeOrder()
		{
			var map = mapOf("third", "3", "first", "1", "second", "2");

			var result = MapUtility.withEntry(map, "fourth", "4");

			assertThat(result.sequencedKeySet())
					.as("all keys should appear in original insertion order with new key last")
					.containsExactly("third", "first", "second", "fourth");
		}
	}

	@Nested
	@DisplayName("when original map is not mutated")
	final class WhenOriginalMapIsNotMutated
	{
		@Test
		@DisplayName("the source map is unchanged after the call")
		void theSourceMapIsUnchangedAfterTheCall()
		{
			var original = mapOf("A", "1", "B", "2");
			var snapshot = Map.copyOf(original);

			MapUtility.withEntry(original, "C", "3");

			assertThat(original)
					.as("source map should be identical to its state before withEntry was called")
					.isEqualTo(snapshot);
		}
	}

	@Nested
	@DisplayName("when result is unmodifiable")
	final class WhenResultIsUnmodifiable
	{
		@Test
		@DisplayName("throws UnsupportedOperationException on put")
		void throwsUnsupportedOperationExceptionOnPut()
		{
			var result = MapUtility.withEntry(mapOf("A", "1"), "B", "2");

			assertThatThrownBy(() -> result.put("C", "3"))
					.as("result map should be unmodifiable")
					.isInstanceOf(UnsupportedOperationException.class);
		}
	}

	@Nested
	@DisplayName("with null arguments")
	final class WithNullArguments
	{
		@Test
		@DisplayName("throws NullPointerException when map is null")
		void throwsNullPointerExceptionWhenMapIsNull()
		{
			assertThatThrownBy(() -> MapUtility.withEntry(null, "key", "value"))
					.as("null map should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("throws NullPointerException when key is null")
		void throwsNullPointerExceptionWhenKeyIsNull()
		{
			assertThatThrownBy(() -> MapUtility.withEntry(mapOf("A", "1"), null, "value"))
					.as("null key should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}
}