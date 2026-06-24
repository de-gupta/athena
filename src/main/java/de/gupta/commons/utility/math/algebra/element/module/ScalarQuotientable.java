package de.gupta.commons.utility.math.algebra.element.module;

public interface ScalarQuotientable<E extends ScalarQuotientable<E, S>, S>
{
	S ratio(final E denominator);
}