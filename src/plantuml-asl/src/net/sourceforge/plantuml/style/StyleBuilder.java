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
package net.sourceforge.plantuml.style;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.SkinParam;

public class StyleBuilder implements AutomaticCounter {

	private final Map<String, Style> styles = new LinkedHashMap<String, Style>();

	private final SkinParam skinParam;
	private int counter;

	public StyleBuilder(SkinParam skinParam) {
		this.skinParam = skinParam;

	}

	public Collection<String> getAllStyleNames() {
		return Collections.unmodifiableCollection(styles.keySet());
	}

	public Style createStyle(String name) {
		final Style result = styles.get(name);
		if (result == null) {
			return new Style(StyleKind.STEREOTYPE, name, new EnumMap<PName, Value>(PName.class));
		}
		return result;
	}

	public StyleBuilder muteStyle(Style modifiedStyle) {
		final Map<String, Style> copy = new LinkedHashMap<String, Style>(styles);
		final String modifiedName = modifiedStyle.getStyleName();
		final Style orig = copy.get(modifiedName);
		if (orig == null) {
			copy.put(modifiedName, modifiedStyle);
		} else {
			assert orig.getStyleName().equals(modifiedName);
			final Style newStyle = orig.mergeWith(modifiedStyle);
			copy.put(modifiedName, newStyle);
		}
		final StyleBuilder result = new StyleBuilder(skinParam);
		result.styles.putAll(copy);
		result.counter = this.counter;
		return result;
	}

	public void put(String styleName, Style newStyle) {
		this.styles.put(styleName, newStyle);
	}

	public int getNextInt() {
		return ++counter;
	}

	public Style getMergedStyle(Collection<String> names) {
		Style result = null;
		for (String name : names) {
			final Style tmp = createStyle(name);
			// System.err.println("name=" + name);
//			if (tmp == null) {
//				// System.err.println("Style error for " + name);
//				continue;
//			}
			if (result == null) {
				result = tmp;
			} else {
				result = result.mergeWith(tmp);
			}
		}
		for (Entry<String, Style> ent : styles.entrySet()) {
			if (ent.getKey().indexOf('+') == -1) {
				continue;
			}
			if (matchAll(ent.getKey(), names)) {
				result = result.mergeWith(ent.getValue());
			}

		}
		return result;
	}

	private boolean matchAll(String key, Collection<String> names) {
		for (StringTokenizer st = new StringTokenizer(key, "+"); st.hasMoreTokens();) {
			final String token = st.nextToken();
			if (names.contains(token) == false) {
				return false;
			}
		}
		return true;
	}

}
