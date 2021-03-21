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

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class BodierSimple implements Bodier {

	private final List<CharSequence> rawBody = new ArrayList<CharSequence>();
	private ILeaf leaf;

	public void muteClassToObject() {
		throw new UnsupportedOperationException();
	}

	BodierSimple() {
	}

	public void setLeaf(ILeaf leaf) {
		if (leaf == null) {
			throw new IllegalArgumentException();
		}
		this.leaf = leaf;
	}

	public void addFieldOrMethod(String s) throws NoSuchColorException {
		final Display display = Display.getWithNewlines2(s);
		rawBody.addAll(display.asList());
	}

	public Display getMethodsToDisplay() {
		throw new UnsupportedOperationException();
	}

	public Display getFieldsToDisplay() {
		throw new UnsupportedOperationException();
	}

	public boolean hasUrl() {
		return false;
	}

	public List<CharSequence> getRawBody() {
		return Collections.unmodifiableList(rawBody);
	}

	public TextBlock getBody(FontParam fontParam, ISkinParam skinParam, boolean showMethods, boolean showFields,
			Stereotype stereotype, Style style) {
		return BodyFactory.create1(skinParam.getDefaultTextAlignment(HorizontalAlignment.LEFT), rawBody, fontParam,
				skinParam, stereotype, leaf, style);
	}

}
