/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
package net.sourceforge.plantuml.baraye;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Plasma {

	private String separator;
	private final Quark root;
	private final Map<List<String>, Quark> quarks = new LinkedHashMap<>();

	public Plasma(String separator) {
		final List<String> empty = Collections.emptyList();
		this.root = ensurePresent(empty);
		this.separator = separator;
	}

	public Quark root() {
		return root;
	}

	public final String getSeparator() {
		return separator;
	}

	public final void setSeparator(String separator) {
		if (separator == null)
			separator = "\u0000";
		this.separator = separator;
	}

	public Quark parse(Quark root, String full) {

		final List<String> result = root.getSignature();
		while (true) {
			int idx = full.indexOf(separator);
			if (idx == -1) {
				result.add(full);
				return ensurePresent(result);
			}
			if (idx > 0) {
				result.add(full.substring(0, idx));
				ensurePresent(new ArrayList<>(result));
			}

			full = full.substring(idx + separator.length());
		}
	}

	Quark ensurePresent(List<String> result) {
		Quark quark = quarks.get(result);
		if (quark == null) {
			if (result.size() == 0)
				quark = new Quark(this, null, result);
			else {
				final Quark parent = ensurePresent(result.subList(0, result.size() - 1));
				quark = new Quark(this, parent, result);
			}
			System.err.println("PUTTING " + quark);
			quarks.put(result, quark);
		}
		return quark;

	}

	public Collection<Quark> quarks() {
		return Collections.unmodifiableCollection(new ArrayList<>(quarks.values()));
	}

//	public boolean exists(String name) {
//		for (Quark quark : quarks.values())
//			if (quark.getName().equals(name))
//				return true;
//		return false;
//	}

	public Quark getIfExistsFromName(String name) {
		for (Quark quark : quarks.values())
			if (quark.getName().equals(name))
				return quark;
		return null;
	}

	public Quark getIfExistsFromFullPath(String full) {
		for (Quark quark : quarks.values())
			if (quark.toString(separator).equals(full))
				return quark;
		return null;
	}

	public Quark getIfExists(List<String> signature) {
		return quarks.get(signature);
	}

	public int countChildren(Quark parent) {
		int count = 0;
		for (Quark quark : new ArrayList<>(quarks.values()))
			if (quark.getParent() == parent)
				count++;
		return count;
	}

	public List<Quark> getChildren(Quark parent) {
		final List<Quark> result = new ArrayList<>();
		for (Quark quark : new ArrayList<>(quarks.values()))
			if (quark.getParent() == parent)
				result.add(quark);
		return Collections.unmodifiableList(result);
	}

	public void moveAllTo(Quark src, Quark dest) {
		for (Quark quark : new ArrayList<>(quarks.values())) {
			if (quark == dest)
				continue;
			if (src.containsLarge(quark)) {
				quarks.remove(quark.getSignature());
				quark.internalMove(src, dest);
				quarks.put(quark.getSignature(), quark);
			}
		}

	}

}
