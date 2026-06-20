package de.gupta.commons.utility.map.enumMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ImmutableEnumMap")
final class ImmutableEnumMapTest
{
	private enum Color
	{RED, BLUE, GREEN}

	@Nested
	@DisplayName("when constructed via of()")
	final class WhenConstructedViaOf
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("findReturnsTheMappedValueCases")
		@DisplayName("find returns the mapped value")
		void findReturnsTheMappedValue(final String as, final Color key, final String expectedValue)
		{
			final ImmutableEnumMap<Color, String> map = ImmutableEnumMap.of(Color.class,
					Map.of(Color.RED, "red", Color.BLUE, "blue", Color.GREEN, "green"));

			assertThat(map.find(key)).as(as).contains(expectedValue);
		}

		@Test
		@DisplayName("values map is unmodifiable")
		void valuesMapIsUnmodifiable()
		{
			final ImmutableEnumMap<Color, String> map = ImmutableEnumMap.of(Color.class, Map.of(Color.RED, "red"));

			assertThatThrownBy(() -> map.values().put(Color.BLUE, "blue"))
					.as("values() map should reject mutations")
					.isInstanceOf(UnsupportedOperationException.class);
		}

		@Test
		@DisplayName("original map mutation does not affect the immutable copy")
		void originalMapMutationDoesNotAffectImmutableCopy()
		{
			final Map<Color, String> mutable = new HashMap<>();
			mutable.put(Color.RED, "original");
			final ImmutableEnumMap<Color, String> map = ImmutableEnumMap.of(Color.class, mutable);

			mutable.put(Color.RED, "changed");

			assertThat(map.find(Color.RED)).as("value after source map mutation").contains("original");
		}

		private static Stream<Arguments> findReturnsTheMappedValueCases()
		{
			return Stream.of(
					Arguments.of("RED maps to red", Color.RED, "red"),
					Arguments.of("BLUE maps to blue", Color.BLUE, "blue"),
					Arguments.of("GREEN maps to green", Color.GREEN, "green")
			);
		}
	}
}