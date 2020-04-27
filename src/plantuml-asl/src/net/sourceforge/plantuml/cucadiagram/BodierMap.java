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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.TextBlock;

public class BodierMap implements Bodier {

	private final List<String> rawBody = new ArrayList<String>();
	private final Map<String, String> map = new LinkedHashMap<String, String>();
	private ILeaf leaf;

	public void muteClassToObject() {
		throw new UnsupportedOperationException();
	}

	public BodierMap() {
	}

	public void setLeaf(ILeaf leaf) {
		if (leaf == null) {
			throw new IllegalArgumentException();
		}
		this.leaf = leaf;

	}

	public static String getLinkedEntry(String s) {
		final Pattern p = Pattern.compile("(\\*-+\\>)");
		final Matcher m = p.matcher(s);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

	public void addFieldOrMethod(String s) {
		if (s.contains("=>")) {
			final int x = s.indexOf("=>");
			map.put(s.substring(0, x).trim(), s.substring(x + 2).trim());
		} else if (getLinkedEntry(s) != null) {
			final String link = getLinkedEntry(s);
			final int x = s.indexOf(link);
			map.put(s.substring(0, x).trim(), "\0");
		}
	}

	public List<Member> getMethodsToDisplay() {
		throw new UnsupportedOperationException();
	}

	public List<Member> getFieldsToDisplay() {
		throw new UnsupportedOperationException();
	}

	public boolean hasUrl() {
		return false;
	}

	public TextBlock getBody(FontParam fontParam, ISkinParam skinParam, final boolean showMethods,
			final boolean showFields, Stereotype stereotype) {
		return new TextBlockMap(fontParam, skinParam, map);
	}

	public List<String> getRawBody() {
		return Collections.unmodifiableList(rawBody);
	}

}
