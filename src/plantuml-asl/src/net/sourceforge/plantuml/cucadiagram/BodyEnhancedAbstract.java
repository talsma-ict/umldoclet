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
package net.sourceforge.plantuml.cucadiagram;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockLineBefore;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public abstract class BodyEnhancedAbstract extends AbstractTextBlock implements TextBlock {

	protected final HorizontalAlignment align;
	protected final FontConfiguration titleConfig;
	protected TextBlock area;
	private final Style style;

	BodyEnhancedAbstract(HorizontalAlignment align, FontConfiguration titleConfig, Style style) {
		this.align = align;
		this.titleConfig = titleConfig;
		this.style = style;
	}

	public static boolean isBlockSeparator(CharSequence cs) {
		final String s = cs.toString();
		if (s.startsWith("--") && s.endsWith("--"))
			return true;

		if (s.startsWith("==") && s.endsWith("=="))
			return true;

		if (s.startsWith("..") && s.endsWith("..") && s.equals("...") == false)
			return true;

		if (s.startsWith("__") && s.endsWith("__"))
			return true;

		return false;
	}

	public final XDimension2D calculateDimension(StringBounder stringBounder) {
		return getArea(stringBounder).calculateDimension(stringBounder);
	}

	final public void drawU(UGraphic ug) {
		getArea(ug.getStringBounder()).drawU(ug);
	}

	final protected TextBlock getTitle(String s, ISkinSimple spriteContainer) {
		if (s.length() <= 4)
			return null;

		s = StringUtils.trin(s.substring(2, s.length() - 2));
		return Display.getWithNewlines(s).create(titleConfig, HorizontalAlignment.LEFT, spriteContainer);
	}

	abstract protected TextBlock getArea(StringBounder stringBounder);

	abstract protected double getMarginX();

	final protected TextBlock decorate(StringBounder stringBounder, TextBlock b, char separator, TextBlock title) {
		final double marginX = getMarginX();
		if (separator == 0)
			return TextBlockUtils.withMargin(b, marginX, 0);

		if (title == null)
			return new TextBlockLineBefore(getDefaultThickness(), TextBlockUtils.withMargin(b, marginX, 4), separator);

		final XDimension2D dimTitle = title.calculateDimension(stringBounder);
		final TextBlock raw = new TextBlockLineBefore(getDefaultThickness(),
				TextBlockUtils.withMargin(b, marginX, 6, dimTitle.getHeight() / 2, 4), separator, title);
		return TextBlockUtils.withMargin(raw, 0, 0, dimTitle.getHeight() / 2, 0);
	}

	final protected double getDefaultThickness() {
		return style.value(PName.LineThickness).asDouble();
	}

}
