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
package net.sourceforge.plantuml.cucadiagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

class Election {

	private final Map<String, CharSequence> all = new HashMap<String, CharSequence>();

	public void addCandidate(String display, CharSequence candidate) {
		all.put(display, candidate);

	}

	private CharSequence getCandidate(String shortName) {
		List<CharSequence> list = getAllCandidateContains(shortName);
		if (list.size() == 1) {
			return list.get(0);
		}
		list = getAllCandidateContainsStrict(shortName);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	private List<CharSequence> getAllCandidateContains(String shortName) {
		final List<CharSequence> result = new ArrayList<CharSequence>();
		for (Map.Entry<String, CharSequence> ent : all.entrySet()) {
			if (ent.getKey().contains(shortName)) {
				result.add(ent.getValue());
			}
		}
		return result;
	}

	private List<CharSequence> getAllCandidateContainsStrict(String shortName) {
		final List<CharSequence> result = new ArrayList<CharSequence>();
		for (Map.Entry<String, CharSequence> ent : all.entrySet()) {
			final String key = ent.getKey();
			if (key.matches(".*\\b" + shortName + "\\b.*")) {
				result.add(ent.getValue());
			}
		}
		return result;
	}

	public Map<CharSequence, String> getAllElected(Collection<String> shortNames) {
		final Map<CharSequence, String> memberWithPort = new HashMap<CharSequence, String>();
		for (String shortName : new HashSet<String>(shortNames)) {
			final CharSequence m = getCandidate(shortName);
			if (m != null) {
				memberWithPort.put(m, shortName);
				shortNames.remove(shortName);
			}
		}
		return Collections.unmodifiableMap(memberWithPort);
	}

}
