package de.gupta.commons.utility.math.algebra.structure.binary.action;

import de.gupta.commons.utility.math.algebra.structure.binary.SemigroupStructure;

public interface SemigroupAction<S, X> extends SemigroupStructure<S>
{
	X act(S actor, X point);
}