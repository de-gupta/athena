package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.commons.utility.math.algebra.element.module.ScalarDivisible;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.Quotientable;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.Collections;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface Estimator<E>
{
	static <E extends Ring<E> & Quotientable<E> & ScalarDivisible<E, Long> & Radical<E>> Estimator<E> newton()
	{
		return (radicand, degree, rounding) -> estimate ->
				estimate.power(degree - 1)
				        .add(radicand.quotient(estimate.multiplyAll(Collections.nCopies(degree - 2, estimate))))
				        .divide((long) degree, rounding).quotient();
	}

	UnaryOperator<E> estimate(final E radicand, final int degree, final RoundingStrategy<E> rounding);
}