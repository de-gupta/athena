package de.gupta.commons.utility.math.algebra.element.binary.action;

import de.gupta.commons.utility.math.algebra.element.binary.Semigroup;

public interface SemigroupActedUpon<S extends Semigroup<S>, X extends SemigroupActedUpon<S, X>>
{
	X act(S actor);
}