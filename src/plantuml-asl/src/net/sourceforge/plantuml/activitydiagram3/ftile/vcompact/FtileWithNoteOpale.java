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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.plantuml.AlignmentParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.activitydiagram3.PositionedNote;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
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
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteType;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.style.Styleable;
import net.sourceforge.plantuml.svek.image.Opale;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class FtileWithNoteOpale extends AbstractFtile implements Stencil, Styleable {

	private final Ftile tile;
	private final Opale opale;
	private final VerticalAlignment verticalAlignment;

	private final NotePosition notePosition;
	private final double suppSpace = 20;
	private final Swimlane swimlaneNote;

	public StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.note);
	}

	public Set<Swimlane> getSwimlanes() {
		if (swimlaneNote != null) {
			final Set<Swimlane> result = new HashSet<>(tile.getSwimlanes());
			result.add(swimlaneNote);
			return Collections.unmodifiableSet(result);
		}
		return tile.getSwimlanes();
	}

	public Swimlane getSwimlaneIn() {
		return tile.getSwimlaneIn();
	}

	public Swimlane getSwimlaneOut() {
		return tile.getSwimlaneOut();
	}

	@Override
	public Collection<Ftile> getMyChildren() {
		return Collections.singleton(tile);
	}

	public static Ftile create(Ftile tile, Collection<PositionedNote> notes, ISkinParam skinParam, boolean withLink,
			VerticalAlignment verticalAlignment) {
		if (notes.size() > 1)
			return new FtileWithNotes(tile, notes, skinParam, verticalAlignment);

		if (notes.size() == 0)
			throw new IllegalArgumentException();

		return new FtileWithNoteOpale(tile, notes.iterator().next(), skinParam, withLink, verticalAlignment);
	}

	private FtileWithNoteOpale(Ftile tile, PositionedNote note, ISkinParam skinParam, boolean withLink,
			VerticalAlignment verticalAlignment) {
		super(tile.skinParam());
		this.verticalAlignment = verticalAlignment;
		this.swimlaneNote = note.getSwimlaneNote();
		if (note.getColors() != null)
			skinParam = note.getColors().mute(skinParam);

		this.tile = tile;
		this.notePosition = note.getNotePosition();
		if (note.getType() == NoteType.FLOATING_NOTE)
			withLink = false;

		final Style style = getStyleSignature().getMergedStyle(skinParam.getCurrentStyleBuilder())
				.eventuallyOverride(note.getColors());
		final HColor noteBackgroundColor = style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(),
				getIHtmlColorSet());
		final HColor borderColor = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(), getIHtmlColorSet());
		final FontConfiguration fc = style.getFontConfiguration(skinParam.getThemeStyle(), getIHtmlColorSet());
		final double shadowing = style.value(PName.Shadowing).asDouble();
		final LineBreakStrategy wrapWidth = style.wrapWidth();
		final UStroke stroke = style.getStroke();

		final HorizontalAlignment align = skinParam.getHorizontalAlignment(AlignmentParam.noteTextAlignment, null,
				false, null);
		final Sheet sheet = Parser.build(fc, align, skinParam, CreoleMode.FULL).createSheet(note.getDisplay());
		final TextBlock text = new SheetBlock2(new SheetBlock1(sheet, wrapWidth, skinParam.getPadding()), this, stroke);
		opale = new Opale(shadowing, borderColor, noteBackgroundColor, text, withLink, stroke);

	}

	private UTranslate getTranslate(StringBounder stringBounder) {
		final Dimension2D dimTotal = calculateDimensionInternal(stringBounder);
		final Dimension2D dimNote = opale.calculateDimension(stringBounder);
		final Dimension2D dimTile = tile.calculateDimension(stringBounder);
		final double yForFtile = (dimTotal.getHeight() - dimTile.getHeight()) / 2;
		final double marge;
		if (notePosition == NotePosition.LEFT)
			marge = dimNote.getWidth() + suppSpace;
		else
			marge = 0;

		return new UTranslate(marge, yForFtile);
	}

	@Override
	public UTranslate getTranslateFor(Ftile child, StringBounder stringBounder) {
		if (child == tile)
			return new UTranslate();
		return super.getTranslateFor(child, stringBounder);

	}

	private UTranslate getTranslateForOpale(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);
		final Dimension2D dimNote = opale.calculateDimension(stringBounder);

		final double yForNote;
		if (verticalAlignment == VerticalAlignment.CENTER)
			yForNote = (dimTotal.getHeight() - dimNote.getHeight()) / 2;
		else
			yForNote = 0;

		if (notePosition == NotePosition.LEFT)
			return UTranslate.dy(yForNote);

		final double dx = dimTotal.getWidth() - dimNote.getWidth();
		return new UTranslate(dx, yForNote);
	}

	public void drawU(UGraphic ug) {
		final Swimlane intoSw;
		if (ug instanceof UGraphicInterceptorOneSwimlane)
			intoSw = ((UGraphicInterceptorOneSwimlane) ug).getSwimlane();
		else
			intoSw = null;

		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimNote = opale.calculateDimension(stringBounder);

		if (notePosition == NotePosition.LEFT) {
			final Direction strategy = Direction.RIGHT;
			final Point2D pp1 = new Point2D.Double(dimNote.getWidth(), dimNote.getHeight() / 2);
			final Point2D pp2 = new Point2D.Double(dimNote.getWidth() + suppSpace, dimNote.getHeight() / 2);
			opale.setOpale(strategy, pp1, pp2);
		} else {
			final Direction strategy = Direction.LEFT;
			final Point2D pp1 = new Point2D.Double(0, dimNote.getHeight() / 2);
			final Point2D pp2 = new Point2D.Double(-suppSpace, dimNote.getHeight() / 2);
			opale.setOpale(strategy, pp1, pp2);
		}

		if (ug instanceof UGraphicInterceptorOneSwimlane == false || swimlaneNote == null || intoSw == swimlaneNote)
			opale.drawU(ug.apply(getTranslateForOpale(ug)));

		ug.apply(getTranslate(stringBounder)).draw(tile);
	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		final Dimension2D dimTotal = calculateDimensionInternal(stringBounder);
		final FtileGeometry orig = tile.calculateDimension(stringBounder);
		final UTranslate translate = getTranslate(stringBounder);
		if (orig.hasPointOut())
			return new FtileGeometry(dimTotal, orig.getLeft() + translate.getDx(), orig.getInY() + translate.getDy(),
					orig.getOutY() + translate.getDy());

		return new FtileGeometry(dimTotal, orig.getLeft() + translate.getDx(), orig.getInY() + translate.getDy());
	}

	private Dimension2D calculateDimensionInternal(StringBounder stringBounder) {
		final Dimension2D dimNote = opale.calculateDimension(stringBounder);
		final Dimension2D dimTile = tile.calculateDimension(stringBounder);
		final double height = Math.max(dimNote.getHeight(), dimTile.getHeight());
		return new Dimension2DDouble(dimTile.getWidth() + 1 * dimNote.getWidth() + suppSpace, height);
	}

	public double getStartingX(StringBounder stringBounder, double y) {
		return -opale.getMarginX1();
	}

	public double getEndingX(StringBounder stringBounder, double y) {
		return opale.calculateDimension(stringBounder).getWidth() - opale.getMarginX1();
	}

}
