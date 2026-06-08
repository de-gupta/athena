package de.gupta.commons.utility.math.analysis.space.standard;

import de.gupta.commons.utility.math.analysis.space.InterpolatableSpace;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public enum LocalDateInterpolatableSpace implements InterpolatableSpace<LocalDate>
{
	INSTANCE;

	@Override
	public double distance(final LocalDate left, final LocalDate right)
	{
		return Math.abs(ChronoUnit.DAYS.between(left, right));
	}

	@Override
	public double parameter(final LocalDate left, final LocalDate right, final LocalDate query)
	{
		return (double) ChronoUnit.DAYS.between(left, query) / ChronoUnit.DAYS.between(left, right);
	}

	@Override
	public int compare(final LocalDate left, final LocalDate right)
	{
		return left.compareTo(right);
	}
}
