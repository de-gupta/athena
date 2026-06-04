package de.gupta.commons.utility.io.table;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CsvTable#render")
final class CsvTableRenderTest
{
	@Nested
	@DisplayName("when table is empty")
	final class WhenTableIsEmpty
	{
		@Test
		@DisplayName("returns an empty string")
		void returnsAnEmptyString()
		{
			var table = CsvTable.parse("");

			assertThat(table.render())
					.as("empty table should render to an empty string")
					.isEmpty();
		}
	}

	@Nested
	@DisplayName("when table has headers only")
	final class WhenTableHasHeadersOnly
	{
		@Test
		@DisplayName("renders a single CRLF-terminated header line")
		void rendersASingleCrlfTerminatedHeaderLine()
		{
			var table = CsvTable.parse("name,city");

			assertThat(table.render())
					.as("header-only table should render as one CRLF-terminated line")
					.isEqualTo("name,city\r\n");
		}
	}

	@Nested
	@DisplayName("when table has data rows")
	final class WhenTableHasDataRows
	{
		@Test
		@DisplayName("renders all rows with CRLF line endings")
		void rendersAllRowsWithCrlfLineEndings()
		{
			var table = CsvTable.parse("name,city\nAlice,London\nBob,Paris");

			assertThat(table.render())
					.as("all rows including header should use CRLF line endings")
					.isEqualTo("name,city\r\nAlice,London\r\nBob,Paris\r\n");
		}
	}

	@Nested
	@DisplayName("when fields require quoting")
	final class WhenFieldsRequireQuoting
	{
		@Test
		@DisplayName("quotes a field that contains a comma")
		void quotesAFieldThatContainsAComma()
		{
			var table = CsvTable.parse("name,address\nAlice,\"123 Main St, Apt 4\"");

			assertThat(table.render())
					.as("field with comma should be quoted in rendered output")
					.contains("\"123 Main St, Apt 4\"");
		}

		@Test
		@DisplayName("doubles quotes inside a quoted field")
		void doublesQuotesInsideAQuotedField()
		{
			var table = CsvTable.parse("name,quote\nAlice,\"say \"\"hello\"\"\"");

			assertThat(table.render())
					.as("internal double-quotes should be doubled in rendered output")
					.contains("\"say \"\"hello\"\"\"");
		}

		@Test
		@DisplayName("quotes a field that contains a newline")
		void quotesAFieldThatContainsANewline()
		{
			var table = CsvTable.parse("name,notes\nAlice,\"line one\nline two\"");

			assertThat(table.render())
					.as("field with embedded newline should be quoted in rendered output")
					.contains("\"line one\nline two\"");
		}
	}

	@Nested
	@DisplayName("when round-tripped")
	final class WhenRoundTripped
	{
		@Test
		@DisplayName("parse then render then parse produces identical rows")
		void parseThenRenderThenParseProducesIdenticalRows()
		{
			var original = "name,city,note\nAlice,London,\"a, b\"\nBob,Paris,\"say \"\"hi\"\"\"";

			var firstParse = CsvTable.parse(original);
			var rendered = firstParse.render();
			var secondParse = CsvTable.parse(rendered);

			assertThat(secondParse.rows())
					.as("rows after parse-render-parse should be identical to rows after first parse")
					.usingRecursiveComparison()
					.isEqualTo(firstParse.rows());
		}
	}
}
