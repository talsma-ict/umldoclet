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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.Style;

public class BodierMap implements Bodier {

	private final List<CharSequence> rawBody = new ArrayList<>();
	private final Map<String, String> map = new LinkedHashMap<String, String>();
	private ILeaf leaf;

	@Override
	public void muteClassToObject() {
		throw new UnsupportedOperationException();
	}

	public BodierMap() {
	}

	@Override
	public void setLeaf(ILeaf leaf) {
		this.leaf = Objects.requireNonNull(leaf);

	}

	public static String getLinkedEntry(String s) {
		final Pattern p = Pattern.compile("(\\*-+\\>)");
		final Matcher m = p.matcher(s);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

	@Override
	public boolean addFieldOrMethod(String s) {
		if (s.contains("=>")) {
			final int x = s.indexOf("=>");
			map.put(s.substring(0, x).trim(), s.substring(x + 2).trim());
			return true;
		} else if (getLinkedEntry(s) != null) {
			final String link = getLinkedEntry(s);
			final int x = s.indexOf(link);
			map.put(s.substring(0, x).trim(), "\0");
			return true;
		}
		return false;
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
		return new TextBlockMap(fontConfiguration, fontParam, skinParam, map);
	}

	@Override
	public List<CharSequence> getRawBody() {
		return Collections.unmodifiableList(rawBody);
	}

}
