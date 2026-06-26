package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.algebra.Algebra;
import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.List;

public sealed interface ScalarExtension<E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
		extends Module<ScalarExtension<E, R, S>, S> permits ScalarExtensionImpl
{
	default boolean isEmpty()
	{
		return terms().isEmpty();
	}

	List<LinearCombination.Entry<S, E>> terms();

	default E project(final ProjectionPolicy<E, R, S> policy)
	{
		return policy.project(this);
	}
}