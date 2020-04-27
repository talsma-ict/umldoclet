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
package net.sourceforge.plantuml.svek;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.SignatureUtils;

public class Ports {

	private final Map<String, PortGeometry> ids = new LinkedHashMap<String, PortGeometry>();

	public void addThis(Ports other) {
		ids.putAll(other.ids);
	}

	public static String encodePortNameToId(String portName) {
		return "p" + SignatureUtils.getMD5Hex(portName);
	}

	@Override
	public String toString() {
		return ids.toString();
	}

	public Ports translateY(double deltaY) {
		final Ports result = new Ports();
		for (Map.Entry<String, PortGeometry> ent : ids.entrySet()) {
			result.ids.put(ent.getKey(), ent.getValue().translateY(deltaY));
		}
		return result;
	}

	public void add(String portName, double position, double height) {
		if (portName == null) {
			throw new IllegalArgumentException();
		}
		final String id = encodePortNameToId(portName);
		ids.put(id, new PortGeometry(position, height));
	}

	public Map<String, PortGeometry> getAllWithEncodedPortId() {
		return ids;
	}

}
