package de.gupta.commons.utility.math.algebra.structure.ordered;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

public interface ScalarDivisibleStructure<E>
{
	DivisionResult<E> divide(final E element, final long scalar, final RoundingStrategy<E> strategy);
}