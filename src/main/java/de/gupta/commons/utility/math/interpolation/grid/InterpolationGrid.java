package de.gupta.commons.utility.math.interpolation.grid;

import de.gupta.commons.utility.math.interpolation.InterpolationMethod;
import de.gupta.commons.utility.math.interpolation.Interpolator;
import de.gupta.commons.utility.math.interpolation.data.GridSample;
import de.gupta.commons.utility.math.interpolation.data.InterpolationData;
import de.gupta.commons.utility.math.interpolation.data.Sample;

import java.util.*;
import java.util.stream.IntStream;

public final class InterpolationGrid<X1, X2, Y>
{
	private final List<X1> dim1Keys;
	private final List<Interpolator<X2, Y>> dim2Interpolators;
	private final InterpolationMethod<X1, Y> dim1Method;
	private final Comparator<X1> dim1Order;

	public static <X1, X2, Y> Builder<X1, X2, Y> builder()
	{
		return new Builder<>();
	}

	public Y interpolate(final X1 x1, final X2 x2)
	{
		List<Sample<X1, Y>> virtualSamples = IntStream.range(0, dim1Keys.size())
		                                              .mapToObj(i -> new Sample<>(dim1Keys.get(i),
															  dim2Interpolators.get(i).interpolate(x2)))
		                                              .toList();
		return dim1Method.fit(InterpolationData.of(virtualSamples, dim1Order)).interpolate(x1);
	}

	private InterpolationGrid(
			final List<X1> dim1Keys,
			final List<Interpolator<X2, Y>> dim2Interpolators,
			final InterpolationMethod<X1, Y> dim1Method,
			final Comparator<X1> dim1Order)
	{
		this.dim1Keys = dim1Keys;
		this.dim2Interpolators = dim2Interpolators;
		this.dim1Method = dim1Method;
		this.dim1Order = dim1Order;
	}

	public static final class Builder<X1, X2, Y>
	{
		private Iterable<GridSample<X1, X2, Y>> data;
		private Comparator<X1> dim1Order;
		private InterpolationMethod<X1, Y> dim1Method;
		private Comparator<X2> dim2Order;
		private InterpolationMethod<X2, Y> dim2Method;

		public Builder<X1, X2, Y> withData(final Iterable<GridSample<X1, X2, Y>> data)
		{
			this.data = Objects.requireNonNull(data, "data");
			return this;
		}

		public Builder<X1, X2, Y> dimension1(
				final Comparator<X1> order,
				final InterpolationMethod<X1, Y> method)
		{
			this.dim1Order = Objects.requireNonNull(order, "order");
			this.dim1Method = Objects.requireNonNull(method, "method");
			return this;
		}

		public Builder<X1, X2, Y> dimension2(
				final Comparator<X2> order,
				final InterpolationMethod<X2, Y> method)
		{
			this.dim2Order = Objects.requireNonNull(order, "order");
			this.dim2Method = Objects.requireNonNull(method, "method");
			return this;
		}

		public InterpolationGrid<X1, X2, Y> build()
		{
			if (data == null) throw new IllegalArgumentException("data must be provided via withData()");
			if (dim1Order == null || dim1Method == null)
				throw new IllegalArgumentException("dimension1 must be configured");
			if (dim2Order == null || dim2Method == null)
				throw new IllegalArgumentException("dimension2 must be configured");

			TreeMap<X1, List<Sample<X2, Y>>> slices = new TreeMap<>(dim1Order);
			for (GridSample<X1, X2, Y> gridSample : data)
			{
				slices.computeIfAbsent(gridSample.x1(), _ -> new ArrayList<>())
				      .add(new Sample<>(gridSample.x2(), gridSample.y()));
			}

			if (slices.size() < 2)
			{
				throw new IllegalArgumentException(
						"interpolation grid requires at least two distinct X1 slices");
			}

			List<X1> dim1Keys = new ArrayList<>(slices.keySet());
			List<Interpolator<X2, Y>> dim2Interpolators = new ArrayList<>(slices.size());
			for (List<Sample<X2, Y>> sliceSamples : slices.values())
			{
				dim2Interpolators.add(dim2Method.fit(InterpolationData.of(sliceSamples, dim2Order)));
			}

			return new InterpolationGrid<>(dim1Keys, dim2Interpolators, dim1Method, dim1Order);
		}

		private Builder()
		{
		}
	}
}