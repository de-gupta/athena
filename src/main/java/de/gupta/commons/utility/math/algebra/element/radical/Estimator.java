package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface Estimator<E>
{
	UnaryOperator<E> estimate(final E radicand, final int degree, final RoundingStrategy<E> rounding);
}