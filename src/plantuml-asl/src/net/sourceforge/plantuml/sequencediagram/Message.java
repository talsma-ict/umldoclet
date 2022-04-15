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

import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.style.StyleBuilder;

public final class Message extends AbstractMessage {

	final private Participant p1;
	final private Participant p2;

	public Message(StyleBuilder styleBuilder, Participant p1, Participant p2, Display label,
			ArrowConfiguration arrowConfiguration, String messageNumber) {
		super(styleBuilder, label, arrowConfiguration, messageNumber);
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public String toString() {
		return super.toString() + " " + p1 + "->" + p2 + " " + getLabel();
	}

	@Override
	public Participant getParticipant1() {
		return p1;
	}

	@Override
	public Participant getParticipant2() {
		return p2;
	}

	public boolean dealWith(Participant someone) {
		return someone == p1 || someone == p2;
	}

	@Override
	public boolean compatibleForCreate(Participant p) {
		return p1 != p && p2 == p;
	}

	public boolean isSelfMessage() {
		return p1 == p2;
	}

	private List<Participant> multicast = Collections.emptyList();

	public void setMulticast(List<Participant> multicast) {
		this.multicast = multicast;
	}

	public List<Participant> getMulticast() {
		return multicast;
	}

}
