package de.gupta.commons.utility.math.analysis.space;

import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;

import java.util.Objects;

public interface RealLinearSpace<Y> extends AdditiveAbelianGroupStructure<Y>, BlendableSpace<Y>
{
	Y scale(double scalar, Y vector);

	@Override
	default Y blend(final Y a, final Y b, final double lambda)
	{
		Objects.requireNonNull(a, "a");
		Objects.requireNonNull(b, "b");
		return add(scale(1.0 - lambda, a), scale(lambda, b));
	}
}