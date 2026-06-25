package de.gupta.commons.utility.math.algebra.structure.algebra;

import de.gupta.commons.utility.math.algebra.structure.module.ModuleStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;

public interface AlgebraStructure<R, A> extends RingStructure<A>, ModuleStructure<A, R>
{
	A embed(final R scalar);
}