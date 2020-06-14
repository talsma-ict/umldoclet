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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractConnection;
import net.sourceforge.plantuml.activitydiagram3.ftile.Arrows;
import net.sourceforge.plantuml.activitydiagram3.ftile.ConnectionTranslatable;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Snake;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ConnectionVerticalDown extends AbstractConnection implements ConnectionTranslatable {

	private final Point2D p1;
	private final Point2D p2;
	private final Rainbow color;
	private final TextBlock textBlock;

	public ConnectionVerticalDown(Ftile ftile1, Ftile ftile2, Point2D p1, Point2D p2, Rainbow color, TextBlock textBlock) {
		super(ftile1, ftile2);
		if (color.size() == 0) {
			throw new IllegalArgumentException();
		}
		this.p1 = p1;
		this.p2 = p2;
		this.color = color;
		this.textBlock = textBlock;
	}

	public void drawU(UGraphic ug) {
		ug.draw(getSimpleSnake());
	}

	public double getMaxX(StringBounder stringBounder) {
		return getSimpleSnake().getMaxX(stringBounder);
	}

	private Snake getSimpleSnake() {
		final Snake snake = new Snake(arrowHorizontalAlignment(), color, Arrows.asToDown());
		snake.setLabel(textBlock);
		snake.addPoint(p1);
		snake.addPoint(p2);
		return snake;
	}

	public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
		final Snake snake = new Snake(arrowHorizontalAlignment(), color, Arrows.asToDown());
		snake.setLabel(textBlock);
		final Point2D mp1a = translate1.getTranslated(p1);
		final Point2D mp2b = translate2.getTranslated(p2);
		final double middle = (mp1a.getY() + mp2b.getY()) / 2.0;
		snake.addPoint(mp1a);
		snake.addPoint(mp1a.getX(), middle);
		snake.addPoint(mp2b.getX(), middle);
		snake.addPoint(mp2b);
		ug.draw(snake);

		// final Snake small = new Snake(color, Arrows.asToDown());
		// small.addPoint(mp2b.getX(), middle);
		// small.addPoint(mp2b);
		// ug.draw(small);

	}

}
