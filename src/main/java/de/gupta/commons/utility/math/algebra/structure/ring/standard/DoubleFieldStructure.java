package de.gupta.commons.utility.math.algebra.structure.ring.standard;

import de.gupta.commons.utility.math.algebra.structure.ring.FieldStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;

public enum DoubleFieldStructure implements FieldStructure<Double>
{
	INSTANCE;

	@Override
	public Double zero()
	{
		return 0.0;
	}

	@Override
	public Double one()
	{
		return 1.0;
	}

	@Override
	public Double multiply(final Double left, final Double right)
	{
		return left * right;
	}

	@Override
	public Double add(final Double left, final Double right)
	{
		return left + right;
	}

	@Override
	public Double multiplicativeInverse(final Double element)
	{
		return 1.0 / element;
	}

	@Override
	public Double negate(final Double element)
	{
		return -element;
	}

	@Override
	public RingStructure<Double> scalars()
	{
		return this;
	}
}