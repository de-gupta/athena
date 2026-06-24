package de.gupta.commons.utility.math.algebra.element.module;

import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

final class ScalarQuotientableLawsTest implements ScalarQuotientableLaws<RationalNumber, RationalNumber>
{
	@Override
	@Provide
	public Arbitrary<RationalNumber> nonZeroElements()
	{
		return Combinators.combine(
				Arbitraries.longs().between(-1000, 1000).filter(n -> n != 0),
				Arbitraries.longs().between(1, 1000)
		).as(RationalNumberFactory::of);
	}

	@Override
	public RationalNumber one()
	{
		return RationalNumberFactory.one();
	}
}
