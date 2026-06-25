package de.gupta.commons.utility.math.algebra.structure.algebra;

import de.gupta.commons.utility.math.algebra.structure.module.ModuleStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;

public interface AlgebraStructure<R, A> extends RingStructure<A>, ModuleStructure<A, R>
{
	@Override
	default A scale(final R scalar, final A vector)
	{
		return multiply(embed(scalar), vector);
	}

	A embed(final R scalar);
}