package de.gupta.commons.utility.math.interpolation;

import de.gupta.commons.utility.math.interpolation.data.InterpolationData;

@FunctionalInterface
public interface InterpolationMethod<X, Y>
{
	Interpolator<X, Y> fit(InterpolationData<X, Y> data);
}
