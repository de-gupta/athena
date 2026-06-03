package de.gupta.commons.utility.properties;

import de.gupta.aletheia.functional.Unfolding;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

sealed interface PropertySource permits PropertySource.ClasspathResource,
		PropertySource.FilesystemPath,
		PropertySource.SystemPropertiesSource
{
	static Map<String, String> toStringMap(final Properties properties)
	{
		return properties.stringPropertyNames()
		                 .stream()
		                 .filter(key -> !key.isEmpty())
		                 .collect(Collectors.toUnmodifiableMap(key -> key, properties::getProperty));
	}

	Map<String, String> load();

	record ClasspathResource(String resourcePath) implements PropertySource
	{
		@Override
		public Map<String, String> load()
		{
			return Unfolding.beckon(
									Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath))
			                .metamorphose(ClasspathResource::readStream)
			                .infuse(Map::of);
		}

		private static Map<String, String> readStream(final InputStream stream)
		{
			try (stream)
			{
				var props = new Properties();
				props.load(stream);
				return PropertySource.toStringMap(props);
			}
			catch (IOException _)
			{
				return Map.of();
			}
		}
	}

	record FilesystemPath(Path path) implements PropertySource
	{
		@Override
		public Map<String, String> load()
		{
			return Unfolding.beckon(path)
			                .discern(p -> Files.exists(p) && Files.isReadable(p))
			                .metamorphose(FilesystemPath::readPath)
			                .infuse(Map::of);
		}

		private static Map<String, String> readPath(final Path p)
		{
			try (var input = Files.newInputStream(p))
			{
				var props = new Properties();
				props.load(input);
				return PropertySource.toStringMap(props);
			}
			catch (IOException _)
			{
				return Map.of();
			}
		}
	}

	record SystemPropertiesSource() implements PropertySource
	{
		@Override
		public Map<String, String> load()
		{
			return PropertySource.toStringMap(System.getProperties());
		}
	}
}