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
package net.sourceforge.plantuml.mindmap;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileBox;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class FingerImpl implements Finger, UDrawable {

	private final Display label;
	private final ISkinParam skinParam;
	private final IdeaShape shape;
	private final Direction direction;
	private boolean drawPhalanx = true;

	private final List<FingerImpl> nail = new ArrayList<FingerImpl>();

	public static FingerImpl build(Idea idea, ISkinParam skinParam, Direction direction) {
		final FingerImpl result = new FingerImpl(idea.getLabel(), skinParam, idea.getShape(), direction);
		for (Idea child : idea.getChildren()) {
			result.addInNail(build(child, skinParam, direction));
		}
		return result;
	}

	public void addInNail(FingerImpl child) {
		nail.add(child);
	}

	private FingerImpl(Display label, ISkinParam skinParam, IdeaShape shape, Direction direction) {
		this.label = label;
		this.skinParam = skinParam;
		this.shape = shape;
		this.direction = direction;
	}

	public double getPhalanxThickness(StringBounder stringBounder) {
		return getPhalanx().calculateDimension(stringBounder).getHeight();
	}

	public double getPhalanxElongation(StringBounder stringBounder) {
		return getPhalanx().calculateDimension(stringBounder).getWidth();
	}

	public double getNailThickness(StringBounder stringBounder) {
		double result = 0;
		for (FingerImpl child : nail) {
			result += child.getFullThickness(stringBounder);
		}
		return result;
	}

	public double getNailElongation(StringBounder stringBounder) {
		double result = 0;
		for (FingerImpl child : nail) {
			result = Math.max(result, child.getFullElongation(stringBounder));
		}
		return result;
	}

	public double getFullElongation(StringBounder stringBounder) {
		return getPhalanxElongation(stringBounder) + marginX1 + getNailElongation(stringBounder);
	}

	public double getFullThickness(StringBounder stringBounder) {
		return Math.max(getPhalanxThickness(stringBounder), getNailThickness(stringBounder));
	}

	public void doNotDrawFirstPhalanx() {
		this.drawPhalanx = false;
	}

	public void drawU(final UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final TextBlock phalanx = getPhalanx();
		final Dimension2D dimPhalanx = phalanx.calculateDimension(stringBounder);
		if (drawPhalanx) {
			final double posY = -getPhalanxThickness(stringBounder) / 2;
			final double posX = direction == Direction.RIGHT ? 0 : -dimPhalanx.getWidth();
			phalanx.drawU(ug.apply(new UTranslate(posX, posY)));
		}
		final Point2D p1 = new Point2D.Double(direction == Direction.RIGHT ? dimPhalanx.getWidth()
				: -dimPhalanx.getWidth(), 0);

		double y = -getFullThickness(stringBounder) / 2;
		for (FingerImpl child : nail) {
			final double childThickness = child.getFullThickness(stringBounder);
			final double x = direction == Direction.RIGHT ? dimPhalanx.getWidth() + marginX1 : -dimPhalanx.getWidth()
					- marginX1;
			child.drawU(ug.apply(new UTranslate(x, y + childThickness / 2)));
			final Point2D p2 = new Point2D.Double(x, y + childThickness / 2);
			drawLine(ug.apply(new UChangeColor(getLinkColor())), p1, p2);
			y += childThickness;
		}
	}

	private HtmlColor getLinkColor() {
		// return skinParam.getColors(ColorParam.activityBorder, null).getColor(ColorType.ARROW);
		return ColorParam.activityBorder.getDefaultValue();
		// return HtmlColorUtils.BLACK;
	}

	final private double marginX1 = 50;

	private void drawLine(UGraphic ug, Point2D p1, Point2D p2) {
		// final ULine line = new ULine(p1, p2);
		// ug.apply(new UTranslate(p1)).draw(line);
		final UPath path = new UPath();
		final double delta1 = direction == Direction.RIGHT ? 10 : -10;
		final double delta2 = direction == Direction.RIGHT ? 25 : -25;
		path.moveTo(p1);
		path.lineTo(p1.getX() + delta1, p1.getY());
		path.cubicTo(p1.getX() + delta2, p1.getY(), p2.getX() - delta2, p2.getY(), p2.getX() - delta1, p2.getY());
		path.lineTo(p2);
		ug.draw(path);
	}

	private TextBlock getPhalanx() {
		if (drawPhalanx == false) {
			return TextBlockUtils.empty(0, 0);
		}
		final UFont font = skinParam.getFont(null, false, FontParam.ACTIVITY);
		if (shape == IdeaShape.BOX) {
			final FtileBox box = new FtileBox(Colors.empty().mute(skinParam), label, font, null, BoxStyle.PLAIN);
			return TextBlockUtils.withMargin(box, 0, 10);
		}
		return TextBlockUtils.withMargin(
				label.create(FontConfiguration.blackBlueTrue(font), HorizontalAlignment.LEFT, skinParam), 5, 5);
	}

}
