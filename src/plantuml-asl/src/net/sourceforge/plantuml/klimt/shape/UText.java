/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.klimt.shape;

import net.sourceforge.plantuml.klimt.UShape;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;

public class UText implements UShape {

	private final String text;
	private final FontConfiguration font;
	private final int orientation;

	@Override
	public String toString() {
		return "UText[" + text + "]";
	}

	private UText(String text, FontConfiguration font, int orientation) {
		assert text.indexOf('\t') == -1;
		this.text = text;
		this.font = font;
		this.orientation = orientation;
	}

	public static UText build(String text, FontConfiguration font) {
		return new UText(text, font, 0);
	}

	public UText withOrientation(int orientation) {
		return new UText(text, font, orientation);
	}

	public String getText() {
		return text;
	}

	public FontConfiguration getFontConfiguration() {
		return font;
	}

	// ::comment when __HAXE__
	public double getDescent(StringBounder stringBounder) {
		return stringBounder.getDescent(font.getFont(), text);
	}
	// ::done

	public final int getOrientation() {
		return orientation;
	}

}
