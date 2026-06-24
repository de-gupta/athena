package de.gupta.commons.utility.math.algebra.structure.affine;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegerEuclideanDomainStructure;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

final class AffineSpaceStructureLawsTest
		implements AffineSpaceStructureLaws<IntegralNumber, IntegralNumber>
{
	@Override
	@Provide
	public Arbitrary<IntegralNumber> elements()
	{
		return Arbitraries.longs().between(-1000, 1000).map(IntegralNumberFactory::of);
	}

	@Override
	public AffineSpaceStructure<IntegralNumber, IntegralNumber> structure()
	{
		return IntegerEuclideanDomainStructure.INSTANCE;
	}
}
