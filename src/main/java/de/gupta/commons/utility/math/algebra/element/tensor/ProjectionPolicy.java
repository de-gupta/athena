package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.algebra.Algebra;
import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

@FunctionalInterface
public interface ProjectionPolicy<E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
{
	E project(final ScalarExtension<E, R, S> accumulation);
}