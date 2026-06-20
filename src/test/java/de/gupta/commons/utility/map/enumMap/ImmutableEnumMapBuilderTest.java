package de.gupta.commons.utility.map.enumMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ImmutableEnumMapBuilder")
final class ImmutableEnumMapBuilderTest
{
	private enum Color
	{RED, BLUE, GREEN}

	@Nested
	@DisplayName("when built empty")
	final class WhenBuiltEmpty
	{
		@Test
		@DisplayName("build returns a map with no entries")
		void buildReturnsAMapWithNoEntries()
		{
			final ImmutableEnumMap<Color, String> map = ImmutableEnumMapBuilder.<Color, String>create(Color.class)
			                                                                   .build();

			assertThat(map.values()).as("values of empty builder result").isEmpty();
		}
	}

	@Nested
	@DisplayName("when adding entries via add()")
	final class WhenAddingEntries
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("addStoresTheMappedValueCases")
		@DisplayName("add stores the mapped value")
		void addStoresTheMappedValue(final String as, final Color key, final String value)
		{
			final ImmutableEnumMap<Color, String> map = ImmutableEnumMapBuilder.<Color, String>create(Color.class)
			                                                                   .add(key, value)
			                                                                   .build();

			assertThat(map.find(key)).as(as).contains(value);
		}

		@Test
		@DisplayName("multiple add calls accumulate entries")
		void multipleAddCallsAccumulateEntries()
		{
			final ImmutableEnumMap<Color, String> map = ImmutableEnumMapBuilder.<Color, String>create(Color.class)
			                                                                   .add(Color.RED, "red")
			                                                                   .add(Color.BLUE, "blue")
			                                                                   .build();

			assertThat(map.find(Color.RED)).as("RED after two adds").contains("red");
			assertThat(map.find(Color.BLUE)).as("BLUE after two adds").contains("blue");
		}

		@Test
		@DisplayName("add overwrites an existing entry for the same key")
		void addOverwritesAnExistingEntryForTheSameKey()
		{
			final ImmutableEnumMap<Color, String> map = ImmutableEnumMapBuilder.<Color, String>create(Color.class)
			                                                                   .add(Color.RED, "first")
			                                                                   .add(Color.RED, "second")
			                                                                   .build();

			assertThat(map.find(Color.RED)).as("RED after overwrite").contains("second");
		}

		@Test
		@DisplayName("add is fluent — returns the same builder instance")
		void addIsFluentReturningTheSameBuilderInstance()
		{
			final ImmutableEnumMapBuilder<Color, String> builder = ImmutableEnumMapBuilder.create(Color.class);

			assertThat(builder.add(Color.RED, "red")).as("add() return value").isSameAs(builder);
		}

		private static Stream<Arguments> addStoresTheMappedValueCases()
		{
			return Stream.of(
					Arguments.of("RED stores red", Color.RED, "red"),
					Arguments.of("BLUE stores blue", Color.BLUE, "blue"),
					Arguments.of("GREEN stores green", Color.GREEN, "green")
			);
		}
	}

	@Nested
	@DisplayName("when adding entries via addAll()")
	final class WhenAddingAllEntries
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("addAllStoresAllMappedValuesCases")
		@DisplayName("addAll stores all mapped values")
		void addAllStoresAllMappedValues(final String as, final Color key, final String expectedValue)
		{
			final ImmutableEnumMap<Color, String> map = ImmutableEnumMapBuilder.<Color, String>create(Color.class)
			                                                                   .addAll(Map.of(Color.RED, "red",
					                                                                   Color.BLUE, "blue", Color.GREEN,
					                                                                   "green"))
			                                                                   .build();

			assertThat(map.find(key)).as(as).contains(expectedValue);
		}

		@Test
		@DisplayName("addAll followed by add overwrites the conflicting entry")
		void addAllFollowedByAddOverwritesTheConflictingEntry()
		{
			final ImmutableEnumMap<Color, String> map = ImmutableEnumMapBuilder.<Color, String>create(Color.class)
			                                                                   .addAll(Map.of(Color.RED, "from-map"))
			                                                                   .add(Color.RED, "from-add")
			                                                                   .build();

			assertThat(map.find(Color.RED)).as("RED after addAll then add").contains("from-add");
		}

		@Test
		@DisplayName("addAll is fluent — returns the same builder instance")
		void addAllIsFluentReturningTheSameBuilderInstance()
		{
			final ImmutableEnumMapBuilder<Color, String> builder = ImmutableEnumMapBuilder.create(Color.class);

			assertThat(builder.addAll(Map.of(Color.RED, "red"))).as("addAll() return value").isSameAs(builder);
		}

		private static Stream<Arguments> addAllStoresAllMappedValuesCases()
		{
			return Stream.of(
					Arguments.of("RED maps to red", Color.RED, "red"),
					Arguments.of("BLUE maps to blue", Color.BLUE, "blue"),
					Arguments.of("GREEN maps to green", Color.GREEN, "green")
			);
		}
	}

	@Nested
	@DisplayName("when null arguments are provided")
	final class WhenNullArgumentsAreProvided
	{
		@Test
		@DisplayName("create with null key type throws NullPointerException")
		void createWithNullKeyTypeThrowsNullPointerException()
		{
			assertThatThrownBy(() -> ImmutableEnumMapBuilder.create(null))
					.as("create(null)")
					.isInstanceOf(NullPointerException.class)
					.hasMessage("keyType may not be null");
		}

		@Test
		@DisplayName("add with null key throws NullPointerException")
		void addWithNullKeyThrowsNullPointerException()
		{
			final ImmutableEnumMapBuilder<Color, String> builder = ImmutableEnumMapBuilder.create(Color.class);

			assertThatThrownBy(() -> builder.add(null, "value"))
					.as("add(null, value)")
					.isInstanceOf(NullPointerException.class)
					.hasMessage("key may not be null");
		}

		@Test
		@DisplayName("addAll with null map throws NullPointerException")
		void addAllWithNullMapThrowsNullPointerException()
		{
			final ImmutableEnumMapBuilder<Color, String> builder = ImmutableEnumMapBuilder.create(Color.class);

			assertThatThrownBy(() -> builder.addAll(null))
					.as("addAll(null)")
					.isInstanceOf(NullPointerException.class)
					.hasMessage("values may not be null");
		}
	}
}