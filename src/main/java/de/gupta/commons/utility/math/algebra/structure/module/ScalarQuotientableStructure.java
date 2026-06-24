package de.gupta.commons.utility.math.algebra.structure.module;

@FunctionalInterface
public interface ScalarQuotientableStructure<E, S>
{
	S ratio(E numerator, E denominator);
}