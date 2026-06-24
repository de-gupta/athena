package de.gupta.commons.utility.math.algebra.element.affine;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

final class AffineSpaceLawsTest implements AffineSpaceLaws<IntegralNumber, IntegralNumber>
{
	@Override
	@Provide
	public Arbitrary<IntegralNumber> elements()
	{
		return Arbitraries.longs().between(-1000, 1000).map(IntegralNumberFactory::of);
	}
}
