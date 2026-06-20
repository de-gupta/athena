package de.gupta.commons.utility.math.algebra.structure.module.standard;

import de.gupta.commons.utility.math.algebra.structure.module.VectorSpaceStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.FieldStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.DoubleFieldStructure;

public enum DoubleVectorSpaceStructure implements VectorSpaceStructure<Double, Double>
{
	INSTANCE;

	@Override
	public FieldStructure<Double> scalars()
	{
		return DoubleFieldStructure.INSTANCE;
	}

	@Override
	public Double scale(final Double scalar, final Double vector)
	{
		return scalar * vector;
	}

	@Override
	public Double add(final Double left, final Double right)
	{
		return left + right;
	}

	@Override
	public Double negate(final Double element)
	{
		return -element;
	}

	@Override
	public Double zero()
	{
		return 0.0;
	}
}