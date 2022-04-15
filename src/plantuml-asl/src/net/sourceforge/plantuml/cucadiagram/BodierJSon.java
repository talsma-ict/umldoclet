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

import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class BodierJSon implements Bodier {

	private ILeaf leaf;
	private JsonValue json;

	@Override
	public void muteClassToObject() {
		throw new UnsupportedOperationException();
	}

	public BodierJSon() {
	}

	@Override
	public void setLeaf(ILeaf leaf) {
		this.leaf = Objects.requireNonNull(leaf);

	}

	@Override
	public Display getMethodsToDisplay() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Display getFieldsToDisplay() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasUrl() {
		return false;
	}

	@Override
	public TextBlock getBody(FontParam fontParam, ISkinParam skinParam, final boolean showMethods,
			final boolean showFields, Stereotype stereotype, Style style, FontConfiguration fontConfiguration) {
		return new TextBlockCucaJSon(fontConfiguration, fontParam, skinParam, json);
	}

	@Override
	public List<CharSequence> getRawBody() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addFieldOrMethod(String s) throws NoSuchColorException {
		throw new UnsupportedOperationException();
	}

	public void setJson(JsonValue json) {
		this.json = json;
	}

}
