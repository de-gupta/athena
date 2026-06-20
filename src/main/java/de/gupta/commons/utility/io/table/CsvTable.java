package de.gupta.commons.utility.io.table;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CsvTable
{
	private final List<String> headers;
	private final List<TableRow> rows;

	public static CsvTable of(final List<String> headers, final List<Map<String, String>> rows)
	{
		Objects.requireNonNull(headers, "headers must not be null");
		Objects.requireNonNull(rows, "rows must not be null");
		var immutableHeaders = List.copyOf(headers);
		var tableRows = rows.stream()
		                    .map(rowMap -> buildRow(immutableHeaders, rowMap))
		                    .toList();
		return new CsvTable(immutableHeaders, tableRows);
	}

	public static CsvTable parse(final String content)
	{
		Objects.requireNonNull(content, "content must not be null");
		return CsvParser.parse(content);
	}

	public static CsvTable parse(final Path path)
	{
		Objects.requireNonNull(path, "path must not be null");
		try
		{
			return CsvParser.parse(Files.readString(path));
		}
		catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public List<TableRow> rows()
	{
		return rows;
	}

	public String render()
	{
		if (headers.isEmpty())
		{
			return "";
		}
		var sb = new StringBuilder();
		appendRow(sb, headers);
		for (var row : rows)
		{
			appendRow(sb, List.copyOf(row.cells().values()));
		}
		return sb.toString();
	}

	private static TableRow buildRow(final List<String> headers, final Map<String, String> rowMap)
	{
		Objects.requireNonNull(rowMap, "row map must not be null");
		var cells = new LinkedHashMap<String, String>();
		for (var header : headers)
		{
			var value = rowMap.getOrDefault(header, "");
			cells.put(header, value != null ? value : "");
		}
		return TableRow.of(cells);
	}

	private static void appendRow(final StringBuilder sb, final List<String> fields)
	{
		for (var i = 0; i < fields.size(); i++)
		{
			if (i > 0)
			{
				sb.append(',');
			}
			sb.append(quoteIfNeeded(fields.get(i)));
		}
		sb.append("\r\n");
	}

	private static String quoteIfNeeded(final String field)
	{
		if (field.contains(",") || field.contains("\"") || field.contains("\r") || field.contains("\n"))
		{
			return '"' + field.replace("\"", "\"\"") + '"';
		}
		return field;
	}

	CsvTable(final List<String> headers, final List<TableRow> rows)
	{
		this.headers = headers;
		this.rows = rows;
	}
}