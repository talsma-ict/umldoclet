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
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class Reference extends AbstractEvent implements Event {

	private final List<Participant> participants;
	private final Url url;
	private final HColor backColorGeneral;
	private final HColor backColorElement;

	private final Display strings;

	final private Style style;
	final private Style styleHeader;

	public StyleSignatureBasic getDefaultStyleDefinition() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.reference);
	}

	private StyleSignatureBasic getHeaderStyleDefinition() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.sequenceDiagram, SName.referenceHeader);
	}

	public Style[] getUsedStyles() {
		return new Style[] { style, styleHeader == null ? styleHeader
				: styleHeader.eventuallyOverride(PName.BackGroundColor, backColorElement) };
	}

	public Reference(List<Participant> participants, Url url, Display strings, HColor backColorGeneral,
			HColor backColorElement, StyleBuilder styleBuilder) {
		this.participants = participants;
		this.url = url;
		this.strings = strings;
		this.backColorGeneral = backColorGeneral;
		this.backColorElement = backColorElement;
		this.style = getDefaultStyleDefinition().getMergedStyle(styleBuilder);
		this.styleHeader = getHeaderStyleDefinition().getMergedStyle(styleBuilder);
	}

	public List<Participant> getParticipant() {
		return Collections.unmodifiableList(participants);
	}

	public Display getStrings() {
		return strings;
	}

	public boolean dealWith(Participant someone) {
		return participants.contains(someone);
	}

	public final Url getUrl() {
		return url;
	}

	public boolean hasUrl() {
		return url != null;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final Iterator<Participant> it = participants.iterator(); it.hasNext();) {
			sb.append(it.next().getCode());
			if (it.hasNext())
				sb.append("-");

		}
		return sb.toString();
	}

	public final HColor getBackColorGeneral() {
		return backColorGeneral;
	}

	public final HColor getBackColorElement() {
		return backColorElement;
	}
}
