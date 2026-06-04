package de.gupta.commons.utility.io.table;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TableRow#required(String)")
final class TableRowRequiredTest
{
	private static TableRow rowOf(final String key, final String value)
	{
		var map = new LinkedHashMap<String, String>();
		map.put(key, value);
		return new TableRow(Collections.unmodifiableSequencedMap(map));
	}

	@Nested
	@DisplayName("when column is present with a non-blank value")
	final class WhenColumnIsPresentWithANonBlankValue
	{
		@Test
		@DisplayName("returns the cell value")
		void returnsTheCellValue()
		{
			var row = rowOf("name", "Alice");

			assertThat(row.required("name"))
					.as("present non-blank column should return its value")
					.isEqualTo("Alice");
		}
	}

	@Nested
	@DisplayName("when column is present but blank")
	final class WhenColumnIsPresentButBlank
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("throwsNoSuchElementExceptionCases")
		@DisplayName("throws NoSuchElementException")
		void throwsNoSuchElementException(final String as, final String blankValue)
		{
			var row = rowOf("name", blankValue);

			assertThatThrownBy(() -> row.required("name"))
					.as(as)
					.isInstanceOf(NoSuchElementException.class);
		}

		private static Stream<Arguments> throwsNoSuchElementExceptionCases()
		{
			return Stream.of(
					Arguments.of("empty string value", ""),
					Arguments.of("spaces only", "   "),
					Arguments.of("tab only", "\t")
			);
		}
	}

	@Nested
	@DisplayName("when column is absent")
	final class WhenColumnIsAbsent
	{
		@Test
		@DisplayName("throws NoSuchElementException with the column name in the message")
		void throwsNoSuchElementExceptionWithTheColumnNameInTheMessage()
		{
			var row = rowOf("name", "Alice");

			assertThatThrownBy(() -> row.required("missing"))
					.as("absent column should throw NoSuchElementException")
					.isInstanceOf(NoSuchElementException.class)
					.hasMessageContaining("missing");
		}
	}

	// --- Helpers ---

	@Nested
	@DisplayName("with null arguments")
	final class WithNullArguments
	{
		@Test
		@DisplayName("throws NullPointerException when column is null")
		void throwsNullPointerExceptionWhenColumnIsNull()
		{
			var row = rowOf("name", "Alice");

			assertThatThrownBy(() -> row.required(null))
					.as("null column should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}
}