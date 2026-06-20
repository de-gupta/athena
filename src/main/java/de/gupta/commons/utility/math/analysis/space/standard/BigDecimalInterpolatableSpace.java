package de.gupta.commons.utility.math.analysis.space.standard;

import de.gupta.commons.utility.math.analysis.space.DifferentiableSpace;

import java.math.BigDecimal;
import java.math.MathContext;

public enum BigDecimalInterpolatableSpace implements DifferentiableSpace<BigDecimal>
{
	LINEAR
			{
				@Override
				public double distance(final BigDecimal left, final BigDecimal right)
				{
					return left.subtract(right).abs().doubleValue();
				}

				@Override
				public double parameter(final BigDecimal left, final BigDecimal right, final BigDecimal query)
				{
					return query.subtract(left)
					            .divide(right.subtract(left), MathContext.DECIMAL128)
					            .doubleValue();
				}
			},
	LOG
			{
				@Override
				public double distance(final BigDecimal left, final BigDecimal right)
				{
					return Math.abs(Math.log(right.doubleValue() / left.doubleValue()));
				}

				@Override
				public double parameter(final BigDecimal left, final BigDecimal right, final BigDecimal query)
				{
					return Math.log(query.doubleValue() / left.doubleValue())
							/ Math.log(right.doubleValue() / left.doubleValue());
				}
			};

	@Override
	public int compare(final BigDecimal left, final BigDecimal right)
	{
		return left.compareTo(right);
	}
}
