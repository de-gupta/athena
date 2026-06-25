package de.gupta.commons.utility.math.algebra.structure.algebra;

import de.gupta.commons.utility.math.algebra.element.algebra.Algebra;
import de.gupta.commons.utility.math.algebra.element.algebra.LinearCombination;
import de.gupta.commons.utility.math.algebra.element.algebra.ProjectionPolicy;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

public interface ScalarExtensionStructure<E, R extends Ring<R>, S extends Algebra<R, S>>
{
	default E project(final LinearCombination<S, E> accumulation, final ProjectionPolicy<S, E> policy)
	{
		return policy.project(accumulation);
	}
}