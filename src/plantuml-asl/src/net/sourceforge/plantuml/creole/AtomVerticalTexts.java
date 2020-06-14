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
package net.sourceforge.plantuml.creole;

import java.awt.geom.Dimension2D;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class AtomVerticalTexts extends AbstractAtom implements Atom {
	private final List<Atom> all;

	public AtomVerticalTexts(List<Atom> texts) {
		this.all = texts;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		double width = 0;
		double height = 0;
		for (Atom text : all) {
			final Dimension2D dim = text.calculateDimension(stringBounder);
			width = Math.max(width, dim.getWidth());
			height += dim.getHeight();
		}
		return new Dimension2DDouble(width, height);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return all.get(0).getStartingAltitude(stringBounder);
	}

	public void drawU(UGraphic ug) {
		double y = 0;
		for (Atom text : all) {
			final Dimension2D dim = text.calculateDimension(ug.getStringBounder());
			text.drawU(ug.apply(new UTranslate(0, y)));
			y += dim.getHeight();
		}
	}

}
