package de.gupta.commons.utility.io.table;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TableRow#cells()")
final class TableRowCellsTest
{
	private static TableRow rowOf(final String k1, final String v1,
	                              final String k2, final String v2)
	{
		var map = new LinkedHashMap<String, String>();
		map.put(k1, v1);
		map.put(k2, v2);
		return new TableRow(Collections.unmodifiableMap(map));
	}

	private static TableRow rowOf(final String k1, final String v1,
	                              final String k2, final String v2,
	                              final String k3, final String v3)
	{
		var map = new LinkedHashMap<String, String>();
		map.put(k1, v1);
		map.put(k2, v2);
		map.put(k3, v3);
		return new TableRow(Collections.unmodifiableMap(map));
	}

	// --- Helpers ---

	private static TableRow rowOf(final String key, final String value)
	{
		var map = new LinkedHashMap<String, String>();
		map.put(key, value);
		return new TableRow(Collections.unmodifiableMap(map));
	}

	@Nested
	@DisplayName("when row has cells")
	final class WhenRowHasCells
	{
		@Test
		@DisplayName("returns all entries as a map")
		void returnsAllEntriesAsAMap()
		{
			var row = rowOf("name", "Alice", "city", "London");

			assertThat(row.cells())
					.as("cells() should return all key-value entries of the row")
					.isEqualTo(Map.of("name", "Alice", "city", "London"));
		}

		@Test
		@DisplayName("returns an unmodifiable map")
		void returnsAnUnmodifiableMap()
		{
			var row = rowOf("name", "Alice");

			assertThatThrownBy(() -> row.cells().put("extra", "value"))
					.as("cells() result should be unmodifiable")
					.isInstanceOf(UnsupportedOperationException.class);
		}

		@Test
		@DisplayName("preserves insertion order of columns")
		void preservesInsertionOrderOfColumns()
		{
			var row = rowOf("z-col", "z", "a-col", "a", "m-col", "m");

			assertThat(row.cells().keySet())
					.as("keys should appear in insertion order, not alphabetical order")
					.containsExactly("z-col", "a-col", "m-col");
		}
	}

	@Nested
	@DisplayName("when row is empty")
	final class WhenRowIsEmpty
	{
		@Test
		@DisplayName("returns an empty map")
		void returnsAnEmptyMap()
		{
			var row = new TableRow(Collections.unmodifiableMap(new LinkedHashMap<>()));

			assertThat(row.cells())
					.as("row with no cells should return an empty map")
					.isEmpty();
		}
	}
}