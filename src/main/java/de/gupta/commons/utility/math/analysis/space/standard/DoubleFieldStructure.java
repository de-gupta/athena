package de.gupta.commons.utility.math.analysis.space.standard;

import de.gupta.commons.utility.math.algebra.structure.ring.FieldStructure;

enum DoubleFieldStructure implements FieldStructure<Double>
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
	public Double add(final Double left, final Double right)
	{
		return left + right;
	}

	@Override
	public Double multiply(final Double left, final Double right)
	{
		return left * right;
	}

	@Override
	public Double additiveInverse(final Double element)
	{
		return -element;
	}

	@Override
	public Double multiplicativeInverse(final Double element)
	{
		return 1.0 / element;
	}
}
