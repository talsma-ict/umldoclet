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
package net.sourceforge.plantuml.cucadiagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.ThemeStyle;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public abstract class WithLinkType {

	protected LinkType type;
	protected boolean hidden = false;
	private boolean single = false;

	private Colors colors = Colors.empty();

	private List<Colors> supplementary = new ArrayList<>();

	public final HColor getSpecificColor() {
		return colors.getColor(ColorType.LINE);
	}

	public final void setSpecificColor(HColor specificColor) {
		setSpecificColor(specificColor, 0);
	}

	public final void setSpecificColor(HColor specificColor, int i) {
		if (i == 0) {
			colors = colors.add(ColorType.LINE, specificColor);
		} else {
			supplementary.add(colors.add(ColorType.LINE, specificColor));
		}
	}

	public List<Colors> getSupplementaryColors() {
		return Collections.unmodifiableList(supplementary);
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public final Colors getColors() {
		return colors;
	}

	final public void goDashed() {
		type = type.goDashed();
	}

	final public void goDotted() {
		type = type.goDotted();
	}

	final public void goThickness(double thickness) {
		type = type.goThickness(thickness);
	}

	final public void goHidden() {
		this.hidden = true;
	}

	public abstract void goNorank();

	final public void goBold() {
		type = type.goBold();
	}

	public final void goSingle() {
		this.single = true;
	}

	public boolean isSingle() {
		return single;
	}

	public void applyStyle(ThemeStyle themeStyle, String arrowStyle) {
		if (arrowStyle == null) {
			return;
		}
		final StringTokenizer st = new StringTokenizer(arrowStyle, ";");
		int i = 0;
		while (st.hasMoreTokens()) {
			final String s = st.nextToken();
			applyOneStyle(themeStyle, s, i);
			i++;
		}
	}

	private void applyOneStyle(ThemeStyle themeStyle, String arrowStyle, int i) {
		final StringTokenizer st = new StringTokenizer(arrowStyle, ",");
		while (st.hasMoreTokens()) {
			final String s = st.nextToken();
			if (s.equalsIgnoreCase("dashed")) {
				this.goDashed();
			} else if (s.equalsIgnoreCase("bold")) {
				this.goBold();
			} else if (s.equalsIgnoreCase("dotted")) {
				this.goDotted();
			} else if (s.equalsIgnoreCase("hidden")) {
				this.goHidden();
			} else if (s.equalsIgnoreCase("single")) {
				this.goSingle();
			} else if (s.equalsIgnoreCase("plain")) {
				// Do nothing
			} else if (s.equalsIgnoreCase("norank")) {
				this.goNorank();
			} else if (s.startsWith("thickness=")) {
				this.goThickness(Double.parseDouble(s.substring("thickness=".length())));
			} else {
				final HColor tmp = HColorSet.instance().getColorOrWhite(themeStyle, s);
				setSpecificColor(tmp, i);
			}
		}
	}

	public LinkType getType() {
		return type;
	}

}
