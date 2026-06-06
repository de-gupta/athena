package de.gupta.commons.utility.math.analysis.space;

@FunctionalInterface
public interface BlendableSpace<Y>
{
	Y blend(Y a, Y b, double lambda);
}