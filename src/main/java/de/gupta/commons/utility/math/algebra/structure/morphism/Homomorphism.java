package de.gupta.commons.utility.math.algebra.structure.morphism;

import java.util.function.Function;

@FunctionalInterface
public interface Homomorphism<S, T> extends Function<S, T>
{
	@Override
	T apply(S source);
}