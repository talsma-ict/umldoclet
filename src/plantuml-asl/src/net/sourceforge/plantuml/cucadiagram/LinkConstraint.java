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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class LinkConstraint {

	private final Link link1;
	private final Link link2;
	private final Display display;

	private double x1;
	private double y1;
	private double x2;
	private double y2;

	public LinkConstraint(Link link1, Link link2, Display display) {
		this.link1 = link1;
		this.link2 = link2;
		this.display = display;
	}

	public void setPosition(Link link, Point2D pt) {
		if (link == link1) {
			x1 = pt.getX();
			y1 = pt.getY();
		} else if (link == link2) {
			x2 = pt.getX();
			y2 = pt.getY();
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void drawMe(UGraphic ug, ISkinParam skinParam) {
		if (x1 == 0 && y1 == 0) {
			return;
		}
		if (x2 == 0 && y2 == 0) {
			return;
		}
		ug = ug.apply(HColorUtils.BLACK);
//		ug.apply(new UTranslate(x1, y1)).draw(new URectangle(10, 10));
//		ug.apply(new UTranslate(x2, y2)).draw(new URectangle(10, 10));

		final ULine line = new ULine(x2 - x1, y2 - y1);
		ug = ug.apply(new UStroke(3, 3, 1));
		ug.apply(new UTranslate(x1, y1)).draw(line);

		final TextBlock label = display.create(new FontConfiguration(skinParam, FontParam.ARROW, null),
				HorizontalAlignment.CENTER, skinParam);
		final Dimension2D dimLabel = label.calculateDimension(ug.getStringBounder());
		final double x = (x1 + x2) / 2 - dimLabel.getWidth() / 2;
		final double y = (y1 + y2) / 2 - dimLabel.getHeight() / 2;
		label.drawU(ug.apply(new UTranslate(x, y)));

	}

}
