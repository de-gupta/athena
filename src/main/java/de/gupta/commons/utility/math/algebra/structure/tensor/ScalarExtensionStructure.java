package de.gupta.commons.utility.math.algebra.structure.tensor;

import de.gupta.commons.utility.math.algebra.element.algebra.Algebra;
import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;
import de.gupta.commons.utility.math.algebra.element.tensor.LinearCombination;
import de.gupta.commons.utility.math.algebra.element.tensor.ProjectionPolicy;
import de.gupta.commons.utility.math.algebra.element.tensor.ScalarExtension;
import de.gupta.commons.utility.math.algebra.structure.module.ModuleStructure;

public interface ScalarExtensionStructure<E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
		extends ModuleStructure<ScalarExtension<E, R, S>, S>
{
	default E project(final LinearCombination<S, E> accumulation, final ProjectionPolicy<S, E> policy)
	{
		return policy.project(accumulation);
	}
}