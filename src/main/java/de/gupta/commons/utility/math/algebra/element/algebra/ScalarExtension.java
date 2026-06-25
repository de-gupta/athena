package de.gupta.commons.utility.math.algebra.element.algebra;

import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

public interface ScalarExtension<E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
{
	default E project(final LinearCombination<S, E> accumulation, final ProjectionPolicy<S, E> policy)
	{
		return policy.project(accumulation);
	}
}