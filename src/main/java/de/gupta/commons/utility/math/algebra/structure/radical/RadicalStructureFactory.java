package de.gupta.commons.utility.math.algebra.structure.radical;

import de.gupta.commons.utility.math.algebra.element.radical.Estimator;
import de.gupta.commons.utility.math.algebra.element.radical.EstimatorBasedRoot;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;
import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;

public final class RadicalStructureFactory
{
	public static <E extends Ring<E>> RadicalStructure<E> using(final Estimator<E> estimator)
	{
		return (element, degree, whenToStop, rounding) -> EstimatorBasedRoot.compute(element, degree, element.zero(),
				element.one(), estimator, whenToStop, rounding);
	}

	public static <E> RadicalStructure<E> using(final Estimator<E> estimator, final RingStructure<E> ringStructure)
	{
		return ((element, degree, whenToStop, rounding) -> EstimatorBasedRoot.compute(element, degree,
				ringStructure.zero(),
				ringStructure.one(), estimator, whenToStop, rounding));
	}
}