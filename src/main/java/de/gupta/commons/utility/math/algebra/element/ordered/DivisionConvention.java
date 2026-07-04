package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

@FunctionalInterface
public interface DivisionConvention<E>
{
	DivisionResult<E> divide(E dividend, E divisor);
}