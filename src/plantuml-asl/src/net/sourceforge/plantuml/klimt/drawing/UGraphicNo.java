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
package net.sourceforge.plantuml.klimt.drawing;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import net.sourceforge.plantuml.klimt.UGroupType;
import net.sourceforge.plantuml.klimt.UParam;
import net.sourceforge.plantuml.klimt.UParamNull;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.ColorMapper;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.url.Url;

public abstract class UGraphicNo implements UGraphic {
	// ::remove file when __HAXE__

	private final StringBounder stringBounder;
	private final UTranslate translate;

//	private UGraphicNo(UGraphicNo other, UChange change) {
//		this(other.stringBounder,
//				change instanceof UTranslate ? other.translate.compose((UTranslate) change) : other.translate);
//	}

	public UGraphicNo(StringBounder stringBounder, UTranslate translate) {
		this.stringBounder = stringBounder;
		this.translate = translate;
	}

	//
	// Implement UGraphic
	//

	@Override
	final public void startUrl(Url url) {
	}

	@Override
	public void startGroup(Map<UGroupType, String> typeIdents) {
	}

	@Override
	final public void closeUrl() {
	}

	@Override
	final public void closeGroup() {
	}

	@Override
	public ColorMapper getColorMapper() {
		throw new UnsupportedOperationException();
	}

	@Override
	public HColor getDefaultBackground() {
		return HColors.BLACK;
	}

	@Override
	public UParam getParam() {
		return new UParamNull();
	}

	@Override
	public StringBounder getStringBounder() {
		return stringBounder;
	}

	@Override
	public void flushUg() {
	}

	@Override
	public boolean matchesProperty(String propertyName) {
		return false;
	}

	@Override
	public void writeToStream(OutputStream os, String metadata, int dpi) throws IOException {
		throw new UnsupportedOperationException();
	}

	//
	// Internal things
	//

	protected final UTranslate getTranslate() {
		return translate;
	}
}
