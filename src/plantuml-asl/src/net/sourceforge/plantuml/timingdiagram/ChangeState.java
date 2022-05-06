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
package net.sourceforge.plantuml.timingdiagram;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class ChangeState implements Comparable<ChangeState> {

	private final TimeTick when;
	private final String[] states;
	private final String comment;
	private final Colors colors;

	public ChangeState(TimeTick when, String comment, Colors colors, String... states) {
		if (states.length == 0)
			throw new IllegalArgumentException();

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

	public final HColor getBackColor(ISkinParam skinParam, Style style) {
		if (colors == null || colors.getColor(ColorType.BACK) == null)
			return style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet());

		return colors.getColor(ColorType.BACK);
	}

	private final HColor getLineColor(ISkinParam skinParam, Style style) {
		if (colors == null || colors.getColor(ColorType.LINE) == null)
			return style.value(PName.LineColor).asColor(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet());

		return colors.getColor(ColorType.LINE);
	}

	private UStroke getStroke(Style style) {
		return style.getStroke();
	}

	public SymbolContext getContext(ISkinParam skinParam, Style style) {
		return new SymbolContext(getBackColor(skinParam, style), getLineColor(skinParam, style))
				.withStroke(getStroke(style));
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
