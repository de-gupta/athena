package de.gupta.commons.utility.io.table;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TableRow#optional(String, String)")
final class TableRowOptionalTest
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

			assertThat(row.optional("name", "default"))
					.as("present non-blank column should return its value, not the fallback")
					.isEqualTo("Alice");
		}
	}

	@Nested
	@DisplayName("when column is present but blank")
	final class WhenColumnIsPresentButBlank
	{
		@Test
		@DisplayName("returns the blank value rather than the fallback")
		void returnsTheBlankValueRatherThanTheFallback()
		{
			var row = rowOf("name", "   ");

			assertThat(row.optional("name", "default"))
					.as("optional should return the actual blank value — fallback is only for absent columns")
					.isEqualTo("   ");
		}
	}

	@Nested
	@DisplayName("when column is absent")
	final class WhenColumnIsAbsent
	{
		@Test
		@DisplayName("returns the fallback value")
		void returnsTheFallbackValue()
		{
			var row = rowOf("name", "Alice");

			assertThat(row.optional("missing", "default"))
					.as("absent column should return the supplied fallback")
					.isEqualTo("default");
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

			assertThatThrownBy(() -> row.optional(null, "default"))
					.as("null column should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("throws NullPointerException when fallback is null")
		void throwsNullPointerExceptionWhenFallbackIsNull()
		{
			var row = rowOf("name", "Alice");

			assertThatThrownBy(() -> row.optional("name", null))
					.as("null fallback should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}
}