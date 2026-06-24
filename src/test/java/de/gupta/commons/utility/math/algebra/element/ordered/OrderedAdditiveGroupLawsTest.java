package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

final class OrderedAdditiveGroupLawsTest
		implements OrderedAdditiveGroupLaws<IntegralNumber>
{
	@Override
	@Provide
	public Arbitrary<IntegralNumber> elements()
	{
		return Arbitraries.longs()
		                  .filter(value -> value != Long.MIN_VALUE)
		                  .map(IntegralNumberFactory::of);
	}
}