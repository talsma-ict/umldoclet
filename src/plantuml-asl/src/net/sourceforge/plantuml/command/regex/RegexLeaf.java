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
package net.sourceforge.plantuml.command.regex;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.annotation.HaxeIgnored;

public class RegexLeaf implements IRegex {

	private static final RegexLeaf END = new RegexLeaf("$");
	private static final RegexLeaf START = new RegexLeaf("^");
	private final String pattern;
	private final String name;

	private int count = -1;

	@HaxeIgnored
	public RegexLeaf(String regex) {
		this(null, regex);
	}

	public RegexLeaf(String name, String regex) {
		this.pattern = regex;
		this.name = name;
	}

	public static RegexLeaf spaceZeroOrMore() {
		return new RegexLeaf("[%s]*");
	}

	public static RegexLeaf spaceOneOrMore() {
		return new RegexLeaf("[%s]+");
	}

	public static RegexLeaf start() {
		return START;
	}

	public static RegexLeaf end() {
		return END;
	}

	@Override
	public String toString() {
		return super.toString() + " " + name + " " + pattern;
	}

	public String getName() {
		return name;
	}

	public String getPattern() {
		return pattern;
	}

	public int count() {
		if (count == -1) {
			count = MyPattern.cmpile(pattern).matcher("").groupCount();
		}
		return count;
	}

	public Map<String, RegexPartialMatch> createPartialMatch(Iterator<String> it) {
		final RegexPartialMatch m = new RegexPartialMatch(name);
		for (int i = 0; i < count(); i++) {
			final String group = it.next();
			m.add(group);
		}
		if (name == null) {
			return Collections.emptyMap();
		}
		return Collections.singletonMap(name, m);
	}

	public boolean match(StringLocated full) {
		throw new UnsupportedOperationException();
	}

	public RegexResult matcher(String full) {
		throw new UnsupportedOperationException();
	}

	static private final Set<String> UNKNOWN = new HashSet<>();

	static private final Pattern p1 = Pattern.compile("^[-0A-Za-z_!:@;/=,\"]+$");
	static private final Pattern p2 = Pattern.compile("^[-0A-Za-z_!:@;/=,\"]+\\?$");
	static private final Pattern p3 = Pattern
			.compile("^\\(?[-0A-Za-z_!:@;/=\" ]+\\??(\\|[-0A-Za-z_!:@;/=,\" ]+\\??)+\\)?$");

	private static long getSignatureP3(String s) {
		long result = -1L;
		for (StringTokenizer st = new StringTokenizer(s, "()|"); st.hasMoreTokens();) {
			final String val = st.nextToken();
			final long sig = FoxSignature.getFoxSignature(val.endsWith("?") ? val.substring(0, val.length() - 2) : val);
			result = result & sig;
		}
		return result;
	}

	public long getFoxSignatureNone() {
		return 0;
	}

	public long getFoxSignature() {
		if (p1.matcher(pattern).matches()) {
			return FoxSignature.getFoxSignature(pattern);
		}
		if (p2.matcher(pattern).matches()) {
			return FoxSignature.getFoxSignature(pattern.substring(0, pattern.length() - 2));
		}
		if (p3.matcher(pattern).matches()) {
			// System.err.println("special " + pattern);
			// System.err.println("result " +
			// FoxSignature.backToString(getSignatureP3(pattern)));
			return getSignatureP3(pattern);
		}
		if (pattern.length() == 2 && pattern.startsWith("\\")
				&& Character.isLetterOrDigit(pattern.charAt(1)) == false) {
			return FoxSignature.getFoxSignature(pattern.substring(1));
		}
		if (pattern.equals("\\<\\>") || pattern.equals("(\\<\\<.*\\>\\>)")) {
			return FoxSignature.getFoxSignature("<>");
		}
		if (pattern.equals("\\<-\\>")) {
			return FoxSignature.getFoxSignature("<->");
		}
		if (pattern.equals("(-+)")) {
			return FoxSignature.getFoxSignature("-");
		}
		if (pattern.equals("\\|+") || pattern.equals("\\|\\|")) {
			return FoxSignature.getFoxSignature("|");
		}
		if (pattern.equals("([*]+)")) {
			return FoxSignature.getFoxSignature("*");
		}
		if (pattern.equals("[%s]+") || pattern.equals("[%s]*")) {
			return 0;
		}
//		synchronized (UNKNOWN) {
//			final boolean changed = UNKNOWN.add(pattern);
//			if (changed)
//				System.err.println("unknow=" + pattern);
//
//		}
		return 0;
	}

}
