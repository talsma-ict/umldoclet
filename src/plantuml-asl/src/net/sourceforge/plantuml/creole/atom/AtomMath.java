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
package net.sourceforge.plantuml.creole.atom;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.SvgString;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.math.ScientificEquationSafe;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UImageSvg;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSimple;

public class AtomMath extends AbstractAtom implements Atom {

	private final double scale;
	private final ScientificEquationSafe math;
	private final HColor foreground;
	private final HColor background;
	private final ColorMapper colorMapper;

	public AtomMath(ScientificEquationSafe math, HColor foreground, HColor background, double scale,
			ColorMapper colorMapper) {
		this.math = math;
		this.colorMapper = colorMapper;
		this.foreground = foreground;
		this.background = background;
		this.scale = scale;
	}

	private Dimension2D calculateDimensionSlow(StringBounder stringBounder) {
		final BufferedImage image = math.getImage(scale, Color.BLACK, Color.WHITE);
		return new Dimension2DDouble(image.getWidth(), image.getHeight());
	}

	private Dimension2D dim;

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		if (dim == null) {
			dim = calculateDimensionSlow(stringBounder);
		}
		return dim;
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return 0;
	}

	public void drawU(UGraphic ug) {
		final boolean isSvg = ug.matchesProperty("SVG");
		final Color back;
		if (background == null) {
			back = null;
		} else {
			back = getColor(background, Color.WHITE);
		}
		final Color fore = getColor(foreground, Color.BLACK);
		// final double dpiFactor = ug.dpiFactor();
		if (isSvg) {
			final SvgString svg = math.getSvg(scale, fore, back);
			ug.draw(new UImageSvg(svg));
		} else {
			final UImage image = new UImage(null, math.getImage(scale, fore, back), math.getFormula());
			ug.draw(image);
		}
	}

	private Color getColor(HColor color, Color defaultValue) {
		if (color instanceof HColorSimple) {
			return colorMapper.toColor(color);
		}
		return defaultValue;

	}
}
