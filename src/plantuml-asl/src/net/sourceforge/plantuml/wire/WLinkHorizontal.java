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
package net.sourceforge.plantuml.wire;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class WLinkHorizontal {

	private final UTranslate start;
	private final double destination;
	private final WLinkType type;
	private final WArrowDirection direction;
	private final HColor color;
	private final Display label;
	private final ISkinParam skinParam;

	public WLinkHorizontal(ISkinParam skinParam, UTranslate start, double destination, WLinkType type,
			WArrowDirection direction, HColor color, Display label) {
		this.start = start;
		this.destination = destination;
		this.skinParam = skinParam;
		this.direction = direction;
		this.type = type;
		this.label = label;
		this.color = color == null ? HColorUtils.BLACK : color;
	}

	private TextBlock getTextBlock() {
		final FontConfiguration fontConfiguration = FontConfiguration.blackBlueTrue(UFont.sansSerif(10))
				.changeColor(color);
		return label.create(fontConfiguration, HorizontalAlignment.LEFT, skinParam);
	}

	public void drawMe(UGraphic ug) {
		ug = ug.apply(color);
		final TextBlock textBlock = getTextBlock();
		final Dimension2D dimText = textBlock.calculateDimension(ug.getStringBounder());

		UGraphic ugText = ug.apply(start);
		final double len = destination - start.getDx();

		if (type == WLinkType.NORMAL) {
			ug = ug.apply(color.bg());
			drawNormalArrow(ug);

			ugText = ugText.apply(UTranslate.dy(-dimText.getHeight() / 2));

		} else if (type == WLinkType.BUS) {
			ug = ug.apply(HColorUtils.WHITE.bg());
			drawBusArrow(ug);
			ugText = ugText.apply(UTranslate.dy((20 - dimText.getHeight()) / 2 - 5));
		}

		if (dimText.getHeight() > 0) {
			switch (direction) {
			case NORMAL:
				ugText = ugText.apply(UTranslate.dx(4));
				break;
			case REVERSE:
				ugText = ugText.apply(UTranslate.dx(len - dimText.getWidth() - 4));
				break;
			default:
				ugText = ugText.apply(UTranslate.dx((len - dimText.getWidth()) / 2));
				break;
			}
			if (type == WLinkType.NORMAL) {
				ugText.apply(HColorUtils.WHITE).apply(HColorUtils.WHITE.bg()).draw(new URectangle(dimText));
			}
			textBlock.drawU(ugText);
		}

	}

	private void drawBusArrow(UGraphic ug) {
		final double dx = destination - start.getDx() - 2;
		final UPath path = new UPath();
		if (direction == WArrowDirection.NONE) {
			path.moveTo(0, 0);
			path.lineTo(dx, 0);
			path.lineTo(dx, 10);
			path.lineTo(0, 10);
			path.lineTo(0, 0);
			path.closePath();
			ug.apply(start.compose(UTranslate.dx(1))).draw(path);
		}
		if (direction == WArrowDirection.NORMAL) {
			path.moveTo(0, 0);
			path.lineTo(dx - 15, 0);
			path.lineTo(dx - 15, -5);
			path.lineTo(dx, 5);
			path.lineTo(dx - 15, 15);
			path.lineTo(dx - 15, 10);
			path.lineTo(0, 10);
			path.lineTo(0, 0);
			path.closePath();
			ug.apply(start.compose(UTranslate.dx(1))).draw(path);
		}
		if (direction == WArrowDirection.BOTH) {
			path.moveTo(0, 5);
			path.lineTo(15, -5);
			path.lineTo(15, 0);
			path.lineTo(dx - 15, 0);
			path.lineTo(dx - 15, -5);
			path.lineTo(dx, 5);
			path.lineTo(dx - 15, 15);
			path.lineTo(dx - 15, 10);
			path.lineTo(15, 10);
			path.lineTo(15, 15);
			path.lineTo(0, 5);
			path.closePath();
			ug.apply(start.compose(UTranslate.dx(1))).draw(path);
		}
		if (direction == WArrowDirection.REVERSE) {
			path.moveTo(0, 5);
			path.lineTo(15, -5);
			path.lineTo(15, 0);
			path.lineTo(dx, 0);
			path.lineTo(dx, 10);
			path.lineTo(15, 10);
			path.lineTo(15, 15);
			path.lineTo(0, 5);
			path.closePath();
			ug.apply(start.compose(UTranslate.dx(1))).draw(path);
		}
	}

	private void drawNormalArrow(UGraphic ug) {
		final double dx = destination - start.getDx() - 2;
		if (direction == WArrowDirection.BOTH || direction == WArrowDirection.NORMAL) {
			final UPath path = new UPath();
			path.moveTo(0, 0);
			path.lineTo(-5, -5);
			path.lineTo(-5, 5);
			path.lineTo(0, 0);
			path.closePath();
			ug.apply(start.compose(UTranslate.dx(dx))).draw(path);
		}
		if (direction == WArrowDirection.BOTH || direction == WArrowDirection.REVERSE) {
			final UPath path = new UPath();
			path.moveTo(0, 0);
			path.lineTo(5, -5);
			path.lineTo(5, 5);
			path.lineTo(0, 0);
			path.closePath();
			ug.apply(start.compose(UTranslate.dx(1))).draw(path);
		}
		ug.apply(start.compose(UTranslate.dx(1))).draw(ULine.hline(dx));
	}

}
