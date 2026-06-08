package de.gupta.commons.utility.math.analysis.space.standard;

import de.gupta.commons.utility.math.analysis.space.DifferentiableSpace;

public enum DoubleInterpolatableSpace implements DifferentiableSpace<Double>
{
	LINEAR
			{
				@Override
				public double distance(final Double left, final Double right)
				{
					return Math.abs(right - left);
				}

				@Override
				public double parameter(final Double left, final Double right, final Double query)
				{
					return (query - left) / (right - left);
				}
			},
	LOG
			{
				@Override
				public double distance(final Double left, final Double right)
				{
					return Math.abs(Math.log(right / left));
				}

				@Override
				public double parameter(final Double left, final Double right, final Double query)
				{
					return Math.log(query / left) / Math.log(right / left);
				}
			};

	@Override
	public int compare(final Double a, final Double b)
	{
		return Double.compare(a, b);
	}
}