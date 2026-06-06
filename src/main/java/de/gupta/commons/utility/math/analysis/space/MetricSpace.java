package de.gupta.commons.utility.math.analysis.space;

@FunctionalInterface
public interface MetricSpace<X>
{
	double distance(X a, X b);
}