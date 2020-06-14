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
package net.sourceforge.plantuml.timingdiagram;

import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.cucadiagram.WithLinkType;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;

public class TimeMessage extends WithLinkType {

	private final TickInPlayer tickInPlayer1;
	private final TickInPlayer tickInPlayer2;
	private final Display label;

	public TimeMessage(TickInPlayer tickInPlayer1, TickInPlayer tickInPlayer2, String label) {
		this.tickInPlayer1 = tickInPlayer1;
		this.tickInPlayer2 = tickInPlayer2;
		this.label = Display.getWithNewlines(label);
		this.setSpecificColor(HtmlColorUtils.BLUE);
		this.type = new LinkType(LinkDecor.NONE, LinkDecor.NONE);
	}

	public final Player getPlayer1() {
		return tickInPlayer1.getPlayer();
	}

	public final Player getPlayer2() {
		return tickInPlayer2.getPlayer();
	}

	public final TimeTick getTick1() {
		return tickInPlayer1.getTick();
	}

	public final TimeTick getTick2() {
		return tickInPlayer2.getTick();
	}

	public final Display getLabel() {
		return label;
	}

	@Override
	public void goNorank() {
		// Nothing to do
	}

}
