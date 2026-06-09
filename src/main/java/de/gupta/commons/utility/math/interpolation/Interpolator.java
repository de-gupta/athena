package de.gupta.commons.utility.math.interpolation;

@FunctionalInterface
public interface Interpolator<X, Y>
{
	Y interpolate(X query);
}
