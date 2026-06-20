package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public enum StandardEuclideanRoundingStrategy implements EuclideanRoundingStrategy
{
	FLOOR,
	CEILING,
	TRUNCATE;

	@Override
	public <E> E round(final DivisionResult<E> division, final Predicate<E> isZero, final Predicate<E> isNegative,
	                   final E one, final BinaryOperator<E> add)
	{
		return switch (this)
		{
			case FLOOR -> division.quotient();
			case CEILING ->
					isZero.test(division.remainder()) ? division.quotient() : add.apply(division.quotient(), one);
			case TRUNCATE -> !isZero.test(division.remainder()) && isNegative.test(division.quotient())
					? add.apply(division.quotient(), one)
					: division.quotient();
		};
	}
}