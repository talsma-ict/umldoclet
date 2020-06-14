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
package net.sourceforge.plantuml.svek;

import java.util.LinkedHashMap;
import java.util.Map;

public class Ports {

	private final Map<String, PortGeometry> all = new LinkedHashMap<String, PortGeometry>();

	public void addThis(Ports other) {
		all.putAll(other.all);
	}

	@Override
	public String toString() {
		return all.toString();
	}

	public Ports translateY(double deltaY) {
		final Ports result = new Ports();
		for (Map.Entry<String, PortGeometry> ent : all.entrySet()) {
			result.all.put(ent.getKey(), ent.getValue().translateY(deltaY));
		}
		return result;
	}

	public void add(String portName, double position, double height) {
		if (portName == null) {
			throw new IllegalArgumentException();
		}
		all.put(portName, new PortGeometry(position, height));
	}

	public Map<String, PortGeometry> getAll() {
		return all;
	}

}
