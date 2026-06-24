package de.gupta.commons.utility.math.algebra.element.module;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

public interface ScalarDivisible<E extends ScalarDivisible<E>>
{
	DivisionResult<E> divide(final long scalar, final RoundingStrategy<E> strategy);
}