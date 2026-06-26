package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.algebra.Algebra;
import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

public interface ScalarExtension<E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
		extends Module<ScalarExtension<E, R, S>, S>
{
	default E project(final LinearCombination<S, E> accumulation, final ProjectionPolicy<S, E> policy)
	{
		return policy.project(accumulation);
	}
}