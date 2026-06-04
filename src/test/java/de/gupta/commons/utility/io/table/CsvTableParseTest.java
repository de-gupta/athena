package de.gupta.commons.utility.io.table;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CsvTable#parse")
final class CsvTableParseTest
{
	@TempDir
	Path tempDir;

	private static Path writeFile(final Path dir, final String name, final String content)
	{
		try
		{
			var file = dir.resolve(name);
			Files.writeString(file, content);
			return file;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Nested
	@DisplayName("when content is null")
	final class WhenContentIsNull
	{
		@Test
		@DisplayName("throws NullPointerException")
		void throwsNullPointerException()
		{
			assertThatThrownBy(() -> CsvTable.parse((String) null))
					.as("null content should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}

	@Nested
	@DisplayName("when content is empty")
	final class WhenContentIsEmpty
	{
		@Test
		@DisplayName("returns a table with no headers and no rows")
		void returnsATableWithNoHeadersAndNoRows()
		{
			var table = CsvTable.parse("");

			assertThat(table.rows())
					.as("empty content should produce no rows")
					.isEmpty();
		}
	}

	@Nested
	@DisplayName("when content is a header row only")
	final class WhenContentIsAHeaderRowOnly
	{
		@Test
		@DisplayName("returns a table with headers and an empty row list")
		void returnsATableWithHeadersAndAnEmptyRowList()
		{
			var table = CsvTable.parse("name,age,city");

			assertThat(table.rows())
					.as("header-only CSV should produce no data rows")
					.isEmpty();
		}
	}

	@Nested
	@DisplayName("when content has simple data")
	final class WhenContentHasSimpleData
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("parsesCorrectRowsAndValuesCases")
		@DisplayName("parses the correct number of rows with the correct cell values")
		void parsesCorrectRowsAndValues(final String as, final ParseCase tc)
		{
			var table = CsvTable.parse(tc.content());

			assertThat(table.rows())
					.as(as + " — row count")
					.hasSize(tc.expectedRows());
			assertThat(table.rows().getFirst().cells().values())
					.as(as + " — first row values")
					.containsExactlyElementsOf(tc.firstRowValues());
		}

		private static Stream<Arguments> parsesCorrectRowsAndValuesCases()
		{
			return Stream.of(
					ParseCase.of("two columns two rows",
							"name,city\nAlice,London\nBob,Paris",
							2,
							List.of("Alice", "London")),
					ParseCase.of("three columns one row",
							"a,b,c\n1,2,3",
							1,
							List.of("1", "2", "3"))
			).map(tc -> Arguments.of(tc.as(), tc));
		}

		private record ParseCase(String as, String content, int expectedRows,
		                         List<String> firstRowValues)
		{
			private static ParseCase of(final String as, final String content, final int expectedRows,
			                            final List<String> firstRowValues)
			{
				return new ParseCase(as, content, expectedRows, firstRowValues);
			}
		}
	}

	@Nested
	@DisplayName("when fields are quoted (RFC-4180)")
	final class WhenFieldsAreQuoted
	{
		@Test
		@DisplayName("parses a quoted field containing an embedded comma")
		void parsesAQuotedFieldContainingAnEmbeddedComma()
		{
			var table = CsvTable.parse("name,address\nAlice,\"123 Main St, Apt 4\"");

			assertThat(table.rows().getFirst().cells().get("address"))
					.as("quoted field with comma should be read as one value")
					.isEqualTo("123 Main St, Apt 4");
		}

		@Test
		@DisplayName("parses a quoted field containing an embedded newline")
		void parsesAQuotedFieldContainingAnEmbeddedNewline()
		{
			var table = CsvTable.parse("name,notes\nAlice,\"line one\nline two\"");

			assertThat(table.rows().getFirst().cells().get("notes"))
					.as("quoted field with newline should be read as one multi-line value")
					.isEqualTo("line one\nline two");
		}

		@Test
		@DisplayName("parses a doubled quote inside a quoted field as a literal double-quote")
		void parsesADoubledQuoteAsALiteralDoubleQuote()
		{
			var table = CsvTable.parse("name,quote\nAlice,\"say \"\"hello\"\"\"");

			assertThat(table.rows().getFirst().cells().get("quote"))
					.as("doubled quote inside quoted field should produce a single literal quote")
					.isEqualTo("say \"hello\"");
		}
	}

	@Nested
	@DisplayName("when whitespace is present")
	final class WhenWhitespaceIsPresent
	{
		@Test
		@DisplayName("strips leading and trailing whitespace from header names")
		void stripsLeadingAndTrailingWhitespaceFromHeaderNames()
		{
			var table = CsvTable.parse("  name  ,  city  \nAlice,London");

			assertThat(table.rows().getFirst().cells().keySet())
					.as("header names should be stripped of surrounding whitespace")
					.containsExactly("name", "city");
		}

		@Test
		@DisplayName("strips leading and trailing whitespace from cell values")
		void stripsLeadingAndTrailingWhitespaceFromCellValues()
		{
			var table = CsvTable.parse("name,city\n  Alice  ,  London  ");

			assertThat(table.rows().getFirst().cells().values())
					.as("cell values should be stripped of surrounding whitespace")
					.containsExactly("Alice", "London");
		}
	}

	@Nested
	@DisplayName("when rows are all blank")
	final class WhenRowsAreAllBlank
	{
		@Test
		@DisplayName("silently skips all-blank data rows")
		void silentlySkipsAllBlankDataRows()
		{
			var table = CsvTable.parse("name,city\nAlice,London\n  ,  \nBob,Paris");

			assertThat(table.rows())
					.as("all-blank rows should be silently skipped")
					.hasSize(2);
		}
	}

	@Nested
	@DisplayName("when line endings are CRLF")
	final class WhenLineEndingsAreCrlf
	{
		@Test
		@DisplayName("parses CRLF-delimited CSV correctly")
		void parsesCrlfDelimitedCsvCorrectly()
		{
			var table = CsvTable.parse("name,city\r\nAlice,London\r\nBob,Paris");

			assertThat(table.rows())
					.as("CRLF line endings should be treated as row separators")
					.hasSize(2);
			assertThat(table.rows().getFirst().cells().get("name"))
					.as("first row name cell should be parsed correctly")
					.isEqualTo("Alice");
		}
	}

	@Nested
	@DisplayName("when rows have fewer fields than headers")
	final class WhenRowsHaveFewerFieldsThanHeaders
	{
		@Test
		@DisplayName("pads absent fields with empty string")
		void padsAbsentFieldsWithEmptyString()
		{
			var table = CsvTable.parse("name,city,country\nAlice,London");

			assertThat(table.rows().getFirst().cells().get("country"))
					.as("missing field should be padded with empty string")
					.isEqualTo("");
		}
	}

	@Nested
	@DisplayName("when rows have more fields than headers")
	final class WhenRowsHaveMoreFieldsThanHeaders
	{
		@Test
		@DisplayName("ignores extra fields beyond the header count")
		void ignoresExtraFieldsBeyondTheHeaderCount()
		{
			var table = CsvTable.parse("name,city\nAlice,London,England,Extra");

			assertThat(table.rows().getFirst().cells())
					.as("row with extra fields should only expose cells for the declared headers")
					.hasSize(2);
			assertThat(table.rows().getFirst().cells().get("city"))
					.as("city cell should still be correctly mapped")
					.isEqualTo("London");
		}
	}

	@Nested
	@DisplayName("when blank rows appear at the boundaries")
	final class WhenBlankRowsAppearAtTheBoundaries
	{
		@Test
		@DisplayName("blank rows before the header are skipped — first non-blank row becomes the header")
		void blankRowsBeforeTheHeaderAreSkipped()
		{
			var table = CsvTable.parse("\n\nname,city\nAlice,London");

			assertThat(table.rows())
					.as("blank rows preceding the header should be discarded; one data row should remain")
					.hasSize(1);
			assertThat(table.rows().getFirst().cells().get("name"))
					.as("name cell should be parsed correctly despite leading blank rows")
					.isEqualTo("Alice");
		}

		@Test
		@DisplayName("blank rows after the last data row are skipped")
		void blankRowsAfterTheLastDataRowAreSkipped()
		{
			var table = CsvTable.parse("name,city\nAlice,London\n\n\n");

			assertThat(table.rows())
					.as("trailing blank rows should be silently skipped")
					.hasSize(1);
		}
	}

	@Nested
	@DisplayName("when content contains only whitespace or newlines")
	final class WhenContentContainsOnlyWhitespaceOrNewlines
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsEmptyTableCases")
		@DisplayName("returns an empty table")
		void returnsAnEmptyTable(final String as, final String content)
		{
			var table = CsvTable.parse(content);

			assertThat(table.rows())
					.as(as)
					.isEmpty();
		}

		private static Stream<Arguments> returnsEmptyTableCases()
		{
			return Stream.of(
					Arguments.of("spaces only", "   "),
					Arguments.of("single newline", "\n"),
					Arguments.of("multiple newlines", "\n\n\n"),
					Arguments.of("CRLF only", "\r\n\r\n")
			);
		}
	}

	@Nested
	@DisplayName("when a row has a trailing comma")
	final class WhenARowHasATrailingComma
	{
		@Test
		@DisplayName("trailing comma creates an implicit empty last field")
		void trailingCommaCreatesAnImplicitEmptyLastField()
		{
			var table = CsvTable.parse("name,city,\nAlice,London,");

			assertThat(table.rows().getFirst().cells().get(""))
					.as("trailing comma in header creates a blank-named column; data row's trailing comma maps to it")
					.isEqualTo("");
		}
	}

	@Nested
	@DisplayName("when header row contains duplicate names")
	final class WhenHeaderRowContainsDuplicateNames
	{
		@Test
		@DisplayName("last column's value wins for the duplicate key")
		void lastColumnsValueWinsForTheDuplicateKey()
		{
			var table = CsvTable.parse("name,score,name\nAlice,100,Alicia");

			assertThat(table.rows().getFirst().cells().get("name"))
					.as("when a header appears twice the last column's value overwrites the first due to map put semantics")
					.isEqualTo("Alicia");
			assertThat(table.rows().getFirst().cells())
					.as("duplicate header collapses to one key — only two distinct keys in the result")
					.hasSize(2);
		}
	}

	@Nested
	@DisplayName("when line endings are mixed")
	final class WhenLineEndingsAreMixed
	{
		@Test
		@DisplayName("parses a file with both LF and CRLF line endings correctly")
		void parsesAFileWithBothLfAndCrlfLineEndingsCorrectly()
		{
			var table = CsvTable.parse("name,city\r\nAlice,London\nBob,Paris\r\nCarol,Berlin");

			assertThat(table.rows())
					.as("mixed CRLF and LF line endings should all be treated as row separators")
					.hasSize(3);
			assertThat(table.rows().get(1).cells().get("name"))
					.as("row following a LF-only terminator should be parsed correctly")
					.isEqualTo("Bob");
		}
	}

	@Nested
	@DisplayName("when CSV has a single column")
	final class WhenCsvHasASingleColumn
	{
		@Test
		@DisplayName("parses all values from the single column")
		void parsesAllValuesFromTheSingleColumn()
		{
			var table = CsvTable.parse("name\nAlice\nBob\nCarol");

			assertThat(table.rows())
					.as("single-column CSV should produce three data rows")
					.hasSize(3);
			assertThat(table.rows().getFirst().cells().get("name"))
					.as("first row should contain Alice")
					.isEqualTo("Alice");
			assertThat(table.rows().getLast().cells().get("name"))
					.as("last row should contain Carol")
					.isEqualTo("Carol");
		}
	}

	// --- Helpers ---

	@Nested
	@DisplayName("when parsing from a Path")
	final class WhenParsingFromAPath
	{
		@Test
		@DisplayName("parses file content correctly")
		void parsesFileContentCorrectly()
		{
			var file = writeFile(tempDir, "data.csv", "name,city\nAlice,London");

			var table = CsvTable.parse(file);

			assertThat(table.rows())
					.as("table parsed from file should have one data row")
					.hasSize(1);
			assertThat(table.rows().getFirst().cells().get("name"))
					.as("name cell should be parsed from file")
					.isEqualTo("Alice");
		}

		@Test
		@DisplayName("throws UncheckedIOException for a non-existent file")
		void throwsUncheckedIOExceptionForANonExistentFile()
		{
			var absent = tempDir.resolve("does-not-exist.csv");

			assertThatThrownBy(() -> CsvTable.parse(absent))
					.as("non-existent file should throw UncheckedIOException")
					.isInstanceOf(UncheckedIOException.class);
		}

		@Test
		@DisplayName("throws NullPointerException for a null path")
		void throwsNullPointerExceptionForANullPath()
		{
			assertThatThrownBy(() -> CsvTable.parse((Path) null))
					.as("null path should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}
}