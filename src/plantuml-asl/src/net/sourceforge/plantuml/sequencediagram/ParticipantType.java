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
package net.sourceforge.plantuml.sequencediagram;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.style.Styleable;

public enum ParticipantType implements Styleable {
	PARTICIPANT(ColorParam.participantBackground), //
	ACTOR(ColorParam.actorBackground), //
	BOUNDARY(ColorParam.boundaryBackground), //
	CONTROL(ColorParam.controlBackground), //
	ENTITY(ColorParam.entityBackground), //
	QUEUE(ColorParam.queueBackground), //
	DATABASE(ColorParam.databaseBackground), //
	COLLECTIONS(ColorParam.collectionsBackground);

	private final ColorParam background;

	private ParticipantType(ColorParam background) {
		this.background = background;
	}

	public ColorParam getBackgroundColorParam() {
		return background;
	}

	public StyleSignatureBasic getStyleSignature() {
		if (this == PARTICIPANT) {
			return StyleSignatureBasic.of(SName.root, SName.element,
					SName.sequenceDiagram, SName.participant);
		}
		if (this == ACTOR) {
			return StyleSignatureBasic.of(SName.root, SName.element,
					SName.sequenceDiagram, SName.actor);
		}
		if (this == BOUNDARY) {
			return StyleSignatureBasic.of(SName.root, SName.element,
					SName.sequenceDiagram, SName.boundary);
		}
		if (this == CONTROL) {
			return StyleSignatureBasic.of(SName.root, SName.element,
					SName.sequenceDiagram, SName.control);
		}
		if (this == ENTITY) {
			return StyleSignatureBasic.of(SName.root, SName.element,
					SName.sequenceDiagram, SName.entity);
		}
		if (this == QUEUE) {
			return StyleSignatureBasic.of(SName.root, SName.element,
					SName.sequenceDiagram, SName.queue);
		}
		if (this == DATABASE) {
			return StyleSignatureBasic.of(SName.root, SName.element,
					SName.sequenceDiagram, SName.database);
		}
		if (this == COLLECTIONS) {
			return StyleSignatureBasic.of(SName.root, SName.element,
					SName.sequenceDiagram, SName.collections);
		}
		return null;
	}

}
