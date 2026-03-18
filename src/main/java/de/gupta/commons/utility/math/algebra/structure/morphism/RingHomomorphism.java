package de.gupta.commons.utility.math.algebra.structure.morphism;

import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;

public interface RingHomomorphism<S, T> extends Homomorphism<S, T>
{
	RingStructure<S> domain();

	RingStructure<T> codomain();
}