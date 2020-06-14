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
package net.sourceforge.plantuml.ugraphic;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.svek.image.ContainingEllipse;
import net.sourceforge.plantuml.svek.image.Footprint;

public class TextBlockInEllipse extends AbstractTextBlock implements TextBlock {

	private final TextBlock text;
	private final ContainingEllipse ellipse;

	public TextBlockInEllipse(TextBlock text, StringBounder stringBounder) {
		this.text = text;
		final Dimension2D textDim = text.calculateDimension(stringBounder);
		double alpha = textDim.getHeight() / textDim.getWidth();
		if (alpha < .2) {
			alpha = .2;
		} else if (alpha > .8) {
			alpha = .8;
		}
		final Footprint footprint = new Footprint(stringBounder);
		ellipse = footprint.getEllipse(text, alpha);

	}

	public UEllipse getUEllipse() {
		return ellipse.asUEllipse().bigger(6);
	}

	public void drawU(UGraphic ug) {
		final UEllipse sh = getUEllipse();
		final Point2D center = ellipse.getCenter();
		
		final double dx = sh.getWidth() / 2 - center.getX();
		final double dy = sh.getHeight() / 2 - center.getY();
		
		ug.draw(sh);
		
		text.drawU(ug.apply(new UTranslate(dx, (dy - 2))));
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return getUEllipse().getDimension();
	}

	public void setDeltaShadow(double deltaShadow) {
		ellipse.setDeltaShadow(deltaShadow);
	}
}
