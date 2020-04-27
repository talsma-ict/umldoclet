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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.InGroupable;
import net.sourceforge.plantuml.sequencediagram.MessageExo;
import net.sourceforge.plantuml.sequencediagram.MessageExoType;
import net.sourceforge.plantuml.sequencediagram.Note;
import net.sourceforge.plantuml.skin.ArrowComponent;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;

class Step1MessageExo extends Step1Abstract {

	private final MessageExoArrow messageArrow;

	Step1MessageExo(ParticipantRange range, StringBounder stringBounder, MessageExo message, DrawableSet drawingSet,
			Frontier freeY) {
		super(range, stringBounder, message, drawingSet, freeY);

		setConfig(getArrowType(message));

		final ArrowComponent comp = drawingSet.getSkin().createComponentArrow(message.getUsedStyles(), getConfig(),
				drawingSet.getSkinParam(), message.getLabelNumbered());
		this.messageArrow = new MessageExoArrow(freeY.getFreeY(range), drawingSet.getSkin(), comp,
				getLivingParticipantBox(), message.getType(), message.getUrl(), message.isShortArrow(),
				message.getArrowConfiguration());

		final List<Note> noteOnMessages = message.getNoteOnMessages();
		for (Note noteOnMessage : noteOnMessages) {
			final ISkinParam skinParam = noteOnMessage.getSkinParamBackcolored(drawingSet.getSkinParam());
			addNote(drawingSet.getSkin().createComponent(noteOnMessage.getUsedStyles(),
					ComponentType.NOTE, null, skinParam, noteOnMessage.getStrings()));
		}

	}

	Frontier prepareMessage(ConstraintSet constraintSet, InGroupablesStack inGroupablesStack) {
		final Arrow graphic = createArrow();
		final double arrowYStartLevel = graphic.getArrowYStartLevel(getStringBounder());
		final double arrowYEndLevel = graphic.getArrowYEndLevel(getStringBounder());

		getMessage().setPosYstartLevel(arrowYStartLevel);

		final double length = graphic.getArrowOnlyWidth(getStringBounder());
		incFreeY(graphic.getPreferredHeight(getStringBounder()));
		double marginActivateAndDeactive = 0;
		if (getMessage().isActivateAndDeactive()) {
			marginActivateAndDeactive = 30;
			incFreeY(marginActivateAndDeactive);
		}
		getDrawingSet().addEvent(getMessage(), graphic);

		final LivingParticipantBox livingParticipantBox = getLivingParticipantBox();
		if (messageArrow.getType().isRightBorder()) {
			constraintSet.getConstraint(livingParticipantBox.getParticipantBox(), constraintSet.getLastborder())
					.ensureValue(length);
		} else {
			constraintSet.getConstraint(constraintSet.getFirstBorder(), livingParticipantBox.getParticipantBox())
					.ensureValue(length);
		}

		final double posYendLevel = arrowYEndLevel + marginActivateAndDeactive;
		getMessage().setPosYendLevel(posYendLevel);

		assert graphic instanceof InGroupable;
		if (graphic instanceof InGroupable) {
			inGroupablesStack.addElement((InGroupable) graphic);
			inGroupablesStack.addElement(livingParticipantBox);
		}

		return getFreeY();
	}

	private LivingParticipantBox getLivingParticipantBox() {
		return getDrawingSet().getLivingParticipantBox(((MessageExo) getMessage()).getParticipant());
	}

	private Arrow createArrow() {
		if (getMessage().getNoteOnMessages().size() == 0) {
			return messageArrow;
		}
		final List<NoteBox> noteBoxes = new ArrayList<NoteBox>();
		for (int i = 0; i < getNotes().size(); i++) {
			final Component note = getNotes().get(i);
			final Note noteOnMessage = getMessage().getNoteOnMessages().get(i);
			noteBoxes.add(createNoteBox(getStringBounder(), messageArrow, note, noteOnMessage));
		}
		return new ArrowAndNoteBox(getStringBounder(), messageArrow, noteBoxes);
	}

	private ArrowConfiguration getArrowType(MessageExo m) {
		final MessageExoType type = m.getType();
		ArrowConfiguration result = null;

		if (type.getDirection() == 1) {
			result = m.getArrowConfiguration();
		} else {
			result = m.getArrowConfiguration().reverse();
		}
		result = result.withDecoration1(m.getArrowConfiguration().getDecoration1());
		result = result.withDecoration2(m.getArrowConfiguration().getDecoration2());
		return result;
		// ArrowConfiguration result = null;
		// if (type.getDirection() == 1) {
		// result = ArrowConfiguration.withDirectionNormal();
		// } else {
		// result = ArrowConfiguration.withDirectionReverse();
		// }
		// if (m.getArrowConfiguration().isDotted()) {
		// result = result.withDotted();
		// }
		// if (m.getArrowConfiguration().isAsync()) {
		// result = result.withHead(ArrowHead.ASYNC);
		// }
		// result = result.withPart(m.getArrowConfiguration().getPart());
		// return result;
	}

}
