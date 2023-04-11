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
package net.sourceforge.plantuml.klimt.creole.atom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.salt.element.Skeleton2;

public class AtomTree extends AbstractAtom implements Atom {

	private final HColor lineColor;
	private final List<Atom> cells = new ArrayList<>();
	private final Map<Atom, Integer> levels = new HashMap<Atom, Integer>();
	private final double margin = 2;

	public AtomTree(HColor lineColor) {
		this.lineColor = lineColor;
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		final Skeleton2 skeleton = new Skeleton2();
		double width = 0;
		double height = 0;
		for (Atom cell : cells) {
			final XDimension2D dim = cell.calculateDimension(stringBounder);
			height += dim.getHeight();
			final int level = getLevel(cell);
			width = Math.max(width, skeleton.getXEndForLevel(level) + margin + dim.getWidth());
		}
		return new XDimension2D(width, height);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return 0;
	}

	public void drawU(final UGraphic ugInit) {
		final Skeleton2 skeleton = new Skeleton2();
		double y = 0;
		UGraphic ug = ugInit;
		for (Atom cell : cells) {
			final int level = getLevel(cell);
			cell.drawU(ug.apply(UTranslate.dx(margin + skeleton.getXEndForLevel(level))));
			final XDimension2D dim = cell.calculateDimension(ug.getStringBounder());
			skeleton.add(level, y + dim.getHeight() / 2);
			ug = ug.apply(UTranslate.dy(dim.getHeight()));
			y += dim.getHeight();
		}
		skeleton.draw(ugInit.apply(this.lineColor));
	}

	private int getLevel(Atom atom) {
		return levels.get(atom);
	}

	public void addCell(Atom cell, int level) {
		this.cells.add(cell);
		this.levels.put(cell, level);
	}

}
