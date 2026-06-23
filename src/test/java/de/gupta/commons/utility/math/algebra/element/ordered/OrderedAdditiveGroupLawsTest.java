package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

final class OrderedAdditiveGroupLawsTest
		implements OrderedAdditiveGroupLaws<IntegersAsEuclideanDomain>
{
	@Override
	@Provide
	public Arbitrary<IntegersAsEuclideanDomain> elements()
	{
		return Arbitraries.longs()
		                  .filter(value -> value != Long.MIN_VALUE)
		                  .map(IntegersAsEuclideanDomain::of);
	}
}