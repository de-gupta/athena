package de.gupta.commons.utility.math.algebra.structure.module;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

final class ScalarQuotientableStructureLawsTest
		implements ScalarQuotientableStructureLaws<IntegralNumber, RationalNumber>
{
	@Override
	@Provide
	public Arbitrary<IntegralNumber> nonZeroElements()
	{
		return Arbitraries.longs().between(-1000, 1000).filter(n -> n != 0).map(IntegralNumberFactory::of);
	}

	@Override
	public ScalarQuotientableStructure<IntegralNumber, RationalNumber> structure()
	{
		return RationalNumberFactory::of;
	}

	@Override
	public RationalNumber one()
	{
		return RationalNumberFactory.one();
	}
}
