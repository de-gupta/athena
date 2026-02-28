package de.gupta.commons.utility.math.prefix;

import java.util.Optional;

public interface PrefixWithFactor
{
	double effectiveFactor();

	PrefixWithFactor multiply(PrefixWithFactor other);

	PrefixWithFactor power(int power);

	Optional<PrefixWithFactor> normalize();

	Optional<PrefixWithFactor> humanReadable();

	Optional<PrefixWithFactor> cleanIfUnity();

	boolean isUnity();

	default boolean isPrefixNotUnity()
	{
		return !isPrefixUnity();
	}

	boolean isPrefixUnity();

	Prefix prefix();

	double factor();

}