/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.plasma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plasma<DATA> {

	private String separator = "\u0000";
	private final Quark<DATA> root;
	private final List<Quark<DATA>> quarks = new ArrayList<>();
	private final Map<String, PEntry<DATA>> stats = new HashMap<String, PEntry<DATA>>();

	public Plasma() {
		this.root = new Quark<DATA>(this, null, "");
	}

	protected void register(Quark<DATA> quark) {
		quarks.add(quark);
		PEntry<DATA> ent = stats.get(quark.getName());
		if (ent == null) {
			ent = new PEntry<DATA>(quark);
			stats.put(quark.getName(), ent);
		} else {
			ent.counter++;
		}
	}

	public Quark<DATA> root() {
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

	public final boolean hasSeparator() {
		return this.separator.equals("\u0000") == false;
	}

	public Collection<Quark<DATA>> quarks() {
		return Collections.unmodifiableCollection(quarks);
	}

	public Quark<DATA> firstWithName(String name) {
		final PEntry<DATA> ent = stats.get(name);
		if (ent == null)
			return null;
		return ent.first;
	}

	public int countByName(String name) {
		final PEntry<DATA> ent = stats.get(name);
		if (ent == null)
			return 0;
		return ent.counter;
	}

}
