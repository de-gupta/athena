package de.gupta.commons.utility.math.algebra.structure.morphism;

import de.gupta.commons.utility.math.algebra.structure.binary.GroupStructure;

public interface GroupHomomorphism<S, T> extends Homomorphism<S, T>
{
	GroupStructure<S> domain();

	GroupStructure<T> codomain();
}