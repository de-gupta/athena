package de.gupta.commons.utility.math.algebra.structure.algebra;

import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.rationals.RationalNumberStructureFactory;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

final class AlgebraStructureLawsTest
		implements AlgebraStructureLaws<RationalNumber, RationalNumber>
{
	@Override
	@Provide
	public Arbitrary<RationalNumber> scalars()
	{
		return nonZeroRationals();
	}

	@Override
	@Provide
	public Arbitrary<RationalNumber> elements()
	{
		return nonZeroRationals();
	}

	@Override
	public AlgebraStructure<RationalNumber, RationalNumber> structure()
	{
		return RationalNumberStructureFactory.instance();
	}

	private static Arbitrary<RationalNumber> nonZeroRationals()
	{
		return Combinators.combine(
				Arbitraries.longs().between(-100, 100).filter(n -> n != 0),
				Arbitraries.longs().between(1, 100)
		).as(RationalNumberFactory::of);
	}
}