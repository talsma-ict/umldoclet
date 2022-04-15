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
package net.sourceforge.plantuml.png;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.DisplaySection;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public class PngTitler {

	private final DisplaySection text;

	private final Style style;
	private final HColorSet set;
	private final ISkinSimple spriteContainer;

	public PngTitler(DisplaySection text, Style style, HColorSet set, ISkinSimple spriteContainer) {
		this.style = style;
		this.set = set;
		this.spriteContainer = spriteContainer;
		this.text = text;

	}

	public Dimension2D getTextDimension(StringBounder stringBounder) {
		final TextBlock textBloc = getRibbonBlock();
		if (textBloc == null)
			return null;

		return textBloc.calculateDimension(stringBounder);
	}

	public TextBlock getRibbonBlock() {
		final Display display = text.getDisplay();
		if (display == null)
			return null;

		return style.createTextBlockBordered(display, set, spriteContainer);
	}
}
