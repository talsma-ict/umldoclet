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
package net.sourceforge.plantuml.skin;

import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.style.Styleable;

public enum ComponentType implements Styleable {

	ARROW,

	ACTOR_HEAD, ACTOR_TAIL,

	BOUNDARY_HEAD, BOUNDARY_TAIL, CONTROL_HEAD, CONTROL_TAIL, ENTITY_HEAD, ENTITY_TAIL, QUEUE_HEAD, QUEUE_TAIL,
	DATABASE_HEAD, DATABASE_TAIL, COLLECTIONS_HEAD, COLLECTIONS_TAIL,

	//
	ALIVE_BOX_CLOSE_CLOSE, ALIVE_BOX_CLOSE_OPEN, ALIVE_BOX_OPEN_CLOSE, ALIVE_BOX_OPEN_OPEN,

	DELAY_TEXT, DESTROY,

	DELAY_LINE, PARTICIPANT_LINE, CONTINUE_LINE,

	//
	GROUPING_ELSE, GROUPING_HEADER, GROUPING_SPACE,
	//
	NEWPAGE, NOTE, NOTE_HEXAGONAL, NOTE_BOX, DIVIDER, REFERENCE, ENGLOBER,

	//
	PARTICIPANT_HEAD, PARTICIPANT_TAIL

	//
	/* TITLE, SIGNATURE */;

	public boolean isArrow() {
		return this == ARROW;
	}

	public StyleSignatureBasic getStyleSignature() {
		if (this == PARTICIPANT_HEAD || this == PARTICIPANT_TAIL)
			return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.participant);

		if (this == PARTICIPANT_LINE || this == CONTINUE_LINE)
			return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.lifeLine);

		if (this == ALIVE_BOX_CLOSE_CLOSE || this == ALIVE_BOX_CLOSE_OPEN || this == ALIVE_BOX_OPEN_CLOSE
				|| this == ALIVE_BOX_OPEN_OPEN)
			return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.lifeLine);

		if (this == DESTROY)
			return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.lifeLine);

		if (this == DIVIDER)
			return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.separator);

		if (this == ENGLOBER)
			return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.box);

		if (this == NOTE)
			return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.note);

		if (this == DELAY_TEXT)
			return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.delay);

		if (this == DELAY_LINE)
			return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.delay);

//		if (this == REFERENCE) {
//			return StyleSignature.of(SName.root, SName.element,
//					SName.sequenceDiagram, SName.reference);
//		}
		throw new UnsupportedOperationException(toString());
	}
}
