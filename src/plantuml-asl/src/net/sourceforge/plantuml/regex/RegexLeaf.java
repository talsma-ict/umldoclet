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
package net.sourceforge.plantuml.regex;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.text.FoxSignature;
import net.sourceforge.plantuml.text.StringLocated;

public class RegexLeaf implements IRegex {

	private static final RegexLeaf END = new RegexLeaf("$");
	private static final RegexLeaf START = new RegexLeaf("^");
	private final String pattern;
	private final String name;

	private int count = -1;

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
		if (count == -1)
			count = MyPattern.cmpile(pattern).matcher("").groupCount();

		return count;
	}

	public Map<String, RegexPartialMatch> createPartialMatch(Iterator<String> it) {
		final RegexPartialMatch m = new RegexPartialMatch(name);
		for (int i = 0; i < count(); i++) {
			final String group = it.next();
			m.add(group);
		}
		if (name == null)
			return Collections.emptyMap();

		return Collections.singletonMap(name, m);
	}

	public boolean match(StringLocated full) {
		throw new UnsupportedOperationException();
	}

	public RegexResult matcher(String full) {
		throw new UnsupportedOperationException();
	}

	// static private final Set<String> UNKNOWN = new HashSet<>();

	static private final Pattern p1 = Pattern.compile(
			"^\\(?((?:[-0A-Za-z_!:@;/=,\" ][?+*]?|\\\\[b$(){}<>|*.+^\\[\\]][?+*]?|\\.\\*|\\.\\+)+)(?:\\)\\+|\\))?$");

	static private final Pattern p2 = Pattern.compile("^\\([-?a-z ]+(\\|[-?a-z ]+)+\\)$");

	static private final Pattern p3 = Pattern.compile("^\\(?\\[[-=.~]+\\]\\+\\)?$");

	private static long getSignatureP2(String s) {
		long result = -1L;
		for (StringTokenizer st = new StringTokenizer(s, "()|"); st.hasMoreTokens();) {
			final String val = st.nextToken();
			result = result & FoxSignature.getFoxSignatureFromRegex(val);
		}
		return result;
	}

	public long getFoxSignature() {
		if (pattern.equals("[%s]+"))
			return FoxSignature.getSpecialSpaces();
		if (pattern.equals("[%s]*"))
			return 0;
		final String pattern2 = pattern.replaceAll("\\[%s\\][+*?]?|\\(\\[([^\\\\\\[\\]])+\\]\\)[+*?]?", "");

		final Matcher m1 = p1.matcher(pattern2);
		if (m1.matches())
			return FoxSignature.getFoxSignatureFromRegex(m1.group(1));

		final Matcher m2 = p2.matcher(pattern2);
		if (m2.matches())
			return getSignatureP2(pattern2);

		final Matcher m3 = p3.matcher(pattern2);
		if (m3.matches())
			return FoxSignature.getSpecial1();

//		synchronized (UNKNOWN) {
//			final boolean changed = UNKNOWN.add(pattern2);
//			if (changed) {
//				if (pattern.equals(pattern2))
//					System.err.println("unknow=" + UNKNOWN.size() + " " + pattern);
//				else
//					System.err.println("unknow=" + UNKNOWN.size() + " " + pattern2 + "        " + pattern);
//				// Thread.dumpStack();
//			}
//		}
		return 0;
	}

}
