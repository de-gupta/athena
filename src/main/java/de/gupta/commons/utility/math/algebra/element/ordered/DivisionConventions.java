package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.structure.ordered.OrderedEuclideanDomainStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public final class DivisionConventions
{
	public static <E extends OrderedEuclideanDomain<E>> DivisionConvention<E> floor()
	{
		return OrderedEuclideanDomain::divideFloor;
	}

	public static <E> DivisionConvention<E> floor(final OrderedEuclideanDomainStructure<E> structure)
	{
		return structure::divideFloor;
	}

	public static <E extends OrderedEuclideanDomain<E>> DivisionConvention<E> ceiling()
	{
		return (dividend, divisor) ->
		{
			DivisionResult<E> floor = dividend.divideFloor(divisor);
			if (floor.remainder().isZero()) return floor;
			return DivisionResult.of(
					floor.quotient().add(floor.quotient().one()),
					floor.remainder().subtract(divisor));
		};
	}

	public static <E> DivisionConvention<E> ceiling(final OrderedEuclideanDomainStructure<E> structure)
	{
		return ceiling(structure::divideFloor, structure::isZero, structure.one(), structure::add, structure::subtract);
	}

	public static <E> DivisionConvention<E> ceiling(final DivisionConvention<E> floorStrategy,
	                                                final Predicate<E> isZero,
	                                                final E one, final BinaryOperator<E> add,
	                                                final BinaryOperator<E> subtract)
	{
		return (dividend, divisor) ->
		{
			DivisionResult<E> floor = floorStrategy.divide(dividend, divisor);
			if (isZero.test(floor.remainder())) return floor;
			return DivisionResult.of(add.apply(floor.quotient(), one), subtract.apply(floor.remainder(), divisor));
		};
	}

	public static <E extends OrderedEuclideanDomain<E>> DivisionConvention<E> truncate()
	{
		return (dividend, divisor) ->
		{
			DivisionResult<E> floor = dividend.divideFloor(divisor);
			E q = floor.quotient();
			E r = floor.remainder();
			if (!r.isZero() && q.isNegative())
				return DivisionResult.of(q.add(q.one()), r.subtract(divisor));
			return floor;
		};
	}

	public static <E> DivisionConvention<E> truncate(final OrderedEuclideanDomainStructure<E> structure)
	{
		return truncate(structure::divideFloor, structure::isZero, structure::isNegative, structure.one(),
				structure::add,
				structure::subtract);
	}

	public static <E> DivisionConvention<E> truncate(final DivisionConvention<E> floorStrategy,
	                                                 final Predicate<E> isZero,
	                                                 final Predicate<E> isNegative, final E one,
	                                                 final BinaryOperator<E> add,
	                                                 final BinaryOperator<E> subtract)
	{
		return (dividend, divisor) ->
		{
			DivisionResult<E> floor = floorStrategy.divide(dividend, divisor);
			E q = floor.quotient();
			E r = floor.remainder();
			if (!isZero.test(r) && isNegative.test(q))
				return DivisionResult.of(add.apply(q, one), subtract.apply(r, divisor));
			return floor;
		};
	}

	private DivisionConventions()
	{
	}
}