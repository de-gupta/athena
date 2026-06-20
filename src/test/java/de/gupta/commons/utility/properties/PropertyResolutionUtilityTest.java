package de.gupta.commons.utility.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("Property Resolution Utility")
final class PropertyResolutionUtilityTest
{
	private record OverwriteOrSetKeyTestCase(SequencedCollection<String> values, String expectedValue,
	                                         String description)
	{
	}

	@Nested
	@DisplayName("overwriteOrSetKey")
	class OverwriteOrSetKeyTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("overwriteOrSetKeyValuesProvider")
		@DisplayName("should use the first non-blank override value")
		void shouldUseFirstNonBlankOverrideValue(final SequencedCollection<String> values, final String expectedValue,
		                                         final String description)
		{
			Properties properties = new Properties();
			properties.setProperty("mode", "default");

			Properties result = PropertyResolutionUtility.overwriteOrSetKey(properties, "mode", values);

			assertThat(result.getProperty("mode"))
					.as(description)
					.isEqualTo(expectedValue);
		}

		@Test
		@DisplayName("should leave the original value when all override candidates are blank")
		void shouldLeaveOriginalValueWhenAllOverrideCandidatesAreBlank()
		{
			Properties properties = new Properties();
			properties.setProperty("mode", "default");

			Properties result = PropertyResolutionUtility.overwriteOrSetKey(properties, "mode",
					new ArrayList<>(List.of("", " ", "\t")));

			assertSoftly(softly ->
			{
				softly.assertThat(result.getProperty("mode"))
				      .as("The existing property should be preserved when no non-blank override exists")
				      .isEqualTo("default");
				softly.assertThat(result)
				      .as("A new Properties instance should still be returned")
				      .isNotSameAs(properties);
			});
		}

		@Test
		@DisplayName("should preserve unrelated properties and not mutate the original input")
		void shouldPreserveUnrelatedPropertiesAndNotMutateTheOriginalInput()
		{
			Properties properties = new Properties();
			properties.setProperty("mode", "default");
			properties.setProperty("environment", "dev");

			Properties result = PropertyResolutionUtility.overwriteOrSetKey(properties, "mode",
					new ArrayList<>(List.of("override")));

			assertSoftly(softly ->
			{
				softly.assertThat(result.getProperty("mode"))
				      .as("The requested key should be overwritten in the returned Properties")
				      .isEqualTo("override");
				softly.assertThat(result.getProperty("environment"))
				      .as("Unrelated properties should be copied across")
				      .isEqualTo("dev");
				softly.assertThat(properties.getProperty("mode"))
				      .as("The original Properties instance should remain unchanged")
				      .isEqualTo("default");
			});
		}

		@Test
		@DisplayName("should copy resolved default properties and detach from later source mutations")
		void shouldCopyResolvedDefaultPropertiesAndDetachFromLaterSourceMutations()
		{
			Properties defaults = new Properties();
			defaults.setProperty("mode", "default-from-defaults");
			defaults.setProperty("environment", "dev");

			Properties properties = new Properties(defaults);

			Properties result = PropertyResolutionUtility.overwriteOrSetKey(properties, "mode",
					new ArrayList<>(List.of("override")));

			defaults.setProperty("environment", "prod");
			properties.setProperty("mode", "changed-after-copy");

			assertSoftly(softly ->
			{
				softly.assertThat(result.getProperty("mode"))
				      .as("The returned Properties should contain the overridden value")
				      .isEqualTo("override");
				softly.assertThat(result.getProperty("environment"))
				      .as("Resolved default-backed properties should be copied into the result")
				      .isEqualTo("dev");
				softly.assertThat(result.stringPropertyNames())
				      .as("Copied properties should live directly in the result rather than only through defaults")
				      .contains("mode", "environment");
			});
		}

		@Test
		@DisplayName("should set the key when it does not already exist and a non-blank value is resolved")
		void shouldSetTheKeyWhenItDoesNotAlreadyExistAndANonBlankValueIsResolved()
		{
			Properties properties = new Properties();

			Properties result = PropertyResolutionUtility.overwriteOrSetKey(properties, "mode",
					new ArrayList<>(List.of("override")));

			assertSoftly(softly ->
			{
				softly.assertThat(result.getProperty("mode"))
				      .as("An absent key should be added when a non-blank override value is available")
				      .isEqualTo("override");
				softly.assertThat(properties.containsKey("mode"))
				      .as("The source Properties instance should remain unchanged")
				      .isFalse();
			});
		}

		@Test
		@DisplayName("should leave an absent key absent when no non-blank value is resolved")
		void shouldLeaveAnAbsentKeyAbsentWhenNoNonBlankValueIsResolved()
		{
			Properties properties = new Properties();

			Properties result = PropertyResolutionUtility.overwriteOrSetKey(properties, "mode",
					new ArrayList<>(List.of("", " ", "\t")));

			assertThat(result.containsKey("mode"))
					.as("An absent key should remain absent when no non-blank override value is available")
					.isFalse();
		}

		@Test
		@DisplayName("should throw NullPointerException when values collection is null")
		void shouldThrowNullPointerExceptionWhenValuesCollectionIsNull()
		{
			Properties properties = new Properties();

			assertThatThrownBy(() -> PropertyResolutionUtility.overwriteOrSetKey(properties, "mode", null))
					.as("A null values collection should follow StringSearchUtility.firstNonBlank semantics")
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("should throw NullPointerException when source properties are null")
		void shouldThrowNullPointerExceptionWhenSourcePropertiesAreNull()
		{
			assertThatThrownBy(() -> PropertyResolutionUtility.overwriteOrSetKey(null, "mode",
					new ArrayList<>(List.of("override"))))
					.as("A null source Properties instance cannot be copied")
					.isInstanceOf(NullPointerException.class);
		}

		private static Stream<Arguments> overwriteOrSetKeyValuesProvider()
		{
			return Stream.of(
					new OverwriteOrSetKeyTestCase(new ArrayList<>(List.of("override")), "override",
							"A single non-blank override should replace the existing value"),
					new OverwriteOrSetKeyTestCase(new ArrayList<>(List.of("", "  ", "override", "later")), "override",
							"Blank candidates should be skipped until the first non-blank value is found"),
					new OverwriteOrSetKeyTestCase(new ArrayList<>(List.of("default", "override")), "default",
							"The first non-blank candidate should win even when later candidates also contain text"),
					new OverwriteOrSetKeyTestCase(new ArrayList<>(Arrays.asList(null, "\t", "override")), "override",
							"Null and blank candidates should be ignored before applying the first non-blank value")
			).map(testCase -> Arguments.of(testCase.values(), testCase.expectedValue(), testCase.description()));
		}
	}
}