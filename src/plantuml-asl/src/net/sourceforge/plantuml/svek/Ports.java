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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import net.sourceforge.plantuml.SignatureUtils;

public class Ports {

	private final Map<String, PortGeometry> ids = new HashMap<String, PortGeometry>();

	public static String encodePortNameToId(String portName) {
		return "p" + SignatureUtils.getMD5Hex(portName);
	}

	@Override
	public String toString() {
		return ids.toString();
	}

	public Ports translateY(double deltaY) {
		final Ports result = new Ports();
		for (Map.Entry<String, PortGeometry> ent : ids.entrySet())
			result.ids.put(ent.getKey(), ent.getValue().translateY(deltaY));

		return result;
	}

	public void add(String portName, int score, double position, double height) {
		final String id = encodePortNameToId(Objects.requireNonNull(portName));
		final PortGeometry already = ids.get(id);
		if (already == null || already.getScore() < score)
			ids.put(id, new PortGeometry(id, position, height, score));
	}

	public void addThis(Ports other) {
		for (Entry<String, PortGeometry> ent : other.ids.entrySet()) {
			final String key = ent.getKey();
			final PortGeometry already = ids.get(key);
			if (already == null || already.getScore() < ent.getValue().getScore())
				ids.put(key, ent.getValue());
		}
	}

	public Collection<PortGeometry> getAllPortGeometry() {
		final List<PortGeometry> result = new ArrayList<PortGeometry>(ids.values());
		Collections.sort(result);
		return Collections.unmodifiableCollection(result);
	}

}
