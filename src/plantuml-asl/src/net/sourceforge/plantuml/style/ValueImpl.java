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
package net.sourceforge.plantuml.style;

import java.awt.Font;

import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.IHtmlColorSet;

public class ValueImpl implements Value {

	private final String value;
	private final int priority;

	public ValueImpl(String value, AutomaticCounter counter) {
		this.value = value;
		this.priority = counter.getNextInt();
	}

	@Override
	public String toString() {
		return value + " (" + priority + ")";
	}

	public String asString() {
		return value;
	}

	public HtmlColor asColor(IHtmlColorSet set) {
		return set.getColorIfValid(value);
	}

	public int asInt() {
		return Integer.parseInt(value);
	}

	public double asDouble() {
		return Double.parseDouble(value);
	}

	public int asFontStyle() {
		if (value.equalsIgnoreCase("bold")) {
			return Font.BOLD;
		}
		if (value.equalsIgnoreCase("italic")) {
			return Font.ITALIC;
		}
		return Font.PLAIN;
	}

	public HorizontalAlignment asHorizontalAlignment() {
		return HorizontalAlignment.fromString(asString());
	}

	public int getPriority() {
		return priority;
	}

}
