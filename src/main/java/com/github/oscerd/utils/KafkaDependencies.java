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
package com.github.oscerd.utils;

import java.util.ArrayList;

import org.apache.maven.model.Dependency;

public class KafkaDependencies {

	private static KafkaDependencies instance;
	private ArrayList<Dependency> deps = null;

	public static KafkaDependencies getInstance() {
		if (instance == null)
			instance = new KafkaDependencies();

		return instance;
	}

	private KafkaDependencies() {
		deps = new ArrayList<Dependency>();
		Dependency dep = new Dependency();
		dep.setGroupId("org.apache.kafka");
		dep.setArtifactId("kafka-clients");
		deps.add(dep);
	}

	public ArrayList<Dependency> getKafkaDeps() {
		return deps;
	}
}
