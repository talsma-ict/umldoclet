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
package net.sourceforge.plantuml.timingdiagram;

import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class ChangeState implements Comparable<ChangeState> {

	private final TimeTick when;
	private final String[] states;
	private final String comment;
	private final Colors colors;

	public ChangeState(TimeTick when, String comment, Colors colors, String... states) {
		if (states.length == 0) {
			throw new IllegalArgumentException();
		}
		this.when = when;
		this.states = states;
		this.comment = comment;
		this.colors = colors;
	}

	public int compareTo(ChangeState other) {
		return this.when.compareTo(other.when);
	}

	public final TimeTick getWhen() {
		return when;
	}

	public final String[] getStates() {
		return states;
	}

	public final String getState() {
		return states[0];
	}

	public String getComment() {
		return comment;
	}

	public final HColor getBackColor() {
		if (colors == null || colors.getColor(ColorType.BACK) == null) {
			return HColorUtils.COL_D7E0F2;
		}
		return colors.getColor(ColorType.BACK);
	}

	private final HColor getLineColor() {
		if (colors == null || colors.getColor(ColorType.LINE) == null) {
			return HColorUtils.COL_038048;
		}
		return colors.getColor(ColorType.LINE);
	}

	public SymbolContext getContext() {
		return new SymbolContext(getBackColor(), getLineColor()).withStroke(new UStroke(1.5));
	}

	public final boolean isBlank() {
		return states[0].equals("{...}");
	}

	public final boolean isCompletelyHidden() {
		return states[0].equals("{hidden}");
	}

	public final boolean isFlat() {
		return states[0].equals("{-}");
	}

	// public final boolean isUnknown() {
	// return states[0].equals("{?}");
	// }

}
