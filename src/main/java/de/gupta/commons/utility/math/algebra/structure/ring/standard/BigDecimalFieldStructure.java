package de.gupta.commons.utility.math.algebra.structure.ring.standard;

import de.gupta.commons.utility.math.algebra.structure.ring.FieldStructure;

import java.math.BigDecimal;
import java.math.MathContext;

public enum BigDecimalFieldStructure implements FieldStructure<BigDecimal>
{
	INSTANCE;

	@Override
	public BigDecimal zero()
	{
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal one()
	{
		return BigDecimal.ONE;
	}

	@Override
	public BigDecimal multiply(final BigDecimal left, final BigDecimal right)
	{
		return left.multiply(right, MathContext.DECIMAL128).stripTrailingZeros();
	}

	@Override
	public BigDecimal add(final BigDecimal left, final BigDecimal right)
	{
		return left.add(right).stripTrailingZeros();
	}

	@Override
	public BigDecimal multiplicativeInverse(final BigDecimal element)
	{
		return BigDecimal.ONE.divide(element, MathContext.DECIMAL128).stripTrailingZeros();
	}

	@Override
	public BigDecimal negate(final BigDecimal element)
	{
		return element.negate();
	}
}