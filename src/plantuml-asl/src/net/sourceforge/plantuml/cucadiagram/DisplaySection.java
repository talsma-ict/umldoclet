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

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;

public class DisplaySection {

	private final Map<HorizontalAlignment, Display> map = new EnumMap<HorizontalAlignment, Display>(
			HorizontalAlignment.class);

	private DisplaySection() {
	}

	public DisplaySection withPage(int page, int lastpage) {
		final DisplaySection result = new DisplaySection();
		for (Map.Entry<HorizontalAlignment, Display> ent : this.map.entrySet()) {
			result.map.put(ent.getKey(), ent.getValue().withPage(page, lastpage));
		}
		return result;
	}

	public Display getDisplay() {
		if (map.size() == 0) {
			return null;
		}
		return map.values().iterator().next();
	}

	public static DisplaySection none() {
		return new DisplaySection();
	}

	public final HorizontalAlignment getHorizontalAlignment() {
		if (map.size() == 0) {
			return HorizontalAlignment.CENTER;
		}
		return map.keySet().iterator().next();
	}

	public boolean isNull() {
		if (map.size() == 0) {
			return true;
		}
		final Display display = map.values().iterator().next();
		return Display.isNull(display);
	}

	public TextBlock createRibbon(FontConfiguration fontConfiguration, ISkinSimple spriteContainer) {
		if (map.size() == 0) {
			return null;
		}
		final Display display = map.values().iterator().next();
		if (Display.isNull(display) || display.size() == 0) {
			return null;
		}
		// if (UseStyle.USE_STYLES()) {
		// throw new UnsupportedOperationException();
		// }
		return display.create(fontConfiguration, getHorizontalAlignment(), spriteContainer);
	}

	public void putDisplay(Display display, HorizontalAlignment horizontalAlignment) {
		this.map.put(horizontalAlignment, display);

	}

}
