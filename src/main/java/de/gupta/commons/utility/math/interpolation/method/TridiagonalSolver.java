package de.gupta.commons.utility.math.interpolation.method;

import de.gupta.commons.utility.math.algebra.structure.module.VectorSpaceStructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleFunction;

final class TridiagonalSolver
{
	static <Y, F> List<Y> solve(
			final double[] subDiagonal,
			final double[] mainDiagonal,
			final double[] superDiagonal,
			final List<Y> rightHandSide,
			final VectorSpaceStructure<Y, F> space,
			final DoubleFunction<F> scalarOf)
	{
		int size = mainDiagonal.length;
		double[] main = mainDiagonal.clone();
		List<Y> rhs = new ArrayList<>(rightHandSide);

		for (int i = 1; i < size; i++)
		{
			double factor = subDiagonal[i - 1] / main[i - 1];
			main[i] -= factor * superDiagonal[i - 1];
			rhs.set(i, space.subtract(rhs.get(i), space.scale(scalarOf.apply(factor), rhs.get(i - 1))));
		}

		List<Y> solution = new ArrayList<>(Collections.nCopies(size, null));
		solution.set(size - 1, space.scale(scalarOf.apply(1.0 / main[size - 1]), rhs.get(size - 1)));
		for (int i = size - 2; i >= 0; i--)
		{
			solution.set(i, space.scale(
					scalarOf.apply(1.0 / main[i]),
					space.subtract(rhs.get(i), space.scale(scalarOf.apply(superDiagonal[i]), solution.get(i + 1)))
			));
		}

		return solution;
	}

	private TridiagonalSolver()
	{
	}
}