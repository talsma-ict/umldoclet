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

import java.util.regex.Pattern;

// Splitter.java to be finished
public abstract class MyPattern {

	public static Pattern2 cmpile(String p) {
		p = transformAndCheck(p);
		return new Pattern2(Pattern.compile(p));
	}

	public static Pattern2 cmpileNockeck(String p) {
		p = transform(p);
		return new Pattern2(Pattern.compile(p));
	}

	public static Pattern2 cmpile(String p, int type) {
		p = transformAndCheck(p);
		return new Pattern2(Pattern.compile(p, type));
	}

	public static Pattern2 cmpileNockeck(String p, int type) {
		p = transform(p);
		return new Pattern2(Pattern.compile(p, type));
	}

	private static String transformAndCheck(String p) {
		p = transform(p);
		return p;
	}

	private static String transform(String p) {
		// Replace ReadLineReader.java
		p = p.replaceAll("%s", "\\\\s\u00A0"); // space
		p = p.replaceAll("%q", "'\u2018\u2019"); // quote
		p = p.replaceAll("%g", "\"\u201c\u201d\u00ab\u00bb"); // double quote
		return p;
	}

	public static boolean mtches(CharSequence input, String regex) {
		return cmpile(regex).matcher(input).matches();
	}

	public static CharSequence removeAll(CharSequence src, String regex) {
		return src.toString().replaceAll(transform(regex), "");
	}

}
