package de.gupta.commons.utility.math.algebra.element.algebra;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

final class ScalarExtensionLawsTest
		implements ScalarExtensionLaws<IntegralNumber, RationalNumber, RationalNumber>
{
	private static final ScalarExtension<IntegralNumber, RationalNumber> EXTENSION =
			n -> RationalNumberFactory.of(n, n.one());

	@Override
	@Provide
	public Arbitrary<IntegralNumber> elements()
	{
		return Arbitraries.longs().between(-1000, 1000).map(IntegralNumberFactory::of);
	}

	@Override
	public ScalarExtension<IntegralNumber, RationalNumber> extension()
	{
		return EXTENSION;
	}
}
