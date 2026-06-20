package de.gupta.commons.utility.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LayeredProperties#get(String)")
final class LayeredPropertiesGetTest
{
	private static final String CLASSPATH_RESOURCE = "de/gupta/commons/utility/properties/test-defaults.properties";

	private final LayeredProperties properties = LayeredProperties.builder()
	                                                              .withClasspathResource(CLASSPATH_RESOURCE)
	                                                              .build();

	@Nested
	@DisplayName("when key is present")
	final class WhenKeyIsPresent
	{
		@Test
		@DisplayName("returns an Optional containing the resolved value")
		void returnsAnOptionalContainingTheResolvedValue()
		{
			var result = properties.get("app.name");

			assertThat(result)
					.as("present key should return Optional containing its value")
					.isEqualTo(Optional.of("test-app"));
		}

		@Test
		@DisplayName("returns the value for each key independently")
		void returnsTheValueForEachKeyIndependently()
		{
			assertThat(properties.get("app.version"))
					.as("app.version should resolve independently")
					.isEqualTo(Optional.of("1.0"));
			assertThat(properties.get("database.host"))
					.as("database.host should resolve independently")
					.isEqualTo(Optional.of("localhost"));
		}
	}

	@Nested
	@DisplayName("when key is absent")
	final class WhenKeyIsAbsent
	{
		@Test
		@DisplayName("returns an empty Optional")
		void returnsAnEmptyOptional()
		{
			var result = properties.get("nonexistent.key");

			assertThat(result)
					.as("absent key should return an empty Optional")
					.isEmpty();
		}
	}

	@Nested
	@DisplayName("with null arguments")
	final class WithNullArguments
	{
		@Test
		@DisplayName("throws NullPointerException when key is null")
		void throwsNullPointerExceptionWhenKeyIsNull()
		{
			assertThatThrownBy(() -> properties.get(null))
					.as("null key should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}
}