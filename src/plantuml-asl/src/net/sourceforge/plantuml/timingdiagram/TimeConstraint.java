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
package net.sourceforge.plantuml.timingdiagram;

import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class TimeConstraint {

	private final TimeTick tick1;
	private final TimeTick tick2;
	private final Display label;
	private final ISkinParam skinParam;
	private final StyleBuilder styleBuilder;
	private final ArrowConfiguration config;
	private final double marginx;

	public TimeConstraint(double marginx, TimeTick tick1, TimeTick tick2, String label, ISkinParam skinParam,
			ArrowConfiguration config) {
		this.marginx = marginx;
		this.tick1 = Objects.requireNonNull(tick1);
		this.tick2 = Objects.requireNonNull(tick2);
		this.label = Display.getWithNewlines(label);
		this.skinParam = skinParam;
		this.styleBuilder = skinParam.getCurrentStyleBuilder();
		this.config = config;
	}

	public final boolean containsStrict(TimeTick other) {
		return tick1.compareTo(other) < 0 && tick2.compareTo(other) > 0;
	}

	public final TimeTick getTick1() {
		return tick1;
	}

	public final TimeTick getTick2() {
		return tick2;
	}

	public final Display getLabel() {
		return label;
	}

	private TextBlock getTextBlock(Display display) {
		return display.create(getFontConfiguration(), HorizontalAlignment.LEFT, skinParam);
	}

	private FontConfiguration getFontConfiguration() {
		return getStyle().getFontConfiguration(skinParam.getIHtmlColorSet());
	}

	public void drawU(UGraphic ug, TimingRuler ruler) {
		final HColor arrowColor = getArrowColor();
		ug = ug.apply(arrowColor).apply(arrowColor.bg());
		final double x1 = ruler.getPosInPixel(tick1) + marginx;
		final double x2 = ruler.getPosInPixel(tick2) - marginx;
		ug = ug.apply(UTranslate.dx(x1));
		final double len = x2 - x1;
		ug.apply(getUStroke()).draw(ULine.hline(len));

		if (len > 10) {
			ug.draw(getPolygon(Direction.LEFT, new XPoint2D(0, 0)));
			ug.draw(getPolygon(Direction.RIGHT, new XPoint2D(len, 0)));
		} else {
			ug.draw(getPolygon(Direction.RIGHT, new XPoint2D(0, 0)));
			ug.draw(getPolygon(Direction.LEFT, new XPoint2D(len, 0)));
		}

		final TextBlock text = getTextBlock(label);
		final XDimension2D dimText = text.calculateDimension(ug.getStringBounder());
		final double x = (len - dimText.getWidth()) / 2;
		text.drawU(ug.apply(new UTranslate(x, -getConstraintHeight(ug.getStringBounder()))));
	}

	private HColor getArrowColor() {
		final HColor configColor = config.getColor();
		if (configColor != null)
			return configColor;
		return getStyle().value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());
	}

	private Style getStyle() {
		return getStyleSignature().getMergedStyle(styleBuilder);
	}

	private UStroke getUStroke() {
		return getStyle().getStroke();
	}

	private StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.timingDiagram, SName.constraintArrow);
	}

	public double getConstraintHeight(StringBounder stringBounder) {
		final TextBlock text = getTextBlock(label);
		final XDimension2D dimText = text.calculateDimension(stringBounder);
		return dimText.getHeight() + getTopMargin();

	}

	public static double getTopMargin() {
		return 5;
	}

	private UPolygon getPolygon(Direction dir, XPoint2D end) {
		final double dx = 8;
		final double dy = 4;
		final XPoint2D pt1;
		final XPoint2D pt2;
		if (dir == Direction.RIGHT) {
			pt1 = end.move(-dx, dy);
			pt2 = end.move(-dx, -dy);
		} else {
			pt1 = end.move(dx, dy);
			pt2 = end.move(dx, -dy);
		}

		final UPolygon polygon = new UPolygon();
		polygon.addPoint(pt1);
		polygon.addPoint(pt2);
		polygon.addPoint(end);

		return polygon;
	}

	public static double getHeightForConstraints(StringBounder stringBounder, List<TimeConstraint> constraints) {
		if (constraints.size() == 0)
			return 0;

		double result = 0;
		for (TimeConstraint constraint : constraints)
			result = Math.max(result, constraint.getConstraintHeight(stringBounder));

		return result;
	}

}
