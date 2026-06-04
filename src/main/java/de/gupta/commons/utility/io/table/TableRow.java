package de.gupta.commons.utility.io.table;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class TableRow
{
	private final Map<String, String> cells;

	public String required(final String column)
	{
		Objects.requireNonNull(column, "column must not be null");
		if (!cells.containsKey(column))
		{
			throw new NoSuchElementException("No value for column: " + column);
		}
		var value = cells.get(column);
		if (value.isBlank())
		{
			throw new NoSuchElementException("Value for column '" + column + "' is blank");
		}
		return value;
	}

	public String optional(final String column, final String fallback)
	{
		Objects.requireNonNull(column, "column must not be null");
		Objects.requireNonNull(fallback, "fallback must not be null");
		return cells.getOrDefault(column, fallback);
	}

	public Map<String, String> cells()
	{
		return cells;
	}

	TableRow(final Map<String, String> cells)
	{
		this.cells = cells;
	}
}