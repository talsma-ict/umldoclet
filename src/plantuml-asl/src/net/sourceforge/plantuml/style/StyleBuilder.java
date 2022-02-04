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
package net.sourceforge.plantuml.style;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.SkinParam;

public class StyleBuilder implements AutomaticCounter {

	private final Map<StyleSignature, Style> stylesMap = new LinkedHashMap<StyleSignature, Style>();
	private final Set<StyleSignature> printedForLog;
	private final SkinParam skinParam;
	private int counter;

	public void printMe() {
		for (Entry<StyleSignature, Style> ent : stylesMap.entrySet())
			ent.getValue().printMe();

	}

	private StyleBuilder(SkinParam skinParam, Set<StyleSignature> printedForLog) {
		this.skinParam = skinParam;
		this.printedForLog = new LinkedHashSet<>();
	}

	public StyleBuilder(SkinParam skinParam) {
		this(skinParam, new LinkedHashSet<StyleSignature>());
	}

	public final SkinParam getSkinParam() {
		return skinParam;
	}

	public Style createStyle(String name) {
		if (name.contains("*"))
			throw new IllegalArgumentException();

		name = name.toLowerCase();
		final StyleSignature signature = new StyleSignature(name);
		final Style result = stylesMap.get(signature);
		if (result == null)
			return new Style(signature, new EnumMap<PName, Value>(PName.class));

		return result;
	}

	public StyleBuilder muteStyle(Style modifiedStyle) {
		final Map<StyleSignature, Style> copy = new LinkedHashMap<StyleSignature, Style>(stylesMap);
		final StyleSignature signature = modifiedStyle.getSignature();
		final Style orig = copy.get(signature);
		if (orig == null) {
			copy.put(signature, modifiedStyle);
		} else {
			final Style tmp = orig.mergeWith(modifiedStyle);
			copy.put(signature, tmp);
		}
		final StyleBuilder result = new StyleBuilder(skinParam, this.printedForLog);
		result.stylesMap.putAll(copy);
		result.counter = this.counter;
		return result;
	}

	public void loadInternal(StyleSignature signature, Style newStyle) {
		if (signature.isStarred())
			throw new IllegalArgumentException();

		final Style orig = this.stylesMap.get(signature);
		if (orig == null) {
			this.stylesMap.put(signature, newStyle);
		} else {
			final Style tmp = orig.mergeWith(newStyle);
			this.stylesMap.put(signature, tmp);
		}
	}

	public int getNextInt() {
		return ++counter;
	}

	public Style getMergedStyle(StyleSignature signature) {
		boolean added = this.printedForLog.add(signature);
		if (added)
			Log.info("Using style " + signature);

		Style result = null;
		for (Entry<StyleSignature, Style> ent : stylesMap.entrySet()) {
			final StyleSignature key = ent.getKey();
			if (key.matchAll(signature) == false)
				continue;

			if (result == null)
				result = ent.getValue();
			else
				result = result.mergeWith(ent.getValue());

		}
		return result;
	}

	public Style getMergedStyleSpecial(StyleSignature signature, int deltaPriority) {
		boolean added = this.printedForLog.add(signature);
		if (added)
			Log.info("Using style " + signature);

		Style result = null;
		for (Entry<StyleSignature, Style> ent : stylesMap.entrySet()) {
			final StyleSignature key = ent.getKey();
			if (key.matchAll(signature) == false)
				continue;

			Style tmp = ent.getValue();
			if (key.isStarred())
				tmp = tmp.deltaPriority(deltaPriority);

			if (result == null)
				result = tmp;
			else
				result = result.mergeWith(tmp);

		}
		return result;
	}

}
