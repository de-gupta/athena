package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumbers;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

final class OrderedAdditiveGroupLawsTest
		implements OrderedAdditiveGroupLaws<IntegralNumbers>
{
	@Override
	@Provide
	public Arbitrary<IntegralNumbers> elements()
	{
		return Arbitraries.longs()
		                  .filter(value -> value != Long.MIN_VALUE)
		                  .map(IntegralNumberFactory::of);
	}
}