package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

final class ScalarExtensionLawsTest
		implements ScalarExtensionLaws<RationalNumber, RationalNumber, RationalNumber>
{
	@Override
	@Provide
	public Arbitrary<ScalarExtension<RationalNumber, RationalNumber, RationalNumber>> elements()
	{
		return Combinators.combine(nonZeroRationals(), nonZeroRationals())
		                  .as(ScalarExtensionFactory::of);
	}

	@Override
	@Provide
	public Arbitrary<RationalNumber> scalars()
	{
		return nonZeroRationals();
	}

	@Override
	public RationalNumber one()
	{
		return RationalNumberFactory.one();
	}

	private static Arbitrary<RationalNumber> nonZeroRationals()
	{
		return Combinators.combine(
				Arbitraries.longs().between(-50, 50).filter(n -> n != 0),
				Arbitraries.longs().between(1, 50)
		).as(RationalNumberFactory::of);
	}
}
