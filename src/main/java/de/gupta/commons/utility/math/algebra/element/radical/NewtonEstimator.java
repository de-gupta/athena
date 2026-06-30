package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.commons.utility.math.algebra.element.module.ScalarDivisible;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.Collections;
import java.util.function.UnaryOperator;

final class NewtonEstimator<E extends Ring<E> & ScalarDivisible<E, Long> & Radical<E>> implements Estimator<E>
{
	private static final NewtonEstimator<?> INSTANCE = new NewtonEstimator<>();

	static <E extends Ring<E> & ScalarDivisible<E, Long> & Radical<E>> Estimator<E> instance()
	{
		return (Estimator<E>) INSTANCE;
	}

	@Override
	public UnaryOperator<E> estimate(final E radicand, final int degree, final RoundingStrategy<E> rounding)
	{
		return estimate ->
				estimate.power(degree - 1)
				        .add(radicand.elementQuotient(estimate.multiplyAll(Collections.nCopies(degree - 2, estimate))))
				        .divide((long) degree, rounding).quotient();
	}

	private NewtonEstimator()
	{
	}
}