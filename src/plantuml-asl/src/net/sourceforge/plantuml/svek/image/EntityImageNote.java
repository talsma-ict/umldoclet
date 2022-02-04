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
package net.sourceforge.plantuml.svek.image;

import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Objects;

import net.sourceforge.plantuml.AlignmentParam;
import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.CornerParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineParam;
import net.sourceforge.plantuml.SkinParamBackcolored;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.cucadiagram.BodyFactory;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockEmpty;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.svek.SvekLine;
import net.sourceforge.plantuml.svek.SvekNode;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicStencil;
import net.sourceforge.plantuml.ugraphic.UGroupType;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class EntityImageNote extends AbstractEntityImage implements Stencil {

	private final HColor noteBackgroundColor;
	private final HColor borderColor;
	private final double shadowing;
	private final int marginX1 = 6;
	private final int marginX2 = 15;
	private final int marginY = 5;
	private final boolean withShadow;
	private final ISkinParam skinParam;

	private final TextBlock textBlock;

	public EntityImageNote(ILeaf entity, ISkinParam skinParam, UmlDiagramType umlDiagramType) {
		super(entity, getSkin(getISkinParam(skinParam, entity), entity));
		this.skinParam = getISkinParam(skinParam, entity);

		this.withShadow = getSkinParam().shadowing(getEntity().getStereotype());
		final Display strings = entity.getDisplay();

		final Rose rose = new Rose();

		final FontConfiguration fontConfiguration;
		final HorizontalAlignment horizontalAlignment;
		if (UseStyle.useBetaStyle()) {
			final Style style = getDefaultStyleDefinition(umlDiagramType.getStyleName())
					.getMergedStyle(skinParam.getCurrentStyleBuilder());
			if (entity.getColors().getColor(ColorType.BACK) == null) {
				this.noteBackgroundColor = style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(),
						skinParam.getIHtmlColorSet());
			} else {
				this.noteBackgroundColor = entity.getColors().getColor(ColorType.BACK);
			}
			this.borderColor = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(),
					skinParam.getIHtmlColorSet());
			this.shadowing = style.value(PName.Shadowing).asDouble();

			fontConfiguration = style.getFontConfiguration(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet());
			horizontalAlignment = style.getHorizontalAlignment();
		} else {
			this.shadowing = skinParam.shadowing(getEntity().getStereotype()) ? 4 : 0;
			if (entity.getColors().getColor(ColorType.BACK) == null) {
				this.noteBackgroundColor = rose.getHtmlColor(getSkinParam(), ColorParam.noteBackground);
			} else {
				this.noteBackgroundColor = entity.getColors().getColor(ColorType.BACK);
			}
			this.borderColor = SkinParamUtils.getColor(getSkinParam(), null, ColorParam.noteBorder);

			fontConfiguration = new FontConfiguration(getSkinParam(), FontParam.NOTE, null);
			horizontalAlignment = skinParam.getHorizontalAlignment(AlignmentParam.noteTextAlignment, null, false, null);
		}

		if (strings.size() == 1 && strings.get(0).length() == 0) {
			textBlock = new TextBlockEmpty();
		} else {
			textBlock = BodyFactory.create3(strings, FontParam.NOTE, getSkinParam(), horizontalAlignment,
					fontConfiguration, getSkinParam().wrapWidth());
		}
	}

	private static ISkinParam getISkinParam(ISkinParam skinParam, IEntity entity) {
		if (entity.getColors() != null) {
			return entity.getColors().mute(skinParam);
		}
		return skinParam;
	}

	static ISkinParam getSkin(ISkinParam skinParam, IEntity entity) {
		final Stereotype stereotype = entity.getStereotype();
		HColor back = entity.getColors().getColor(ColorType.BACK);
		if (back != null) {
			return new SkinParamBackcolored(skinParam, back);
		}
		back = getColorStatic(skinParam, ColorParam.noteBackground, stereotype);
		if (back != null) {
			return new SkinParamBackcolored(skinParam, back);
		}
		return skinParam;
	}

	private static HColor getColorStatic(ISkinParam skinParam, ColorParam colorParam, Stereotype stereo) {
		final Rose rose = new Rose();
		return rose.getHtmlColor(skinParam, stereo, colorParam);
	}

	final public double getPreferredWidth(StringBounder stringBounder) {
		final double result = getTextWidth(stringBounder);
		return result;
	}

	final public double getPreferredHeight(StringBounder stringBounder) {
		return getTextHeight(stringBounder);
	}

	private Dimension2D getSize(StringBounder stringBounder, final TextBlock textBlock) {
		return textBlock.calculateDimension(stringBounder);
	}

	final protected double getTextHeight(StringBounder stringBounder) {
		final TextBlock textBlock = getTextBlock();
		final Dimension2D size = getSize(stringBounder, textBlock);
		return size.getHeight() + 2 * marginY;
	}

	final protected TextBlock getTextBlock() {
		return textBlock;
	}

	final protected double getPureTextWidth(StringBounder stringBounder) {
		final TextBlock textBlock = getTextBlock();
		final Dimension2D size = getSize(stringBounder, textBlock);
		return size.getWidth();
	}

	final public double getTextWidth(StringBounder stringBounder) {
		return getPureTextWidth(stringBounder) + marginX1 + marginX2;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final double height = getPreferredHeight(stringBounder);
		final double width = getPreferredWidth(stringBounder);
		return new Dimension2DDouble(width, height);
	}

	private StyleSignature getDefaultStyleDefinition(SName sname) {
		return StyleSignature.of(SName.root, SName.element, sname, SName.note);
	}

	final public void drawU(UGraphic ug) {
		final Url url = getEntity().getUrl99();

		ug.startGroup(UGroupType.CLASS, "elem " + getEntity().getCode() + " selected");

		if (url != null) {
			ug.startUrl(url);
		}
		final UGraphic ug2 = UGraphicStencil.create(ug, this, new UStroke());
		if (opaleLine == null || opaleLine.isOpale() == false) {
			drawNormal(ug2);
		} else {
			final StringBounder stringBounder = ug.getStringBounder();
			DotPath path = opaleLine.getDotPath();
			path.moveSvek(-node.getMinX(), -node.getMinY());
			Point2D p1 = path.getStartPoint();
			Point2D p2 = path.getEndPoint();
			final double textWidth = getTextWidth(stringBounder);
			final double textHeight = getTextHeight(stringBounder);
			final Point2D center = new Point2D.Double(textWidth / 2, textHeight / 2);
			if (p1.distance(center) > p2.distance(center)) {
				path = path.reverse();
				p1 = path.getStartPoint();
				// p2 = path.getEndPoint();
			}
			final Direction strategy = getOpaleStrategy(textWidth, textHeight, p1);
			final Point2D pp1 = path.getStartPoint();
			final Point2D pp2 = path.getEndPoint();
			final Point2D newRefpp2 = move(pp2, node.getMinX(), node.getMinY());
			final Point2D projection = move(other.projection(newRefpp2, stringBounder), -node.getMinX(),
					-node.getMinY());
			final Opale opale = new Opale(shadowing, borderColor, noteBackgroundColor, textBlock, true);
			opale.setRoundCorner(getRoundCorner());
			opale.setOpale(strategy, pp1, projection);
			final UGraphic stroked = applyStroke(ug2);
			opale.drawU(Colors.applyStroke(stroked, getEntity().getColors()));
		}
		if (url != null) {
			ug.closeUrl();
		}

		ug.closeGroup();
	}

	private double getRoundCorner() {
		return skinParam.getRoundCorner(CornerParam.DEFAULT, null);
	}

	private static Point2D move(Point2D pt, double dx, double dy) {
		return new Point2D.Double(pt.getX() + dx, pt.getY() + dy);
	}

	private void drawNormal(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final UPath polygon = Opale.getPolygonNormal(getTextWidth(stringBounder), getTextHeight(stringBounder),
				getRoundCorner());
		if (withShadow) {
			polygon.setDeltaShadow(4);
		}
		ug = ug.apply(noteBackgroundColor.bg()).apply(borderColor);
		final UGraphic stroked = applyStroke(ug);
		stroked.draw(polygon);
		ug.draw(Opale.getCorner(getTextWidth(stringBounder), getRoundCorner()));

		getTextBlock().drawU(ug.apply(new UTranslate(marginX1, marginY)));
	}

	private UGraphic applyStroke(UGraphic ug) {
		final UStroke stroke = skinParam.getThickness(LineParam.noteBorder, null);
		if (stroke == null) {
			return ug;
		}
		return ug.apply(stroke);
	}

	private Direction getOpaleStrategy(double width, double height, Point2D pt) {
		final double d1 = getOrthoDistance(new Line2D.Double(width, 0, width, height), pt);
		final double d2 = getOrthoDistance(new Line2D.Double(0, height, width, height), pt);
		final double d3 = getOrthoDistance(new Line2D.Double(0, 0, 0, height), pt);
		final double d4 = getOrthoDistance(new Line2D.Double(0, 0, width, 0), pt);
		if (d3 <= d1 && d3 <= d2 && d3 <= d4) {
			return Direction.LEFT;
		}
		if (d1 <= d2 && d1 <= d3 && d1 <= d4) {
			return Direction.RIGHT;
		}
		if (d4 <= d1 && d4 <= d2 && d4 <= d3) {
			return Direction.UP;
		}
		if (d2 <= d1 && d2 <= d3 && d2 <= d4) {
			return Direction.DOWN;
		}
		return null;

	}

	private static double getOrthoDistance(Line2D.Double seg, Point2D pt) {
		if (isHorizontal(seg)) {
			return Math.abs(seg.getP1().getY() - pt.getY());
		}
		if (isVertical(seg)) {
			return Math.abs(seg.getP1().getX() - pt.getX());
		}
		throw new IllegalArgumentException();
	}

	private static boolean isHorizontal(Line2D.Double seg) {
		return seg.getP1().getY() == seg.getP2().getY();
	}

	private static boolean isVertical(Line2D.Double seg) {
		return seg.getP1().getX() == seg.getP2().getX();
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	private SvekLine opaleLine;
	private SvekNode node;
	private SvekNode other;

	public void setOpaleLine(SvekLine line, SvekNode node, SvekNode other) {
		this.opaleLine = line;
		this.node = node;
		this.other = Objects.requireNonNull(other);
	}

	public double getStartingX(StringBounder stringBounder, double y) {
		return 0;
	}

	public double getEndingX(StringBounder stringBounder, double y) {
		return calculateDimension(stringBounder).getWidth();
	}

}
