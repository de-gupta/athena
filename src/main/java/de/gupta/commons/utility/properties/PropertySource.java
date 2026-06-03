package de.gupta.commons.utility.properties;

import java.nio.file.Path;

sealed interface PropertySource permits PropertySource.ClasspathResource,
		PropertySource.FilesystemPath,
		PropertySource.SystemPropertiesSource
{
	record ClasspathResource(String resourcePath) implements PropertySource
	{
	}

	record FilesystemPath(Path path) implements PropertySource
	{
	}

	record SystemPropertiesSource() implements PropertySource
	{
	}
}