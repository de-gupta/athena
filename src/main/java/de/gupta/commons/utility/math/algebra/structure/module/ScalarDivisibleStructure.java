package de.gupta.commons.utility.math.algebra.structure.module;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

public interface ScalarDivisibleStructure<E>
{
	DivisionResult<E> shrink(final E element, final long scalar, final RoundingStrategy<E> strategy);
}