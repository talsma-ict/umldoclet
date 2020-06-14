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
package net.sourceforge.plantuml.ugraphic;

import java.awt.font.LineMetrics;

import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.TextBlockUtils;

public class UText implements UShape {

	private final String text;
	private final FontConfiguration font;
	private final String ariaLabel;

	@Override
	public String toString() {
		return "UText[" + text + "]";
	}

	public UText(String text, FontConfiguration font) {
		this(text, font, null);
	}
	
	private UText(String text, FontConfiguration font, String ariaLabel) {
		assert text.indexOf('\t') == -1;
		this.text = text;
		this.font = font;
		this.ariaLabel = ariaLabel;
	}
	
	public UText withAriaLabel(String newAriaLabel) {
		return new UText(text, font, newAriaLabel);
	}

	public String getText() {
		return text;
	}

	public FontConfiguration getFontConfiguration() {
		return font;
	}
	
	public double getDescent() {
		final LineMetrics fm = TextBlockUtils.getLineMetrics(font.getFont(), text);
		final double descent = fm.getDescent();
		return descent;
	}

	public String getAriaLabel() {
		return ariaLabel;
	}



}
