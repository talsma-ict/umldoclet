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

import java.util.Set;

import net.sourceforge.plantuml.AlignmentParam;
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

public class GtileWithNoteOpale extends AbstractGtile implements Stencil, Styleable {

	private final Gtile tile;
	private final Opale opale;

	private final NotePosition notePosition;
	private final double suppSpace = 20;
	private final Swimlane swimlaneNote;

	private final UTranslate positionNote;
	private final UTranslate positionTile;

	private final Dimension2D dimNote;
	private final Dimension2D dimTile;

	public StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.note);
	}

	@Override
	public Swimlane getSwimlane(String point) {
		return tile.getSwimlane(point);
	}

	@Override
	public Set<Swimlane> getSwimlanes() {
		return tile.getSwimlanes();
	}

	public GtileWithNoteOpale(Gtile tile, PositionedNote note, ISkinParam skinParam, boolean withLink) {
		super(tile.getStringBounder(), tile.skinParam());
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
		final TextBlock text = new SheetBlock2(new SheetBlock1(sheet, wrapWidth, skinParam.getPadding()), this,
				new UStroke(1));
		this.opale = new Opale(shadowing, borderColor, noteBackgroundColor, text, withLink, stroke);

		this.dimNote = opale.calculateDimension(stringBounder);
		this.dimTile = tile.calculateDimension(stringBounder);

		final Dimension2D dimTotal = calculateDimension(stringBounder);

		if (note.getNotePosition() == NotePosition.LEFT) {
			this.positionNote = new UTranslate(0, (dimTotal.getHeight() - dimNote.getHeight()) / 2);
			this.positionTile = new UTranslate(dimNote.getWidth() + suppSpace,
					(dimTotal.getHeight() - dimTile.getHeight()) / 2);
		} else {
			this.positionNote = new UTranslate(dimTile.getWidth() + suppSpace,
					(dimTotal.getHeight() - dimNote.getHeight()) / 2);
			this.positionTile = new UTranslate(0, (dimTotal.getHeight() - dimTile.getHeight()) / 2);
		}
	}

	@Override
	protected UTranslate getCoordImpl(String name) {
		return tile.getCoord(name).compose(positionTile);
	}

	@Override
	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final double height = Math.max(dimNote.getHeight(), dimTile.getHeight());
		return new Dimension2DDouble(dimTile.getWidth() + dimNote.getWidth() + suppSpace, height);
	}

	@Override
	protected void drawUInternal(UGraphic ug) {
		opale.drawU(ug.apply(positionNote));
		tile.drawU(ug.apply(positionTile));
	}

	@Override
	public double getStartingX(StringBounder stringBounder, double y) {
		return -opale.getMarginX1();
	}

	@Override
	public double getEndingX(StringBounder stringBounder, double y) {
		return opale.calculateDimension(stringBounder).getWidth() - opale.getMarginX1();
	}

}
