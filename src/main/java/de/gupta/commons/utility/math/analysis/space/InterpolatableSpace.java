package de.gupta.commons.utility.math.analysis.space;

import java.util.Comparator;

public interface InterpolatableSpace<X> extends MetricSpace<X>, Comparator<X>
{
	double parameter(X left, X right, X query);
}