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
package net.sourceforge.plantuml.cucadiagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.Style;

public class BodierSimple implements Bodier {

	private final List<CharSequence> rawBody = new ArrayList<>();
	private Entity leaf;

	@Override
	public void muteClassToObject() {
		throw new UnsupportedOperationException();
	}

	BodierSimple() {
	}

	@Override
	public void setLeaf(Entity leaf) {
		this.leaf = Objects.requireNonNull(leaf);
	}

	@Override
	public boolean addFieldOrMethod(String s) throws NoSuchColorException {
		final Display display = Display.getWithNewlines2(s);
		rawBody.addAll(display.asList());
		return true;
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
	public List<CharSequence> getRawBody() {
		return Collections.unmodifiableList(rawBody);
	}

	@Override
	public TextBlock getBody(ISkinParam skinParam, boolean showMethods, boolean showFields, Stereotype stereotype,
			Style style, FontConfiguration fontConfiguration) {
		return BodyFactory.create1(skinParam.getDefaultTextAlignment(HorizontalAlignment.LEFT), rawBody, skinParam,
				stereotype, leaf, style);
	}

}
