package de.gupta.commons.utility.math.algebra.element.module;

@FunctionalInterface
public interface ScalarQuotientable<E, S>
{
	S ratio(final E denominator);
}