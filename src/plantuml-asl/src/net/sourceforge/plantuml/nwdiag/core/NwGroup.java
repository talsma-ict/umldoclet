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
package net.sourceforge.plantuml.nwdiag.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.nwdiag.legacy.NServerLegacy;
import net.sourceforge.plantuml.nwdiag.next.NBox;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class NwGroup {

	public static final HColorSet colors = HColorSet.instance();

	private final Set<String> names = new HashSet<>();

	private final String name;
	private HColor color;
	private String description;
	private NBox nbox;

	public final NBox getNbox(Map<String, NServerLegacy> servers) {
		if (nbox == null) {
			nbox = new NBox();
			for (Entry<String, NServerLegacy> ent : servers.entrySet()) {
				if (names.contains(ent.getKey())) {
					nbox.add(ent.getValue().getBar());
				}
			}
		}
		return nbox;
	}

	public void addName(String name) {
		this.names.add(name);
	}

	@Override
	public String toString() {
		return name;
	}

	public NwGroup(String name) {
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public final HColor getColor() {
		return color;
	}

	public final void setColor(HColor color) {
		this.color = color;
	}

	public final void setDescription(String value) {
		this.description = value;
	}

	public final FontConfiguration getGroupDescriptionFontConfiguration() {
		final UFont font = UFont.serif(11);
		return new FontConfiguration(font, HColorUtils.BLACK, HColorUtils.BLACK, false);
	}

	protected final String getDescription() {
		return description;
	}

	public final Set<String> names() {
		return Collections.unmodifiableSet(names);
	}

}
