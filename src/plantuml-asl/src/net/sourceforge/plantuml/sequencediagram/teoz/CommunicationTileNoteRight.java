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
package net.sourceforge.plantuml.sequencediagram.teoz;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.AbstractMessage;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.Note;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class CommunicationTileNoteRight extends AbstractTile {

	private final Tile tile;
	private final AbstractMessage message;
	private final Rose skin;
	private final ISkinParam skinParam;
	private final LivingSpace livingSpace;
	private final Note noteOnMessage;

	public Event getEvent() {
		return message;
	}

	private boolean isCreate() {
		return message.isCreate();
	}

	@Override
	public double getContactPointRelative() {
		return tile.getContactPointRelative();
	}

	public CommunicationTileNoteRight(Tile tile, AbstractMessage message, Rose skin, ISkinParam skinParam,
			LivingSpace livingSpace, Note noteOnMessage) {
		super(((AbstractTile) tile).getStringBounder());
		this.tile = tile;
		this.message = message;
		this.skin = skin;
		this.skinParam = skinParam;
		this.noteOnMessage = noteOnMessage;
		this.livingSpace = livingSpace;
	}

	@Override
	public void callbackY_internal(double y) {
		tile.callbackY(y);
	}

	private Component getComponent(StringBounder stringBounder) {
		final Component comp = skin.createComponent(noteOnMessage.getUsedStyles(), ComponentType.NOTE, null,
				noteOnMessage.getSkinParamBackcolored(skinParam), noteOnMessage.getStrings());
		return comp;
	}

	private Real getNotePosition(StringBounder stringBounder) {
		// final Component comp = getComponent(stringBounder);
		// final Dimension2D dim = comp.getPreferredDimension(stringBounder);
		if (isCreate()) {
			return livingSpace.getPosD(stringBounder);
		}
		final int level = livingSpace.getLevelAt(this, EventsHistoryMode.IGNORE_FUTURE_DEACTIVATE);
		return livingSpace.getPosC(stringBounder).addFixed(level * CommunicationTile.LIVE_DELTA_SIZE);
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Component comp = getComponent(stringBounder);
		final Dimension2D dim = comp.getPreferredDimension(stringBounder);
		final Area area = new Area(dim.getWidth(), dim.getHeight());
		tile.drawU(ug);
		final Real p = getNotePosition(stringBounder);

		comp.drawU(ug.apply(UTranslate.dx(p.getCurrentValue())), area, (Context2D) ug);
	}

	public double getPreferredHeight() {
		final Component comp = getComponent(getStringBounder());
		final Dimension2D dim = comp.getPreferredDimension(getStringBounder());
		return Math.max(tile.getPreferredHeight(), dim.getHeight());
	}

	public void addConstraints() {
		tile.addConstraints();
	}

	public Real getMinX() {
		return tile.getMinX();
	}

	public Real getMaxX() {
		final Component comp = getComponent(getStringBounder());
		final Dimension2D dim = comp.getPreferredDimension(getStringBounder());
		return getNotePosition(getStringBounder()).addFixed(dim.getWidth());
	}

}
