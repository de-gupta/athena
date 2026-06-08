package de.gupta.commons.utility.math.analysis.space;

import java.util.Objects;

public interface DifferentiableSpace<X> extends InterpolatableSpace<X>
{
	default double span(final X left, final X right)
	{
		Objects.requireNonNull(left, "left");
		Objects.requireNonNull(right, "right");
		return distance(left, right);
	}
}
