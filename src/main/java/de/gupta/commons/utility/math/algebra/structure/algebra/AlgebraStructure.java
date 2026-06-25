package de.gupta.commons.utility.math.algebra.structure.algebra;

import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;

public interface AlgebraStructure<R, A> extends RingStructure<A>
{
	A embed(final R scalar);
}