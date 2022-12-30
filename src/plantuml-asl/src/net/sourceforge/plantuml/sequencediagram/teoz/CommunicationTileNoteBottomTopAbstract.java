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
package net.sourceforge.plantuml.sequencediagram.teoz;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.AbstractMessage;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.Note;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public abstract class CommunicationTileNoteBottomTopAbstract extends AbstractTile {

	protected final Tile tile;
	protected final AbstractMessage message;
	protected final Rose skin;
	protected final ISkinParam skinParam;
	protected final Note noteOnMessage;
	private final YGauge yGauge;

	final public Event getEvent() {
		return message;
	}

	@Override
	final public double getContactPointRelative() {
		return tile.getContactPointRelative();
	}

	public CommunicationTileNoteBottomTopAbstract(Tile tile, AbstractMessage message, Rose skin, ISkinParam skinParam,
			Note noteOnMessage, YGauge currentY) {
		super(((AbstractTile) tile).getStringBounder(), currentY);
		this.tile = tile;
		this.message = message;
		this.skin = skin;
		this.skinParam = skinParam;
		this.noteOnMessage = noteOnMessage;
		this.yGauge = YGauge.create(currentY.getMax(), getPreferredHeight());
	}

	@Override
	public YGauge getYGauge() {
		return yGauge;
	}


	@Override
	final protected void callbackY_internal(TimeHook y) {
		super.callbackY_internal(y);
		tile.callbackY(y);
	}

	final protected Component getComponent(StringBounder stringBounder) {
		final Component comp = skin.createComponentNote(noteOnMessage.getUsedStyles(), ComponentType.NOTE,
				noteOnMessage.getSkinParamBackcolored(skinParam), noteOnMessage.getStrings(),
				noteOnMessage.getColors());
		return comp;
	}

	final protected Real getNotePosition(StringBounder stringBounder) {
		final Real minX = tile.getMinX();
		return minX;
	}

	protected final double spacey = 10;

	protected final void drawLine(UGraphic ug, double x1, double y1, double x2, double y2) {

		final Style style = StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		final HColor color = style.value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());

		final double dx = x2 - x1;
		final double dy = y2 - y1;

		ug.apply(new UTranslate(x1, y1)).apply(color).apply(new UStroke(2, 2, 1)).draw(new ULine(dx, dy));

	}

	public double getPreferredHeight() {
		final Component comp = getComponent(getStringBounder());
		final XDimension2D dim = comp.getPreferredDimension(getStringBounder());
		return tile.getPreferredHeight() + dim.getHeight() + spacey;
	}

	public void addConstraints() {
		tile.addConstraints();
	}

	public Real getMinX() {
		return tile.getMinX();
	}

	public Real getMaxX() {
		return tile.getMaxX();
	}

}