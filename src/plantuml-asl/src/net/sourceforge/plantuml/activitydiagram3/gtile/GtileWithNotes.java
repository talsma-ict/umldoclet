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
package net.sourceforge.plantuml.activitydiagram3.gtile;

import java.util.Collection;
import java.util.Set;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.activitydiagram3.PositionedNote;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBlock1;
import net.sourceforge.plantuml.creole.SheetBlock2;
import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.image.Opale;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.utils.MathUtils;

public class GtileWithNotes extends AbstractGtile {

	private final Gtile tile;

	private TextBlock left;
	private TextBlock right;

	private final double suppSpace = 20;

	public StyleSignatureBasic getDefaultStyleDefinition() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.note);
	}

	@Override
	protected UTranslate getCoordImpl(String name) {
		if (name.equals(GPoint.NORTH_HOOK))
			return tile.getCoord(name).compose(getTranslate(getStringBounder()));
		if (name.equals(GPoint.SOUTH_HOOK))
			return tile.getCoord(name).compose(getTranslate(getStringBounder()));
		return super.getCoordImpl(name);
	}

	@Override
	public Swimlane getSwimlane(String point) {
		return tile.getSwimlane(point);
	}

	@Override
	public Set<Swimlane> getSwimlanes() {
		return tile.getSwimlanes();
	}

	public GtileWithNotes(Gtile tile, Collection<PositionedNote> notes, ISkinParam skinParam) {
		super(tile.getStringBounder(), tile.skinParam());
		this.tile = tile;

		for (PositionedNote note : notes) {
			ISkinParam skinParam2 = skinParam;
			if (note.getColors() != null)
				skinParam2 = note.getColors().mute(skinParam2);

			final Style style = getDefaultStyleDefinition().getMergedStyle(skinParam.getCurrentStyleBuilder())
					.eventuallyOverride(note.getColors());
			final HColor noteBackgroundColor = style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(),
					getIHtmlColorSet());
			final HColor borderColor = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(),
					getIHtmlColorSet());
			final FontConfiguration fc = style.getFontConfiguration(skinParam.getThemeStyle(), getIHtmlColorSet());
			final double shadowing = style.value(PName.Shadowing).asDouble();
			final LineBreakStrategy wrapWidth = style.wrapWidth();
			final UStroke stroke = style.getStroke();

			final Sheet sheet = Parser
					.build(fc, skinParam.getDefaultTextAlignment(HorizontalAlignment.LEFT), skinParam, CreoleMode.FULL)
					.createSheet(note.getDisplay());
			final SheetBlock1 sheet1 = new SheetBlock1(sheet, wrapWidth, skinParam.getPadding());
			final SheetBlock2 sheet2 = new SheetBlock2(sheet1, new Stencil() {
				// -6 and 15 value comes from Opale: this is very ugly!
				public double getStartingX(StringBounder stringBounder, double y) {
					return -6;
				}

				public double getEndingX(StringBounder stringBounder, double y) {
					return sheet1.getEndingX(stringBounder, y) + 15;
				}
			}, stroke);

			final Opale opale = new Opale(shadowing, borderColor, noteBackgroundColor, sheet2, false, stroke);
			final TextBlock opaleMarged = TextBlockUtils.withMargin(opale, 10, 10);
			if (note.getNotePosition() == NotePosition.LEFT) {
				if (left == null) {
					left = opaleMarged;
				} else {
					left = TextBlockUtils.mergeTB(left, opaleMarged, HorizontalAlignment.CENTER);
				}
			} else {
				if (right == null) {
					right = opaleMarged;
				} else {
					right = TextBlockUtils.mergeTB(right, opaleMarged, HorizontalAlignment.CENTER);
				}
			}
		}

		if (left == null) {
			left = TextBlockUtils.empty(0, 0);
		}
		if (right == null) {
			right = TextBlockUtils.empty(0, 0);
		}

	}

	private UTranslate getTranslate(StringBounder stringBounder) {
		final Dimension2D dimTotal = calculateDimension(stringBounder);
		final Dimension2D dimTile = tile.calculateDimension(stringBounder);
		final double xDelta = left.calculateDimension(stringBounder).getWidth();
		final double yDelta = (dimTotal.getHeight() - dimTile.getHeight()) / 2;
		return new UTranslate(xDelta, yDelta);
	}

	private UTranslate getTranslateForLeft(StringBounder stringBounder) {
		final Dimension2D dimTotal = calculateDimension(stringBounder);
		final Dimension2D dimLeft = left.calculateDimension(stringBounder);
		final double xDelta = 0;
		final double yDelta = (dimTotal.getHeight() - dimLeft.getHeight()) / 2;
		return new UTranslate(xDelta, yDelta);
	}

	private UTranslate getTranslateForRight(StringBounder stringBounder) {
		final Dimension2D dimTotal = calculateDimension(stringBounder);
		final Dimension2D dimRight = right.calculateDimension(stringBounder);
		final double xDelta = dimTotal.getWidth() - dimRight.getWidth();
		final double yDelta = (dimTotal.getHeight() - dimRight.getHeight()) / 2;
		return new UTranslate(xDelta, yDelta);
	}

	@Override
	protected void drawUInternal(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		left.drawU(ug.apply(getTranslateForLeft(stringBounder)));
		right.drawU(ug.apply(getTranslateForRight(stringBounder)));
		tile.drawU(ug.apply(getTranslate(stringBounder)));
	}

//	@Override
//	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
//		final Dimension2D dimTotal = calculateDimensionInternal(stringBounder);
//		final FtileGeometry orig = tile.calculateDimension(stringBounder);
//		final UTranslate translate = getTranslate(stringBounder);
//		if (orig.hasPointOut()) {
//			return new FtileGeometry(dimTotal, orig.getLeft() + translate.getDx(), orig.getInY() + translate.getDy(),
//					orig.getOutY() + translate.getDy());
//		}
//		return new FtileGeometry(dimTotal, orig.getLeft() + translate.getDx(), orig.getInY() + translate.getDy());
//	}

	@Override
	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dimTile = tile.calculateDimension(stringBounder);
		final Dimension2D dimLeft = left.calculateDimension(stringBounder);
		final Dimension2D dimRight = right.calculateDimension(stringBounder);
		final double height = MathUtils.max(dimLeft.getHeight(), dimRight.getHeight(), dimTile.getHeight());
		return new Dimension2DDouble(dimTile.getWidth() + dimLeft.getWidth() + dimRight.getWidth(), height);
	}

}
