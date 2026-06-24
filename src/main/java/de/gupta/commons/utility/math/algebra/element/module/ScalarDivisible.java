package de.gupta.commons.utility.math.algebra.element.module;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

public interface ScalarDivisible<E extends ScalarDivisible<E, S>, S>
{
	DivisionResult<E> divide(final S scalar, final RoundingStrategy<E> strategy);
}