/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.cute;

import java.util.Map;

import net.sourceforge.plantuml.StringUtils;

public class CuteShapeFactory {

	private final Map<String, Group> groups;

	public CuteShapeFactory(Map<String, Group> groups) {
		this.groups = groups;

	}

	public Positionned createCuteShapePositionned(String data) {
		final VarArgs varArgs = new VarArgs(data);
		return new PositionnedImpl(createCuteShape(data), varArgs);
	}

	private CuteShape createCuteShape(String data) {
		data = StringUtils.trin(data.toLowerCase());
		final VarArgs varArgs = new VarArgs(data);
		if (data.startsWith("circle ")) {
			return new Circle(varArgs);
		}
		if (data.startsWith("cheese ")) {
			return new Cheese(varArgs);
		}
		if (data.startsWith("stick ")) {
			return new Stick(varArgs);
		}
		if (data.startsWith("rectangle ") || data.startsWith("rect ")) {
			return new Rectangle(varArgs);
		}
		if (data.startsWith("triangle ")) {
			return new Triangle(varArgs);
		}
		final String first = data.split(" ")[0];
		// System.err.println("Looking for group " + first + " in " + groups.keySet());
		final Group group = groups.get(first);
		if (group == null) {
			throw new IllegalArgumentException("Cannot find group " + first + " in " + groups.keySet());
		}
		// System.err.println("Found group " + first + " in " + groups.keySet());
		return group;
	}

}
