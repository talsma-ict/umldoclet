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
package net.sourceforge.plantuml.sequencediagram.teoz;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public class Blotter implements UDrawable {

	private final XDimension2D dim;
	private final HColor defaultBackcolor;
	private final double round;
	private HColor last;
	private final SortedMap<Double, HColor> changes = new TreeMap<>();

	public Blotter(XDimension2D dim, HColor defaultBackcolor, double round) {
		if (defaultBackcolor == null)
			defaultBackcolor = HColors.transparent();
		this.round = round;
		this.dim = dim;
		this.defaultBackcolor = defaultBackcolor;
		this.last = defaultBackcolor;
	}

	@Override
	public String toString() {
		return "" + dim + " " + defaultBackcolor;
	}

	@Override
	public void drawU(UGraphic ug) {
		HColor current = defaultBackcolor;
		double y = 0;
		int i = 0;
		for (Entry<Double, HColor> ent : changes.entrySet()) {
			if (current.isTransparent() == false) {
				final UShape rect = getRectangleBackground(i, ent.getKey() - y);
				ug.apply(current).apply(current.bg()).apply(UTranslate.dy(y)).draw(rect);
			}
			y = ent.getKey();
			current = ent.getValue();
			i++;
		}
	}

	private UShape getRectangleBackground(int i, double height) {
		final double width = dim.getWidth();
		if (round == 0)
			return new URectangle(width, height);

		if (changes.size() == 1)
			return new URectangle(width, height).rounded(round);

		if (i == 0) {
			final UPath result = new UPath();
			result.moveTo(round / 2, 0);
			result.lineTo(width - round / 2, 0);
			result.arcTo(round / 2, round / 2, 0, 0, 1, width, round / 2);
			result.lineTo(width, height);
			result.lineTo(0, height);
			result.lineTo(0, round / 2);
			result.arcTo(round / 2, round / 2, 0, 0, 1, round / 2, 0);
			result.closePath();
			return result;
		}
		if (i == changes.size() - 1) {
			final UPath result = new UPath();
			result.moveTo(0, 0);
			result.lineTo(width, 0);
			result.lineTo(width, height - round / 2);
			result.arcTo(round / 2, round / 2, 0, 0, 1, width - round / 2, height);
			result.lineTo(round / 2, height);
			result.arcTo(round / 2, round / 2, 0, 0, 1, 0, height - round / 2);
			result.lineTo(0, 0);
			result.closePath();
			return result;

		}
		return new URectangle(width, height);
	}

	public void closeChanges() {
		changes.put(dim.getHeight(), defaultBackcolor);
	}

	public void addChange(double ypos, HColor color) {
		if (color == null)
			color = HColors.transparent();
		if (color.equals(last))
			return;
		changes.put(ypos, color);
		last = color;
	}

}
