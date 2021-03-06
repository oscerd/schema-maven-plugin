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

import org.apache.maven.plugin.testing.MojoRule;

import org.junit.Rule;
import static org.junit.Assert.*;
import org.junit.Test;
import java.io.File;

public class MyMojoTest {
	@Rule
	public MojoRule rule = new MojoRule() {
		@Override
		protected void before() throws Throwable {
		}

		@Override
		protected void after() {
		}
	};

	/**
	 * @throws Exception if any
	 */
	@Test
	public void testSomething() throws Exception {
		File pom = new File("target/test-classes/project-to-test/");
		assertNotNull(pom);
		assertTrue(pom.exists());

		GenerateSchemasMojo myMojo = (GenerateSchemasMojo) rule.lookupConfiguredMojo(pom, "generate-schemas");
		assertNotNull(myMojo);
		myMojo.execute();

		File outputDirectory = (File) rule.getVariableValueFromObject(myMojo, "outputDirectory");
		assertNotNull(outputDirectory);
		assertTrue(outputDirectory.exists());

		File schemaAvro = new File(outputDirectory, "user.avsc");
		assertTrue(schemaAvro.exists());
		
		File schemaJson = new File(outputDirectory, "user.json");
		assertTrue(schemaJson.exists());
		
		File schemaProto = new File(outputDirectory, "user.proto");
		assertTrue(schemaProto.exists());
	}

}
