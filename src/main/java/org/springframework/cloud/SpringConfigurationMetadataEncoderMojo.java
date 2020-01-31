package org.springframework.cloud;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.Base64;
import org.codehaus.plexus.util.FileUtils;

/**
 * Goal which encodes metadata file.
 */
@Mojo(defaultPhase = LifecyclePhase.PROCESS_CLASSES, name = "encode")
public class SpringConfigurationMetadataEncoderMojo extends AbstractMojo {

	/**
	 * https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-configuration-metadata.html#configuration-metadata-format
	 */
	@Parameter(
			property = "metadataFile",
			defaultValue = "${project.build.outputDirectory}/META-INF/spring-configuration-metadata.json",
			required = true)
	private File metadataFile;

	@Parameter(
			property = "encodedMetadataFile",
			defaultValue = "${project.build.outputDirectory}/META-INF/spring-configuration-metadata-encoded.properties",
			required = true)
	private File encodedMetadataFile;

	@Parameter(
			property = "encodedPropertyName",
			defaultValue = "spring.configuration.metadata.encoded",
			required = true)
	private String encodedPropertyName;

	@Override
	public void execute() throws MojoExecutionException {

		try {
			String springConfigurationMetadataContent = FileUtils.fileRead(metadataFile);

			byte[] springConfigurationMetadataEncoded = Base64.encodeBase64(springConfigurationMetadataContent.getBytes());

			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(this.encodedMetadataFile);
				fileWriter.write(this.encodedPropertyName + "=" + new String(springConfigurationMetadataEncoded));
			}
			catch (IOException e) {
				throw new MojoExecutionException("Error creating file " + this.encodedMetadataFile, e);
			}
			finally {
				if (fileWriter != null) {
					try {
						fileWriter.close();
					}
					catch (IOException e) {/* ignore*/}
				}
			}
		}
		catch (IOException e) {
			throw new MojoExecutionException("Error reading metadata file: " + this.metadataFile, e);
		}
	}

	public File getMetadataFile() {
		return metadataFile;
	}

	public void setMetadataFile(File metadataFile) {
		this.metadataFile = metadataFile;
	}

	public String getEncodedPropertyName() {
		return encodedPropertyName;
	}

	public void setEncodedPropertyName(String encodedPropertyName) {
		this.encodedPropertyName = encodedPropertyName;
	}

	public File getEncodedMetadataFile() {
		return encodedMetadataFile;
	}

	public void setEncodedMetadataFile(File encodedMetadataFile) {
		this.encodedMetadataFile = encodedMetadataFile;
	}
}
