package de.gupta.commons.utility.properties;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LayeredProperties.Builder")
final class LayeredPropertiesBuilderTest
{
	private static final String CLASSPATH_RESOURCE = "de/gupta/commons/utility/properties/test-defaults.properties";

	private static Path writeProperties(final Path dir, final String fileName, final String content)
	{
		try
		{
			var file = dir.resolve(fileName);
			Files.writeString(file, content);
			return file;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Nested
	@DisplayName("when no sources are added")
	final class WhenNoSourcesAreAdded
	{
		@Test
		@DisplayName("get returns empty Optional for any key")
		void getReturnsEmptyOptionalForAnyKey()
		{
			var props = LayeredProperties.builder().build();

			assertThat(props.get("app.name"))
					.as("empty builder should yield no value for any key")
					.isEmpty();
		}

		@Test
		@DisplayName("require throws NoSuchElementException for any key")
		void requireThrowsNoSuchElementExceptionForAnyKey()
		{
			var props = LayeredProperties.builder().build();

			assertThatThrownBy(() -> props.require("app.name"))
					.as("empty builder should throw when key is required")
					.isInstanceOf(NoSuchElementException.class);
		}
	}

	@Nested
	@DisplayName("when a single classpath resource is added")
	final class WhenASingleClasspathResourceIsAdded
	{
		@Test
		@DisplayName("returns values present in the resource")
		void returnsValuesPresentInTheResource()
		{
			var props = LayeredProperties.builder()
			                             .withClasspathResource(CLASSPATH_RESOURCE)
			                             .build();

			assertThat(props.get("app.name"))
					.as("key present in classpath resource should be returned")
					.isEqualTo(Optional.of("test-app"));
			assertThat(props.get("app.version"))
					.as("second key from resource should also be returned")
					.isEqualTo(Optional.of("1.0"));
		}

		@Test
		@DisplayName("silently skips an absent classpath resource")
		void silentlySkipsAnAbsentClasspathResource()
		{
			var props = LayeredProperties.builder()
			                             .withClasspathResource("nonexistent/resource.properties")
			                             .build();

			assertThat(props.get("any.key"))
					.as("absent classpath resource should produce an empty result without throwing")
					.isEmpty();
		}
	}

	@Nested
	@DisplayName("when a single file is added")
	final class WhenASingleFileIsAdded
	{
		@Test
		@DisplayName("returns values present in the file")
		void returnsValuesPresentInTheFile(@TempDir final Path tempDir)
		{
			var file = writeProperties(tempDir, "config.properties", "colour=red\nsize=large");

			var props = LayeredProperties.builder().withFile(file).build();

			assertThat(props.get("colour"))
					.as("key present in file should be returned")
					.isEqualTo(Optional.of("red"));
			assertThat(props.get("size"))
					.as("second key from file should also be returned")
					.isEqualTo(Optional.of("large"));
		}

		@Test
		@DisplayName("silently skips an absent file path")
		void silentlySkipsAnAbsentFilePath(@TempDir final Path tempDir)
		{
			var absent = tempDir.resolve("does-not-exist.properties");

			var props = LayeredProperties.builder().withFile(absent).build();

			assertThat(props.get("any.key"))
					.as("absent file path should produce an empty result without throwing")
					.isEmpty();
		}
	}

	@Nested
	@DisplayName("when multiple sources are added")
	final class WhenMultipleSourcesAreAdded
	{
		@Test
		@DisplayName("later source wins on key collision")
		void laterSourceWinsOnKeyCollision(@TempDir final Path tempDir)
		{
			var first = writeProperties(tempDir, "first.properties", "colour=red");
			var second = writeProperties(tempDir, "second.properties", "colour=blue");

			var props = LayeredProperties.builder()
			                             .withFile(first)
			                             .withFile(second)
			                             .build();

			assertThat(props.get("colour"))
					.as("second source should win over first for the same key")
					.isEqualTo(Optional.of("blue"));
		}

		@Test
		@DisplayName("disjoint keys from all sources are present in the result")
		void disjointKeysFromAllSourcesArePresentInTheResult(@TempDir final Path tempDir)
		{
			var first = writeProperties(tempDir, "first.properties", "key.a=alpha");
			var second = writeProperties(tempDir, "second.properties", "key.b=beta");

			var props = LayeredProperties.builder()
			                             .withFile(first)
			                             .withFile(second)
			                             .build();

			assertThat(props.get("key.a"))
					.as("key from first source should be present")
					.isEqualTo(Optional.of("alpha"));
			assertThat(props.get("key.b"))
					.as("key from second source should be present")
					.isEqualTo(Optional.of("beta"));
		}

		@Test
		@DisplayName("file source wins over earlier classpath resource for same key")
		void fileSourceWinsOverEarlierClasspathResourceForSameKey(@TempDir final Path tempDir)
		{
			var override = writeProperties(tempDir, "override.properties", "app.name=overridden");

			var props = LayeredProperties.builder()
			                             .withClasspathResource(CLASSPATH_RESOURCE)
			                             .withFile(override)
			                             .build();

			assertThat(props.get("app.name"))
					.as("file source added after classpath resource should win on collision")
					.isEqualTo(Optional.of("overridden"));
			assertThat(props.get("app.version"))
					.as("key present only in classpath resource should still be accessible")
					.isEqualTo(Optional.of("1.0"));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("threeSourceMergeCases")
		@DisplayName("three sources are merged in priority order")
		void threeSourcesAreMergedInPriorityOrder(final String as, final ThreeSourceCase tc)
		{
			var props = LayeredProperties.builder()
			                             .withFile(tc.first())
			                             .withFile(tc.second())
			                             .withFile(tc.third())
			                             .build();

			assertThat(props.get("key"))
					.as(as)
					.isEqualTo(Optional.of(tc.expectedValue()));
		}

		private static Stream<Arguments> threeSourceMergeCases(@TempDir final Path tempDir)
		{
			return Stream.of(
					ThreeSourceCase.of("third of three wins on collision",
							writeProperties(tempDir, "s1.properties", "key=first"),
							writeProperties(tempDir, "s2.properties", "key=second"),
							writeProperties(tempDir, "s3.properties", "key=third"),
							"third"),
					ThreeSourceCase.of("second wins when third has no entry for key",
							writeProperties(tempDir, "t1.properties", "key=first"),
							writeProperties(tempDir, "t2.properties", "key=second"),
							writeProperties(tempDir, "t3.properties", "other=unrelated"),
							"second")
			).map(tc -> Arguments.of(tc.as(), tc));
		}

		private record ThreeSourceCase(String as, Path first, Path second, Path third, String expectedValue)
		{
			private static ThreeSourceCase of(final String as, final Path first, final Path second,
			                                  final Path third, final String expectedValue)
			{
				return new ThreeSourceCase(as, first, second, third, expectedValue);
			}
		}
	}

	@Nested
	@DisplayName("when system properties are added")
	final class WhenSystemPropertiesAreAdded
	{
		private static final String SYS_KEY = "de.gupta.athena.test.layered.sysprop";
		@TempDir
		Path tempDir;

		@BeforeEach
		void setSystemProperty()
		{
			System.setProperty(SYS_KEY, "system-value");
		}

		@AfterEach
		void clearSystemProperty()
		{
			System.clearProperty(SYS_KEY);
		}

		@Test
		@DisplayName("system property wins over file source for the same key")
		void systemPropertyWinsOverFileSourceForTheSameKey()
		{
			var file = writeProperties(tempDir, "config.properties", SYS_KEY + "=file-value");

			var props = LayeredProperties.builder()
			                             .withFile(file)
			                             .withSystemProperties()
			                             .build();

			assertThat(props.get(SYS_KEY))
					.as("system property should win over file source for the same key")
					.isEqualTo(Optional.of("system-value"));
		}

		@Test
		@DisplayName("system property wins regardless of call order in the builder")
		void systemPropertyWinsRegardlessOfCallOrderInTheBuilder()
		{
			var file = writeProperties(tempDir, "config.properties", SYS_KEY + "=file-value");

			var props = LayeredProperties.builder()
			                             .withSystemProperties()
			                             .withFile(file)
			                             .build();

			assertThat(props.get(SYS_KEY))
					.as("system property should win even when withSystemProperties was called before withFile")
					.isEqualTo(Optional.of("system-value"));
		}

		@Test
		@DisplayName("key absent from system properties falls back to file source")
		void keyAbsentFromSystemPropertiesFallsBackToFileSource()
		{
			var file = writeProperties(tempDir, "config.properties", "file.only.key=from-file");

			var props = LayeredProperties.builder()
			                             .withFile(file)
			                             .withSystemProperties()
			                             .build();

			assertThat(props.get("file.only.key"))
					.as("key present only in file source should still be accessible when system properties are also added")
					.isEqualTo(Optional.of("from-file"));
		}

		@Test
		@DisplayName("multiple withSystemProperties calls are idempotent")
		void multipleWithSystemPropertiesCallsAreIdempotent()
		{
			var file = writeProperties(tempDir, "config.properties", SYS_KEY + "=file-value");

			var props = LayeredProperties.builder()
			                             .withFile(file)
			                             .withSystemProperties()
			                             .withSystemProperties()
			                             .build();

			assertThat(props.get(SYS_KEY))
					.as("multiple withSystemProperties calls should not corrupt state — system value still wins")
					.isEqualTo(Optional.of("system-value"));
		}
	}

	@Nested
	@DisplayName("when sources are loaded at build time")
	final class WhenSourcesAreLoadedAtBuildTime
	{
		@Test
		@DisplayName("each build call produces an independent snapshot of the current source state")
		void eachBuildCallProducesAnIndependentSnapshotOfTheCurrentSourceState(@TempDir final Path tempDir)
		{
			var file = writeProperties(tempDir, "config.properties", "key=first");
			var builder = LayeredProperties.builder().withFile(file);

			var firstSnapshot = builder.build();
			writeProperties(tempDir, "config.properties", "key=second");
			var secondSnapshot = builder.build();

			assertThat(firstSnapshot.get("key"))
					.as("first build should reflect the file content at first build time")
					.isEqualTo(Optional.of("first"));
			assertThat(secondSnapshot.get("key"))
					.as("second build should reflect the file content at second build time")
					.isEqualTo(Optional.of("second"));
		}
	}

	// --- Helpers ---

	@Nested
	@DisplayName("with null and blank arguments")
	final class WithNullAndBlankArguments
	{
		@Test
		@DisplayName("withClasspathResource throws NullPointerException for null resourcePath")
		void withClasspathResourceThrowsNullPointerExceptionForNullResourcePath()
		{
			assertThatThrownBy(() -> LayeredProperties.builder().withClasspathResource(null))
					.as("null resourcePath should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("withClasspathResource throws IllegalArgumentException for blank resourcePath")
		void withClasspathResourceThrowsIllegalArgumentExceptionForBlankResourcePath()
		{
			assertThatThrownBy(() -> LayeredProperties.builder().withClasspathResource("   "))
					.as("blank resourcePath should throw IllegalArgumentException")
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("withFile throws NullPointerException for null path")
		void withFileThrowsNullPointerExceptionForNullPath()
		{
			assertThatThrownBy(() -> LayeredProperties.builder().withFile(null))
					.as("null path should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}
}