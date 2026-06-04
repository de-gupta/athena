package de.gupta.commons.utility.io.table;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TableRow#of")
final class TableRowOfTest
{
	@Nested
	@DisplayName("when cells are provided")
	final class WhenCellsAreProvided
	{
		@Test
		@DisplayName("returns a row containing all supplied cells")
		void returnsARowContainingAllSuppliedCells()
		{
			var cells = new LinkedHashMap<String, String>();
			cells.put("name", "Alice");
			cells.put("city", "London");

			var row = TableRow.of(cells);

			assertThat(row.cells())
					.as("row should contain exactly the cells supplied to of()")
					.containsEntry("name", "Alice")
					.containsEntry("city", "London");
		}

		@Test
		@DisplayName("preserves the insertion order of the supplied map")
		void preservesTheInsertionOrderOfTheSuppliedMap()
		{
			var cells = new LinkedHashMap<String, String>();
			cells.put("z", "zv");
			cells.put("a", "av");
			cells.put("m", "mv");

			var row = TableRow.of(cells);

			assertThat(row.cells().sequencedKeySet())
					.as("cell keys should appear in the insertion order of the supplied map")
					.containsExactly("z", "a", "m");
		}
	}

	@Nested
	@DisplayName("when the input map is mutated after construction")
	final class WhenTheInputMapIsMutatedAfterConstruction
	{
		@Test
		@DisplayName("the row is not affected — of() takes a defensive copy")
		void theRowIsNotAffected()
		{
			var cells = new LinkedHashMap<String, String>();
			cells.put("name", "Alice");

			var row = TableRow.of(cells);
			cells.put("name", "Mutated");

			assertThat(row.cells().get("name"))
					.as("mutating the source map after of() should not change the row")
					.isEqualTo("Alice");
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
			var cells = new LinkedHashMap<String, String>();
			cells.put("name", "Alice");

			var row = TableRow.of(cells);

			assertThatThrownBy(() -> row.cells().put("extra", "value"))
					.as("cells() from a row created via of() should be unmodifiable")
					.isInstanceOf(UnsupportedOperationException.class);
		}
	}

	@Nested
	@DisplayName("with null arguments")
	final class WithNullArguments
	{
		@Test
		@DisplayName("throws NullPointerException when cells is null")
		void throwsNullPointerExceptionWhenCellsIsNull()
		{
			assertThatThrownBy(() -> TableRow.of(null))
					.as("null cells map should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}
}
