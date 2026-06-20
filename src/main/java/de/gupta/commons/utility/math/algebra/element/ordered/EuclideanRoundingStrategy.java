package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveSemigroup;
import de.gupta.commons.utility.math.algebra.element.ring.IntegralDomain;
import de.gupta.commons.utility.math.algebra.structure.ordered.OrderedEuclideanDomainStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

import java.util.function.BinaryOperator;
import java.util.function.Predicate;

@FunctionalInterface
public interface EuclideanRoundingStrategy
{
	EuclideanRoundingStrategy FLOOR = StandardEuclideanRoundingStrategy.FLOOR;
	EuclideanRoundingStrategy CEILING = StandardEuclideanRoundingStrategy.CEILING;
	EuclideanRoundingStrategy TRUNCATE = StandardEuclideanRoundingStrategy.TRUNCATE;

	default <E extends OrderedEuclideanDomain<E>> E round(final DivisionResult<E> division)
	{
		return round(division, IntegralDomain::isZero, OrderedAdditiveGroup::isNegative, division.quotient().one(),
				AdditiveSemigroup::add);
	}

	<E> E round(DivisionResult<E> division, Predicate<E> isZero, Predicate<E> isNegative, E one,
	            BinaryOperator<E> add);

	default <E> E round(final DivisionResult<E> division, final OrderedEuclideanDomainStructure<E> structure)
	{
		return round(division, structure::isZero, structure::isNegative, structure.one(), structure::add);
	}
}