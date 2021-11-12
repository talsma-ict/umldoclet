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
package net.sourceforge.plantuml.activitydiagram3.ftile.vertical;

import java.awt.geom.Dimension2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.SkinParamColors;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBlock1;
import net.sourceforge.plantuml.creole.SheetBlock2;
import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.ugraphic.UEmpty;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;

public class FtileBox2 extends AbstractFtile {

	private final ClockwiseTopRightBottomLeft padding;
	private final ClockwiseTopRightBottomLeft margin;

	private final TextBlock tb;
	private double roundCorner = 25;
	private final double shadowing;
	private final HorizontalAlignment horizontalAlignment;
	private double minimumWidth = 0;

	private final LinkRendering inRendering;
	private final Swimlane swimlane;
	private final BoxStyle boxStyle;

	private final HColor borderColor;
	private final HColor backColor;
	private final Style style;

	static public StyleSignature getDefaultStyleDefinitionActivity() {
		return StyleSignature.of(SName.root, SName.element, SName.activityDiagram, SName.activity);
	}

	static public StyleSignature getDefaultStyleDefinitionArrow() {
		return StyleSignature.of(SName.root, SName.element, SName.activityDiagram, SName.arrow);
	}

	final public LinkRendering getInLinkRendering() {
		return inRendering;
	}

	public Set<Swimlane> getSwimlanes() {
		if (swimlane == null) {
			return Collections.emptySet();
		}
		return Collections.singleton(swimlane);
	}

	public Swimlane getSwimlaneIn() {
		return swimlane;
	}

	public Swimlane getSwimlaneOut() {
		return swimlane;
	}

	class MyStencil implements Stencil {

		public double getStartingX(StringBounder stringBounder, double y) {
			return -padding.getLeft();
		}

		public double getEndingX(StringBounder stringBounder, double y) {
			final Dimension2D dim = calculateDimension(stringBounder);
			return dim.getWidth() - padding.getRight();
		}

	}

	public static FtileBox2 create(ISkinParam skinParam, Display label, Swimlane swimlane, BoxStyle boxStyle,
			Stereotype stereotype) {
		Style style = null;
		Style styleArrow = null;
		if (UseStyle.useBetaStyle()) {
			style = getDefaultStyleDefinitionActivity().with(stereotype)
					.getMergedStyle(skinParam.getCurrentStyleBuilder());
			styleArrow = getDefaultStyleDefinitionArrow().getMergedStyle(skinParam.getCurrentStyleBuilder());
		}
		return new FtileBox2(skinParam, label, swimlane, boxStyle, style, styleArrow);
	}

	private FtileBox2(ISkinParam skinParam, Display label, Swimlane swimlane, BoxStyle boxStyle, Style style,
			Style styleArrow) {
		super(skinParam);
		this.style = style;
		this.boxStyle = boxStyle;
		this.swimlane = swimlane;
		final FontConfiguration fc;
		final LineBreakStrategy wrapWidth;
		if (UseStyle.useBetaStyle()) {
			this.inRendering = new LinkRendering(
					Rainbow.build(styleArrow, getIHtmlColorSet(), skinParam.getThemeStyle()));
			Colors specBack = null;
			if (skinParam instanceof SkinParamColors) {
				specBack = ((SkinParamColors) skinParam).getColors();
			}
			style = style.eventuallyOverride(specBack);
			this.borderColor = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(), getIHtmlColorSet());
			this.backColor = style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(), getIHtmlColorSet());
			fc = style.getFontConfiguration(skinParam.getThemeStyle(), getIHtmlColorSet());
			this.horizontalAlignment = style.getHorizontalAlignment();
			this.padding = style.getPadding();
			this.margin = style.getMargin();
			this.roundCorner = style.value(PName.RoundCorner).asDouble();
			this.shadowing = style.value(PName.Shadowing).asDouble();
			wrapWidth = style.wrapWidth();
			this.minimumWidth = style.value(PName.MinimumWidth).asDouble();
		} else {
			this.padding = ClockwiseTopRightBottomLeft.same(10);
			this.margin = ClockwiseTopRightBottomLeft.same(0);
			this.inRendering = new LinkRendering(Rainbow.build(skinParam));
			this.borderColor = SkinParamUtils.getColor(skinParam(), null, ColorParam.activityBorder);
			this.backColor = SkinParamUtils.getColor(skinParam(), null, ColorParam.activityBackground);
			fc = new FontConfiguration(skinParam, FontParam.ACTIVITY, null);
			this.horizontalAlignment = HorizontalAlignment.LEFT;
			this.shadowing = skinParam().shadowing(null) ? 3.0 : 0.0;
			wrapWidth = skinParam.wrapWidth();

		}
		final Sheet sheet = Parser
				.build(fc, skinParam.getDefaultTextAlignment(horizontalAlignment), skinParam, CreoleMode.FULL)
				.createSheet(label);
		this.tb = new SheetBlock2(new SheetBlock1(sheet, wrapWidth, skinParam.getPadding()), new MyStencil(),
				new UStroke(1));
		this.print = label.toString();

	}

	final private String print;

	@Override
	public String toString() {
		return print;
	}

	public void drawU(UGraphic ug) {
		final Dimension2D dimRaw = getDimRaw(ug.getStringBounder());
		final double widthTotal = dimRaw.getWidth();
		final double heightTotal = dimRaw.getHeight();
		final UDrawable shape = boxStyle.getUDrawable(widthTotal, heightTotal, shadowing, roundCorner);

		final Dimension2D dimTotal = calculateDimension(ug.getStringBounder());
		ug.draw(new UEmpty(dimTotal));

		final UStroke thickness;
		if (UseStyle.useBetaStyle()) {
			thickness = style.getStroke();
		} else {
			thickness = getThickness();
		}

		if (borderColor == null) {
			ug = ug.apply(new HColorNone());
		} else {
			ug = ug.apply(borderColor);
		}
		if (backColor == null) {
			ug = ug.apply(new HColorNone().bg());
		} else {
			ug = ug.apply(backColor.bg());
		}

		ug = ug.apply(thickness);
		ug = ug.apply(new UTranslate(margin.getLeft(), margin.getTop()));
		shape.drawU(ug);

		if (horizontalAlignment == HorizontalAlignment.LEFT) {
			tb.drawU(ug.apply(new UTranslate(padding.getLeft(), padding.getTop())));
		} else if (horizontalAlignment == HorizontalAlignment.RIGHT) {
			final Dimension2D dimTb = tb.calculateDimension(ug.getStringBounder());
			tb.drawU(ug.apply(
					new UTranslate(dimRaw.getWidth() - dimTb.getWidth() - padding.getRight(), padding.getBottom())));
		} else if (horizontalAlignment == HorizontalAlignment.CENTER) {
			final Dimension2D dimTb = tb.calculateDimension(ug.getStringBounder());
			tb.drawU(ug.apply(new UTranslate((dimRaw.getWidth() - dimTb.getWidth()) / 2, padding.getBottom())));
		}
	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		Dimension2D dimRaw = getDimRaw(stringBounder);
		return new FtileGeometry(dimRaw.getWidth() + margin.getLeft() + margin.getRight(),
				dimRaw.getHeight() + margin.getTop() + margin.getBottom(), margin.getLeft() + dimRaw.getWidth() / 2,
				margin.getTop(), margin.getTop() + dimRaw.getHeight());
	}

	private Dimension2D getDimRaw(StringBounder stringBounder) {
		Dimension2D dimRaw = tb.calculateDimension(stringBounder);
		dimRaw = Dimension2DDouble.delta(dimRaw, padding.getLeft() + padding.getRight() + boxStyle.getShield(),
				padding.getBottom() + padding.getTop());
		dimRaw = Dimension2DDouble.atLeast(dimRaw, minimumWidth, 0);
		return dimRaw;
	}

	public Collection<Ftile> getMyChildren() {
		return Collections.emptyList();
	}

}
