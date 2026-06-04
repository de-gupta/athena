package de.gupta.commons.utility.io.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

final class CsvParser
{
	static CsvTable parse(final String content)
	{
		var rawRows = parseRawRows(content);
		return buildTable(rawRows);
	}

	private static List<List<String>> parseRawRows(final String content)
	{
		var rows = new ArrayList<List<String>>();
		var currentRow = new ArrayList<String>();
		var field = new StringBuilder();
		var inQuotedField = false;
		var afterClosingQuote = false;
		var length = content.length();

		for (var i = 0; i < length; i++)
		{
			var ch = content.charAt(i);

			if (afterClosingQuote)
			{
				afterClosingQuote = false;
				if (ch == '"')
				{
					field.append('"');
					inQuotedField = true;
				}
				else if (ch == ',')
				{
					currentRow.add(field.toString().strip());
					field.setLength(0);
				}
				else if (ch == '\r')
				{
					currentRow.add(field.toString().strip());
					field.setLength(0);
					if (i + 1 < length && content.charAt(i + 1) == '\n')
					{
						i++;
					}
					rows.add(currentRow);
					currentRow = new ArrayList<>();
				}
				else if (ch == '\n')
				{
					currentRow.add(field.toString().strip());
					field.setLength(0);
					rows.add(currentRow);
					currentRow = new ArrayList<>();
				}
				else
				{
					field.append(ch);
				}
			}
			else if (inQuotedField)
			{
				if (ch == '"')
				{
					afterClosingQuote = true;
					inQuotedField = false;
				}
				else
				{
					field.append(ch);
				}
			}
			else
			{
				if (ch == '"' && field.isEmpty())
				{
					inQuotedField = true;
				}
				else if (ch == ',')
				{
					currentRow.add(field.toString().strip());
					field.setLength(0);
				}
				else if (ch == '\r')
				{
					currentRow.add(field.toString().strip());
					field.setLength(0);
					if (i + 1 < length && content.charAt(i + 1) == '\n')
					{
						i++;
					}
					rows.add(currentRow);
					currentRow = new ArrayList<>();
				}
				else if (ch == '\n')
				{
					currentRow.add(field.toString().strip());
					field.setLength(0);
					rows.add(currentRow);
					currentRow = new ArrayList<>();
				}
				else
				{
					field.append(ch);
				}
			}
		}

		if (!field.isEmpty() || !currentRow.isEmpty())
		{
			currentRow.add(field.toString().strip());
			rows.add(currentRow);
		}

		return rows;
	}

	private static CsvTable buildTable(final List<List<String>> rawRows)
	{
		var nonBlankRows = rawRows.stream()
		                          .filter(row -> row.stream().anyMatch(cell -> !cell.isBlank()))
		                          .toList();

		if (nonBlankRows.isEmpty())
		{
			return new CsvTable(List.of(), List.of());
		}

		var headers = nonBlankRows.getFirst().stream()
		                          .map(String::strip)
		                          .toList();

		var tableRows = new ArrayList<TableRow>();
		for (var i = 1; i < nonBlankRows.size(); i++)
		{
			var raw = nonBlankRows.get(i);
			if (raw.stream().allMatch(String::isBlank))
			{
				continue;
			}
			var cells = new LinkedHashMap<String, String>();
			for (var j = 0; j < headers.size(); j++)
			{
				cells.put(headers.get(j), j < raw.size() ? raw.get(j) : "");
			}
			tableRows.add(new TableRow(Collections.unmodifiableMap(cells)));
		}

		return new CsvTable(headers,
				Collections.unmodifiableList(tableRows));
	}

	private CsvParser()
	{
	}
}