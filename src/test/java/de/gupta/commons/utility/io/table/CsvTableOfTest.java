package de.gupta.commons.utility.io.table;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CsvTable#of")
final class CsvTableOfTest
{
	@Nested
	@DisplayName("when headers and rows are provided")
	final class WhenHeadersAndRowsAreProvided
	{
		@Test
		@DisplayName("builds a table with the correct row count")
		void buildsATableWithTheCorrectRowCount()
		{
			var table = CsvTable.of(
					List.of("name", "city"),
					List.of(
							Map.of("name", "Alice", "city", "London"),
							Map.of("name", "Bob", "city", "Paris")));

			assertThat(table.rows())
					.as("of() should produce exactly the rows supplied")
					.hasSize(2);
		}

		@Test
		@DisplayName("builds rows with the correct cell values")
		void buildsRowsWithTheCorrectCellValues()
		{
			var table = CsvTable.of(
					List.of("name", "city"),
					List.of(Map.of("name", "Alice", "city", "London")));

			assertThat(table.rows().getFirst().cells().get("name"))
					.as("name cell should contain the value from the supplied map")
					.isEqualTo("Alice");
			assertThat(table.rows().getFirst().cells().get("city"))
					.as("city cell should contain the value from the supplied map")
					.isEqualTo("London");
		}

		@Test
		@DisplayName("cell order in each row follows the headers list")
		void cellOrderInEachRowFollowsTheHeadersList()
		{
			var table = CsvTable.of(
					List.of("z", "a", "m"),
					List.of(Map.of("z", "zv", "a", "av", "m", "mv")));

			assertThat(table.rows().getFirst().cells().keySet())
					.as("cell keys should appear in header declaration order, not alphabetical")
					.containsExactly("z", "a", "m");
		}
	}

	@Nested
	@DisplayName("when a row map is missing a key")
	final class WhenARowMapIsMissingAKey
	{
		@Test
		@DisplayName("absent keys render as empty string in the resulting row")
		void absentKeysRenderAsEmptyStringInTheResultingRow()
		{
			var table = CsvTable.of(
					List.of("name", "city", "country"),
					List.of(Map.of("name", "Alice", "city", "London")));

			assertThat(table.rows().getFirst().cells().get("country"))
					.as("key absent from the row map should produce an empty string")
					.isEqualTo("");
		}
	}

	@Nested
	@DisplayName("when a row map has extra keys not in headers")
	final class WhenARowMapHasExtraKeysNotInHeaders
	{
		@Test
		@DisplayName("extra keys are ignored — only header-declared columns appear in the row")
		void extraKeysAreIgnored()
		{
			var table = CsvTable.of(
					List.of("name"),
					List.of(Map.of("name", "Alice", "ignored", "extra")));

			assertThat(table.rows().getFirst().cells())
					.as("only the declared header should appear as a cell key")
					.containsOnlyKeys("name");
		}
	}

	@Nested
	@DisplayName("when headers are empty")
	final class WhenHeadersAreEmpty
	{
		@Test
		@DisplayName("returns an empty table that renders to an empty string")
		void returnsAnEmptyTableThatRendersToAnEmptyString()
		{
			var table = CsvTable.of(List.of(), List.of());

			assertThat(table.rows())
					.as("empty headers with no rows should yield no rows")
					.isEmpty();
			assertThat(table.render())
					.as("table with no headers should render to an empty string")
					.isEmpty();
		}
	}

	@Nested
	@DisplayName("when rows list is empty")
	final class WhenRowsListIsEmpty
	{
		@Test
		@DisplayName("returns a header-only table")
		void returnsAHeaderOnlyTable()
		{
			var table = CsvTable.of(List.of("name", "city"), List.of());

			assertThat(table.rows())
					.as("no data rows supplied means rows() is empty")
					.isEmpty();
			assertThat(table.render())
					.as("header-only table should render to the header row followed by CRLF")
					.isEqualTo("name,city\r\n");
		}
	}

	@Nested
	@DisplayName("when used with render for a complete round-trip")
	final class WhenUsedWithRenderForACompleteRoundTrip
	{
		@Test
		@DisplayName("of then render then parse produces identical rows")
		void ofThenRenderThenParseProducesIdenticalRows()
		{
			var original = CsvTable.of(
					List.of("name", "city", "note"),
					List.of(
							Map.of("name", "Alice", "city", "London", "note", "a, b"),
							Map.of("name", "Bob", "city", "Paris", "note", "say \"hi\"")));

			var reparsed = CsvTable.parse(original.render());

			assertThat(reparsed.rows())
					.as("rows after of → render → parse should be identical to the original rows")
					.usingRecursiveComparison()
					.isEqualTo(original.rows());
		}
	}

	@Nested
	@DisplayName("with null arguments")
	final class WithNullArguments
	{
		@Test
		@DisplayName("throws NullPointerException when headers is null")
		void throwsNullPointerExceptionWhenHeadersIsNull()
		{
			assertThatThrownBy(() -> CsvTable.of(null, List.of()))
					.as("null headers should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("throws NullPointerException when rows is null")
		void throwsNullPointerExceptionWhenRowsIsNull()
		{
			assertThatThrownBy(() -> CsvTable.of(List.of("name"), null))
					.as("null rows should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("throws NullPointerException when a row map within rows is null")
		void throwsNullPointerExceptionWhenARowMapWithinRowsIsNull()
		{
			var rowsWithNull = new java.util.ArrayList<Map<String, String>>();
			rowsWithNull.add(null);

			assertThatThrownBy(() -> CsvTable.of(List.of("name"), rowsWithNull))
					.as("null row map inside rows list should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}
}
