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
package net.sourceforge.plantuml.mindmap;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamColors;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileBoxOld;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class FingerImpl implements Finger, UDrawable {

	private final Display label;
	private final HColor backColor;
	private final String stereotype;
	private final ISkinParam skinParam;
	private final StyleBuilder styleBuilder;
	private final IdeaShape shape;
	private final Direction direction;
	private final int level;
	private boolean drawPhalanx = true;
	private double marginLeft = 10;
	private double marginRight = 10;
	private double marginTop = 10;
	private double marginBottom = 10;

	private final List<FingerImpl> nail = new ArrayList<>();
	private Tetris tetris = null;

	private StyleSignature getDefaultStyleDefinitionNode() {
		final String depth = SName.depth(level);
		if (level == 0) {
			return StyleSignature.of(SName.root, SName.element, SName.mindmapDiagram, SName.node, SName.rootNode)
					.add(stereotype).add(depth);
		}
		if (shape == IdeaShape.NONE && nail.size() == 0) {
			return StyleSignature
					.of(SName.root, SName.element, SName.mindmapDiagram, SName.node, SName.leafNode, SName.boxless)
					.add(stereotype).add(depth);
		}
		if (shape == IdeaShape.NONE) {
			return StyleSignature.of(SName.root, SName.element, SName.mindmapDiagram, SName.node, SName.boxless)
					.add(stereotype).add(depth);
		}
		if (nail.size() == 0) {
			return StyleSignature.of(SName.root, SName.element, SName.mindmapDiagram, SName.node, SName.leafNode)
					.add(stereotype).add(depth);
		}
		return StyleSignature.of(SName.root, SName.element, SName.mindmapDiagram, SName.node).add(stereotype)
				.add(depth);
	}

	public StyleSignature getDefaultStyleDefinitionArrow() {
		final String depth = SName.depth(level);
		return StyleSignature.of(SName.root, SName.element, SName.mindmapDiagram, SName.arrow).add(stereotype)
				.add(depth);
	}

	public static FingerImpl build(Idea idea, ISkinParam skinParam, Direction direction) {
		final FingerImpl result = new FingerImpl(idea.getStyleBuilder(), idea.getBackColor(), idea.getLabel(),
				skinParam, idea.getShape(), direction, idea.getLevel(), idea.getStereotype());
		for (Idea child : idea.getChildren()) {
			result.addInNail(build(child, skinParam, direction));
		}
		// System.err.println("End of build for " + idea);
		return result;
	}

	public void addInNail(FingerImpl child) {
		nail.add(child);
	}

	private FingerImpl(StyleBuilder styleBuilder, HColor backColor, Display label, ISkinParam skinParam,
			IdeaShape shape, Direction direction, int level, String stereotype) {
		this.backColor = backColor;
		this.stereotype = stereotype;
		this.level = level;
		this.label = label;
		this.skinParam = skinParam;
		this.shape = shape;
		this.styleBuilder = styleBuilder;
		this.direction = direction;
		final Style styleNode = getDefaultStyleDefinitionNode().getMergedStyle(styleBuilder);
		this.marginLeft = styleNode.getMargin().getLeft();
		this.marginRight = styleNode.getMargin().getRight();
		this.marginTop = styleNode.getMargin().getTop();
		this.marginBottom = styleNode.getMargin().getBottom();
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
		final Point2D p1 = new Point2D.Double(
				direction == Direction.RIGHT ? dimPhalanx.getWidth() : -dimPhalanx.getWidth(), 0);

		for (int i = 0; i < nail.size(); i++) {
			final FingerImpl child = nail.get(i);
			final SymetricalTeePositioned stp = tetris(stringBounder).getElements().get(i);
			final double x = direction == Direction.RIGHT ? dimPhalanx.getWidth() + getX12()
					: -dimPhalanx.getWidth() - getX12();
			final Point2D p2 = new Point2D.Double(x, stp.getY());
			child.drawU(ug.apply(new UTranslate(p2)));
			drawLine(ug.apply(getLinkColor()).apply(getUStroke()), p1, p2);
		}

	}

	private HColor getLinkColor() {
		final Style styleArrow = getDefaultStyleDefinitionArrow().getMergedStyle(styleBuilder);
		return styleArrow.value(PName.LineColor).asColor(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet());
	}

	private UStroke getUStroke() {
		final Style styleArrow = getDefaultStyleDefinitionArrow().getMergedStyle(styleBuilder);
		return styleArrow.getStroke();
	}

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

	private Tetris tetris(StringBounder stringBounder) {
		if (tetris == null) {
			tetris = new Tetris(label.toString());
			for (FingerImpl child : nail) {
				tetris.add(child.asSymetricalTee(stringBounder));
			}
			tetris.balance();
		}
		return tetris;
	}

	private SymetricalTee asSymetricalTee(StringBounder stringBounder) {
		final double thickness1 = getPhalanxThickness(stringBounder);
		final double elongation1 = getPhalanxElongation(stringBounder);
		if (nail.size() == 0) {
			return new SymetricalTee(thickness1, elongation1, 0, 0);
		}
		final double thickness2 = getNailThickness(stringBounder);
		final double elongation2 = getNailElongation(stringBounder);
		return new SymetricalTee(thickness1, elongation1 + getX1(), thickness2, getX2() + elongation2);
	}

	private double getX1() {
		return marginLeft;
	}

	private double getX2() {
		return marginRight + 30;
	}

	public double getX12() {
		return getX1() + getX2();
	}

	public double getPhalanxThickness(StringBounder stringBounder) {
		return getPhalanx().calculateDimension(stringBounder).getHeight();
	}

	public double getPhalanxElongation(StringBounder stringBounder) {
		return getPhalanx().calculateDimension(stringBounder).getWidth();
	}

	private TextBlock getPhalanx() {
		if (drawPhalanx == false) {
			return TextBlockUtils.empty(0, 0);
		}
		if (shape == IdeaShape.BOX) {
			final ISkinParam foo = new SkinParamColors(skinParam, Colors.empty().add(ColorType.BACK, backColor));
			final TextBlock box = FtileBoxOld.createMindMap(styleBuilder, foo, label, getDefaultStyleDefinitionNode());
			return TextBlockUtils.withMargin(box, 0, 0, marginTop, marginBottom);
		}

		assert shape == IdeaShape.NONE;
		final Style styleNode = getDefaultStyleDefinitionNode().getMergedStyle(styleBuilder);
		final TextBlock text = label.create0(
				styleNode.getFontConfiguration(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet()),
				styleNode.getHorizontalAlignment(), skinParam, styleNode.wrapWidth(), CreoleMode.FULL, null, null);
		if (direction == Direction.RIGHT) {
			return TextBlockUtils.withMargin(text, 3, 0, 1, 1);
		}
		return TextBlockUtils.withMargin(text, 0, 3, 1, 1);
	}

	public double getNailThickness(StringBounder stringBounder) {
		return tetris(stringBounder).getHeight();
	}

	public double getNailElongation(StringBounder stringBounder) {
		return tetris(stringBounder).getWidth();
	}

	public double getFullThickness(StringBounder stringBounder) {
		final double thickness1 = getPhalanxThickness(stringBounder);
		final double thickness2 = getNailThickness(stringBounder);
		// System.err.println("thickness1=" + thickness1 + " thickness2=" + thickness2);
		return Math.max(thickness1, thickness2);
	}

	public double getFullElongation(StringBounder stringBounder) {
		return getPhalanxElongation(stringBounder) + getNailElongation(stringBounder);
	}

	public void doNotDrawFirstPhalanx() {
		this.drawPhalanx = false;
	}

}
