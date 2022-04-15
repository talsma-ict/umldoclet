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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.AbstractMessage;
import net.sourceforge.plantuml.sequencediagram.Note;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.Component;

abstract class Step1Abstract {

	private final StringBounder stringBounder;

	private final DrawableSet drawingSet;

	private final AbstractMessage message;

	private Frontier freeY2;

	private ArrowConfiguration config;

	private final List<Component> notes = new ArrayList<>();

	private ParticipantRange range;

	Step1Abstract(ParticipantRange range, StringBounder stringBounder, AbstractMessage message, DrawableSet drawingSet,
			Frontier freeY2) {
		this.range = range;
		this.stringBounder = stringBounder;
		this.message = message;
		this.freeY2 = Objects.requireNonNull(freeY2);
		this.drawingSet = drawingSet;
	}

	protected final ParticipantRange getParticipantRange() {
		return range;
	}

	abstract Frontier prepareMessage(ConstraintSet constraintSet, InGroupablesStack groupingStructures);

	protected final ArrowConfiguration getConfig() {
		return config;
	}

	protected final void setConfig(ArrowConfiguration config) {
		this.config = config.withThickness(drawingSet.getArrowThickness());
	}

	protected final List<Component> getNotes() {
		return notes;
	}

	protected final void addNote(Component note) {
		this.notes.add(note);
	}

	protected final StringBounder getStringBounder() {
		return stringBounder;
	}

	protected final AbstractMessage getMessage() {
		return message;
	}

	protected final DrawableSet getDrawingSet() {
		return drawingSet;
	}

	protected final Frontier getFreeY() {
		return freeY2;
	}

	protected final void incFreeY(double v) {
		freeY2 = freeY2.add(v, range);
	}

	protected final NoteBox createNoteBox(StringBounder stringBounder, Arrow arrow, Component noteComp,
			Note noteOnMessage) {
		final LivingParticipantBox p = arrow.getParticipantAt(stringBounder, noteOnMessage.getPosition());
		final NoteBox noteBox = new NoteBox(arrow.getStartingY(), noteComp, p, null, noteOnMessage.getPosition(),
				noteOnMessage.getUrl());

		if (arrow instanceof MessageSelfArrow && noteOnMessage.getPosition() == NotePosition.RIGHT) {
			noteBox.pushToRight(arrow.getPreferredWidth(stringBounder));
		}
		// if (arrow instanceof MessageExoArrow) {
		// final MessageExoType type = ((MessageExoArrow) arrow).getType();
		// if (type.isRightBorder()) {
		// final double width = noteBox.getPreferredWidth(stringBounder);
		// noteBox.pushToRight(-width);
		// }
		// }

		return noteBox;
	}

}
