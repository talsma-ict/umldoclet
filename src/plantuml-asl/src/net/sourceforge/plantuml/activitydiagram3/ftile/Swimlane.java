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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.util.Set;

import net.sourceforge.plantuml.SpecificBackcolorable;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class Swimlane implements SpecificBackcolorable, Comparable<Swimlane> {

	private final String name;
	private final int order;
	private Display display;

	private UTranslate translate = new UTranslate();
	private double actualWidth;

	public Swimlane(String name, int order) {
		this.name = name;
		this.display = Display.getWithNewlines(name);
		this.order = order;

	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public Display getDisplay() {
		return display;
	}

	public void setDisplay(Display label) {
		this.display = label;
	}

	public final UTranslate getTranslate() {
		return translate;
	}

	public final void setTranslate(UTranslate translate) {
		this.translate = translate;
	}

	public final void setWidth(double actualWidth) {
		this.actualWidth = actualWidth;
	}

	public Colors getColors() {
		return colors;
	}

	public void setSpecificColorTOBEREMOVED(ColorType type, HColor color) {
		if (color != null) {
			this.colors = colors.add(type, color);
		}
	}

	private Colors colors = Colors.empty();

	public final double getActualWidth() {
		return actualWidth;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	private MinMax minMax;

	public void setMinMax(MinMax minMax) {
		this.minMax = minMax;

	}

	public MinMax getMinMax() {
		return minMax;
	}

	@Override
	public int compareTo(Swimlane other) {
		return Integer.compare(this.order, other.order);
	}

	public boolean isSmallerThanAllOthers(Set<Swimlane> others) {
		if (others.size() == 1 && others.contains(this))
			return false;
		for (Swimlane sw : others)
			if (sw.compareTo(this) < 0)
				return false;
		return true;
	}
}
