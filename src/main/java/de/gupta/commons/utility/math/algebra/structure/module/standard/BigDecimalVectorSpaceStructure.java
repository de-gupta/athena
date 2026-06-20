package de.gupta.commons.utility.math.algebra.structure.module.standard;

import de.gupta.commons.utility.math.algebra.structure.module.VectorSpaceStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.FieldStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.BigDecimalFieldStructure;

import java.math.BigDecimal;
import java.math.MathContext;

public enum BigDecimalVectorSpaceStructure implements VectorSpaceStructure<BigDecimal, BigDecimal>
{
	INSTANCE;

	@Override
	public FieldStructure<BigDecimal> scalars()
	{
		return BigDecimalFieldStructure.INSTANCE;
	}

	@Override
	public BigDecimal scale(final BigDecimal scalar, final BigDecimal vector)
	{
		return scalar.multiply(vector, MathContext.DECIMAL128).stripTrailingZeros();
	}

	@Override
	public BigDecimal add(final BigDecimal left, final BigDecimal right)
	{
		return left.add(right).stripTrailingZeros();
	}

	@Override
	public BigDecimal negate(final BigDecimal element)
	{
		return element.negate();
	}

	@Override
	public BigDecimal zero()
	{
		return BigDecimal.ZERO;
	}
}