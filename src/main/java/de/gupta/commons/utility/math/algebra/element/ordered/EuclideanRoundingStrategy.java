package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveSemigroup;
import de.gupta.commons.utility.math.algebra.element.ring.IntegralDomain;
import de.gupta.commons.utility.math.algebra.structure.ordered.OrderedEuclideanDomainStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public enum EuclideanRoundingStrategy
{
	FLOOR,
	CEILING,
	TRUNCATE;

	public <E extends OrderedEuclideanDomain<E>> E round(final DivisionResult<E> division)
	{
		return round(division, IntegralDomain::isZero, OrderedAdditiveGroup::isNegative, division.quotient().one(),
				AdditiveSemigroup::add);
	}

	private <E> E round(final DivisionResult<E> division, final Predicate<E> isZero, final Predicate<E> isNegative,
	                    final E one, final BinaryOperator<E> add)
	{
		return switch (this)
		{
			case FLOOR -> division.quotient();
			case CEILING ->
					isZero.test(division.remainder()) ? division.quotient() : add.apply(division.quotient(), one);
			case TRUNCATE -> !isZero.test(division.remainder()) && isNegative.test(division.quotient()) ?
					add.apply(division.quotient(), one) : division.quotient();
		};
	}

	public <E> E round(final DivisionResult<E> division, final OrderedEuclideanDomainStructure<E> structure)
	{
		return round(division, structure::isZero, structure::isNegative, structure.one(), structure::add);
	}
}