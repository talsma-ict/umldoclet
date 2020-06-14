/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleDefinition;

public class Reference extends AbstractEvent implements Event {

	private final List<Participant> participants;
	private final Url url;
	private final HtmlColor backColorGeneral;
	private final HtmlColor backColorElement;

	private final Display strings;

	final private Style style;

	public StyleDefinition getDefaultStyleDefinition() {
		return ComponentType.REFERENCE.getDefaultStyleDefinition();
	}

	public Style[] getUsedStyles() {
		return new Style[] { style };
	}

	public Reference(List<Participant> participants, Url url, Display strings, HtmlColor backColorGeneral,
			HtmlColor backColorElement, StyleBuilder styleBuilder) {
		this.participants = participants;
		this.url = url;
		this.strings = strings;
		this.backColorGeneral = backColorGeneral;
		this.backColorElement = backColorElement;
		this.style = getDefaultStyleDefinition().getMergedStyle(styleBuilder);
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
			if (it.hasNext()) {
				sb.append("-");
			}

		}
		return sb.toString();
	}

	public final HtmlColor getBackColorGeneral() {
		return backColorGeneral;
	}

	public final HtmlColor getBackColorElement() {
		return backColorElement;
	}
}
