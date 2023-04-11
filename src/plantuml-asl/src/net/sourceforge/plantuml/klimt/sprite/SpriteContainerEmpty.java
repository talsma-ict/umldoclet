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
package net.sourceforge.plantuml.klimt.sprite;

import java.util.Map;

import net.sourceforge.plantuml.klimt.color.HColorSet;
import net.sourceforge.plantuml.klimt.creole.CreoleMode;
import net.sourceforge.plantuml.klimt.creole.Parser;
import net.sourceforge.plantuml.klimt.creole.SheetBuilder;
import net.sourceforge.plantuml.klimt.creole.legacy.CreoleParser;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.style.ISkinSimple;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.text.Guillemet;

public class SpriteContainerEmpty implements SpriteContainer, ISkinSimple {

	@Override
	public Sprite getSprite(String name) {
		return SpriteImage.fromInternal(name);
	}

	@Override
	public String getValue(String key) {
		return null;
	}

	@Override
	public double getPadding() {
		return 0;
	}

	@Override
	public Guillemet guillemet() {
		return Guillemet.DOUBLE_COMPARATOR;
	}

	@Override
	public String getMonospacedFamily() {
		return Parser.MONOSPACED;
	}

	@Override
	public int getTabSize() {
		return 8;
	}

	@Override
	public HColorSet getIHtmlColorSet() {
		return HColorSet.instance();
	}

	@Override
	public int getDpi() {
		return 96;
	}

	@Override
	public void copyAllFrom(Map<String, String> other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> values() {
		throw new UnsupportedOperationException();
	}

	public double minClassWidthTOBEREMOVED(Style style) {
		return 0;
	}

	@Override
	public String transformStringForSizeHack(String s) {
		return s;
	}

	@Override
	public SheetBuilder sheet(FontConfiguration fontConfiguration, HorizontalAlignment horizontalAlignment,
			CreoleMode creoleMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SheetBuilder sheet(FontConfiguration fontConfiguration, HorizontalAlignment horizontalAlignment,
			CreoleMode creoleMode, FontConfiguration stereo) {
		return new CreoleParser(fontConfiguration, horizontalAlignment, this, creoleMode, stereo);
	}

}
