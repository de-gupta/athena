package de.gupta.commons.utility.math.algebra.structure.module;

import de.gupta.commons.utility.math.algebra.element.ordered.DivisionConvention;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

public interface ScalarDivisibleStructure<E, S>
{
	DivisionResult<E> shrink(final E element, final S scalar, final DivisionConvention<E> strategy);
}