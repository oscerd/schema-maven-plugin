/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.oscerd;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.avro.AvroFactory;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;
import com.fasterxml.jackson.dataformat.avro.schema.AvroSchemaGenerator;
import com.fasterxml.jackson.dataformat.protobuf.ProtobufFactory;
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchema;
import com.fasterxml.jackson.dataformat.protobuf.schemagen.ProtobufSchemaGenerator;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Goal which create a schema for a particular class
 */
@Mojo(name = "generate-schemas", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenerateSchemasMojo extends AbstractMojo {
	/**
	 * Input Class for which we want to generate the schema
	 */
	@Parameter(property = "inputClass", required = true)
	private String inputClass;
	/**
	 * File name for the schema generate Input Class for which we want to generate
	 * the schema
	 */
	@Parameter(property = "fileNameSchema", required = true)
	private String fileNameSchema;
	/**
	 * Location of the file.
	 */
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;
	/**
	 * A comma separated list of formats. It could be avro and json
	 */
	@Parameter(defaultValue = "avro", property = "schemaKind", required = true)
	private String schemaKind;
	
    /**
     * POM
     */
	@Parameter( defaultValue = "${project}", readonly = true )
	MavenProject project;

	public void execute() throws MojoExecutionException {
		String[] s = schemaKind.split(",");
		for (int i = 0; i < s.length; i++) {
			if (s[i].equalsIgnoreCase("avro")) {
				AvroSchema schemaWrapper = writeAvroSchema();
				writeSchema(schemaWrapper.getAvroSchema().toString(true), ".avsc");
			}
			if (s[i].equalsIgnoreCase("json")) {
				String schemaWrapper = writeJsonSchema();
				writeSchema(schemaWrapper, ".json");
			}
			if (s[i].equalsIgnoreCase("proto")) {
				ProtobufSchema schemaWrapper = writeProtoSchema();
				writeSchema(schemaWrapper.toString(), ".proto");
			}
		}
	}

	private AvroSchema writeAvroSchema() {
		ObjectMapper mapper = new ObjectMapper(new AvroFactory());
		AvroSchemaGenerator gen = new AvroSchemaGenerator();
		try {
			mapper.acceptJsonFormatVisitor(Class.forName(inputClass), gen);
		} catch (JsonMappingException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		AvroSchema schemaWrapper = gen.getGeneratedSchema();
		return schemaWrapper;
	}

	private String writeJsonSchema() {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(objectMapper);

		JsonNode jsonSchema = null;
		String jsonString = null;
		try {
			jsonSchema = jsonSchemaGenerator.generateJsonSchema(Class.forName(inputClass));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	private ProtobufSchema writeProtoSchema() {
		ObjectMapper mapper = new ObjectMapper(new ProtobufFactory());
		ProtobufSchemaGenerator gen = new ProtobufSchemaGenerator();
		try {
			mapper.acceptJsonFormatVisitor(Class.forName(inputClass), gen);
		} catch (JsonMappingException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		ProtobufSchema schemaWrapper = null;
		try {
			schemaWrapper = gen.getGeneratedSchema();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return schemaWrapper;
	}

	private void writeSchema(String content, String ext) throws MojoExecutionException {
		File f = outputDirectory;

		if (!f.exists()) {
			f.mkdirs();
		}

		File touch = new File(f, fileNameSchema + ext);

		FileWriter w = null;
		try {
			w = new FileWriter(touch);

			w.write(content);
		} catch (IOException e) {
			throw new MojoExecutionException("Error creating file " + touch, e);
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
}
