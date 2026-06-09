package de.gupta.commons.utility.math.interpolation.data;

public record Sample<X, Y>(X x, Y y)
{
	public static <X, Y> Sample<X, Y> of(final X x, final Y y)
	{
		return new Sample<>(x, y);
	}
}
