package de.gupta.commons.utility.math.algebra.structure.ring.standard.rationals;

import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
import de.gupta.commons.utility.math.ordering.OrderRelation;

enum RationalNumberStructureImpl implements RationalNumberStructure
{
	INSTANCE;

	@Override
	public RationalNumber zero()
	{
		return RationalNumberFactory.zero();
	}

	@Override
	public RationalNumber one()
	{
		return RationalNumberFactory.one();
	}

	@Override
	public RationalNumber multiply(final RationalNumber left, final RationalNumber right)
	{
		return left.multiply(right);
	}

	@Override
	public RationalNumber add(final RationalNumber left, final RationalNumber right)
	{
		return left.add(right);
	}

	@Override
	public RationalNumber negate(final RationalNumber element)
	{
		return element.negate();
	}

	@Override
	public RationalNumber multiplicativeInverse(final RationalNumber element)
	{
		return element.reciprocal();
	}

	@Override
	public OrderRelation compare(final RationalNumber left, final RationalNumber right)
	{
		return left.compare(right);
	}
}