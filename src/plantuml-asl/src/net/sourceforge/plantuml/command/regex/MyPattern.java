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
package net.sourceforge.plantuml.command.regex;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {
	private final int maxSize;

	public MaxSizeHashMap(int maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxSize;
	}
}

// Splitter.java to be finished
public abstract class MyPattern {

	private static final Map<String, Pattern2> cache = new MaxSizeHashMap<>(512);

	private static final Pattern2 EMPTY = new Pattern2(Pattern.compile(""));

//	static int CPT1;
//	static int CPT2;

	public static Pattern2 cmpile(final String p) {
		if (p == null || p.length() == 0) {
			return EMPTY;
		}
//		CPT1++;
		Pattern2 result = null;
		synchronized (cache) {
			result = cache.get(p);
			if (result != null) {
				return result;
			}
		}
		assert result == null;
		result = new Pattern2(Pattern.compile(transform(p), Pattern.CASE_INSENSITIVE));

		synchronized (cache) {
			cache.put(p, result);
//			CPT2++;
//			System.err.println("CPT= " + CPT1 + " / " + CPT2 + " " + cache.size());
		}

		return result;
	}

	private static String transform(String p) {
		// Replace ReadLineReader.java
		p = p.replace("%pLN", "\\p{L}0-9"); // Unicode Letter, digit
		p = p.replace("%s", "\\s\u00A0"); // space
		p = p.replace("%q", "'\u2018\u2019"); // quote
		p = p.replace("%g", "\"\u201c\u201d\u00ab\u00bb"); // double quote
		return p;
	}

	public static boolean mtches(CharSequence input, String regex) {
		return cmpile(regex).matcher(input).matches();
	}

	public static CharSequence removeAll(CharSequence src, String regex) {
		return src.toString().replaceAll(transform(regex), "");
	}

}
