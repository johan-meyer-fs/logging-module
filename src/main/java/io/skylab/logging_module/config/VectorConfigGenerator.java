package io.skylab.logging_module.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Configuration class that generates Vector YAML configuration file by reading
 * a template and substituting environment variables.
 */
@AutoConfiguration
@Component
@Slf4j
@ConfigurationProperties(prefix = "vector")
public class VectorConfigGenerator
{

	private static final Pattern ENV_VAR_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

	@Value("${vector.api.enabled}")
	private String apiEnabled;
	@Value("${vector.template.path:classpath:vector-template.yaml}")
	private String templatePath;
	@Value("${vector.config.include-path}")
	private String configIncludePath;
	@Value("${vector.config.output:vector.yaml}")
	private String configOutputPath;
	@Value("${betterstack.ingesting-host}")
	private String ingestingHost;
	@Value("${betterstack.source-token}")
	private String sourceToken;

	/**
	 * Generates the Vector YAML config file
	 *
	 * @return boolean indicating success or failure
	 */
	@PostConstruct
	public boolean generateConfig()
	{
		try
		{
			String templateContent = readTemplateFile();
			String processedContent = processTemplate(templateContent);
			writeYamlFile(processedContent);
			log.info("Successfully generated Vector YAML configuration at {}", configOutputPath);
			return true;
		} catch (IOException e)
		{
			log.error("Failed to generate Vector configuration file {}", e);
			return false;
		}
	}

	/**
	 * Reads the template file from the specified path
	 *
	 * @return String containing the template content
	 * @throws IOException If template file cannot be read
	 */
	private String readTemplateFile() throws IOException
	{
		if (templatePath.startsWith("classpath:"))
		{
			String resourcePath = templatePath.substring("classpath:".length());
			Resource resource = new ClassPathResource(resourcePath);
			return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		} else
		{
			return Files.readString(Paths.get(templatePath), StandardCharsets.UTF_8);
		}
	}

	/**
	 * Processes the template by substituting environment variables
	 *
	 * @param template The template content
	 * @return The processed content with variables substituted
	 */
	private String processTemplate(String template)
	{
		Map<String, String> envVars = collectEnvironmentVariables();

		Matcher matcher = ENV_VAR_PATTERN.matcher(template);
		StringBuffer result = new StringBuffer();

		while (matcher.find())
		{
			String varName = matcher.group(1);
			String replacement = envVars.getOrDefault(varName, "${" + varName + "}");
			// Escape $ and \ for the replacement string in Matcher.appendReplacement
			replacement = replacement.replace("\\", "\\\\").replace("$", "\\$");
			matcher.appendReplacement(result, replacement);
		}
		matcher.appendTail(result);

		return result.toString();
	}

	/**
	 * Collects environment variables including Spring-injected ones
	 *
	 * @return Map of environment variable names to values
	 */
	private Map<String, String> collectEnvironmentVariables()
	{
		Map<String, String> vars = new HashMap<>();
		vars.put("configIncludePath", configIncludePath);
		vars.put("apiEnabled", apiEnabled);
		vars.put("ingestingHost", ingestingHost);
		vars.put("sourceToken", sourceToken);
		return vars;
	}

	/**
	 * Writes the processed content to the output file
	 *
	 * @param content The processed configuration content
	 * @throws IOException If file cannot be written
	 */
	private void writeYamlFile(String content) throws IOException
	{
		Path configPath = Paths.get(configOutputPath);
		Files.writeString(configPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	/**
	 * Sets the path to the template file
	 *
	 * @param templatePath The path to the template file (can be classpath: or file
	 *                     system path)
	 */
	public void setTemplatePath(String templatePath)
	{
		this.templatePath = templatePath;
	}

	/**
	 * Sets the output path for the generated YAML file
	 *
	 * @param configOutputPath The file path where the YAML file should be written
	 */
	public void setConfigOutputPath(String configOutputPath)
	{
		this.configOutputPath = configOutputPath;
	}
}