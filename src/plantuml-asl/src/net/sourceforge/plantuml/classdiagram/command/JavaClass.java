/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.classdiagram.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.cucadiagram.LeafType;

class JavaClass {

	private final String name;
	private final String javaPackage;
	private final List<String> parents = new ArrayList<String>();
	private final LeafType type;
	private final LeafType parentType;

	public JavaClass(String javaPackage, String name, String p, LeafType type, LeafType parentType) {
		this.name = name;
		this.javaPackage = javaPackage;
		if (p == null) {
			p = "";
		}
		final StringTokenizer st = new StringTokenizer(StringUtils.trin(p), ",");
		while (st.hasMoreTokens()) {
			this.parents.add(StringUtils.trin(st.nextToken()).replaceAll("\\<.*", ""));
		}
		this.type = type;
		this.parentType = parentType;
	}

	public final String getName() {
		return name;
	}

	public final LeafType getType() {
		return type;
	}

	public final List<String> getParents() {
		return Collections.unmodifiableList(parents);
	}

	public final LeafType getParentType() {
		return parentType;
	}

	public final String getJavaPackage() {
		return javaPackage;
	}

}
