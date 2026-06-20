package de.gupta.commons.utility.properties;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PropertySource#load")
final class PropertySourceLoadTest
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
	@DisplayName("ClasspathResource — when resource exists on the classpath")
	final class WhenClasspathResourceExistsOnTheClasspath
	{
		@Test
		@DisplayName("returns all key-value pairs from the resource")
		void returnsAllKeyValuePairsFromTheResource()
		{
			var result = new PropertySource.ClasspathResource(CLASSPATH_RESOURCE).load();

			assertThat(result)
					.as("all entries from test-defaults.properties should be present")
					.containsEntry("app.name", "test-app")
					.containsEntry("app.version", "1.0")
					.containsEntry("database.host", "localhost");
		}

		@Test
		@DisplayName("returns an unmodifiable map")
		void returnsAnUnmodifiableMap()
		{
			var result = new PropertySource.ClasspathResource(CLASSPATH_RESOURCE).load();

			assertThatThrownBy(() -> result.put("extra", "value"))
					.as("result map should be unmodifiable")
					.isInstanceOf(UnsupportedOperationException.class);
		}
	}

	@Nested
	@DisplayName("ClasspathResource — when resource is absent from the classpath")
	final class WhenClasspathResourceIsAbsentFromTheClasspath
	{
		@Test
		@DisplayName("returns an empty map without throwing")
		void returnsAnEmptyMapWithoutThrowing()
		{
			var result = new PropertySource.ClasspathResource("nonexistent/missing.properties").load();

			assertThat(result)
					.as("absent classpath resource should silently produce an empty map")
					.isEmpty();
		}
	}

	@Nested
	@DisplayName("FilesystemPath — when file exists and is readable")
	final class WhenFilesystemPathExistsAndIsReadable
	{
		@Test
		@DisplayName("returns all key-value pairs from the file")
		void returnsAllKeyValuePairsFromTheFile(@TempDir final Path tempDir)
		{
			var file = writeProperties(tempDir, "config.properties", "colour=blue\nsize=small");

			var result = new PropertySource.FilesystemPath(file).load();

			assertThat(result)
					.as("all entries written to the file should be present in the result")
					.containsEntry("colour", "blue")
					.containsEntry("size", "small");
		}

		@Test
		@DisplayName("returns an unmodifiable map")
		void returnsAnUnmodifiableMap(@TempDir final Path tempDir)
		{
			var file = writeProperties(tempDir, "config.properties", "key=value");

			var result = new PropertySource.FilesystemPath(file).load();

			assertThatThrownBy(() -> result.put("extra", "value"))
					.as("result map should be unmodifiable")
					.isInstanceOf(UnsupportedOperationException.class);
		}
	}

	@Nested
	@DisplayName("FilesystemPath — when file does not exist")
	final class WhenFilesystemPathDoesNotExist
	{
		@Test
		@DisplayName("returns an empty map without throwing")
		void returnsAnEmptyMapWithoutThrowing(@TempDir final Path tempDir)
		{
			var absent = tempDir.resolve("does-not-exist.properties");

			var result = new PropertySource.FilesystemPath(absent).load();

			assertThat(result)
					.as("absent file should silently produce an empty map")
					.isEmpty();
		}
	}

	// --- Helpers ---

	@Nested
	@DisplayName("SystemPropertiesSource — when loaded")
	final class WhenSystemPropertiesSourceLoaded
	{
		private static final String TEST_KEY = "de.gupta.athena.test.propertysource.load";

		@BeforeEach
		void setTestProperty()
		{
			System.setProperty(TEST_KEY, "live-value");
		}

		@AfterEach
		void clearTestProperty()
		{
			System.clearProperty(TEST_KEY);
		}

		@Test
		@DisplayName("returns a map containing standard JVM system properties")
		void returnsAMapContainingStandardJvmSystemProperties()
		{
			var result = new PropertySource.SystemPropertiesSource().load();

			assertThat(result)
					.as("java.version is a standard JVM property that must always be present")
					.containsKey("java.version");
		}

		@Test
		@DisplayName("reflects live system property state at the moment of the call")
		void reflectsLiveSystemPropertyStateAtTheMomentOfTheCall()
		{
			var result = new PropertySource.SystemPropertiesSource().load();

			assertThat(result)
					.as("system property set before load() should appear in the result")
					.containsEntry(TEST_KEY, "live-value");
		}

		@Test
		@DisplayName("returns an unmodifiable map")
		void returnsAnUnmodifiableMap()
		{
			var result = new PropertySource.SystemPropertiesSource().load();

			assertThatThrownBy(() -> result.put("extra", "value"))
					.as("result map should be unmodifiable")
					.isInstanceOf(UnsupportedOperationException.class);
		}
	}
}