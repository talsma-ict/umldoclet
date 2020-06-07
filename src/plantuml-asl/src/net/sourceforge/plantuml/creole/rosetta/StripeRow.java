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
package net.sourceforge.plantuml.creole.rosetta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.creole.Stripe;
import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.FontStyle;

public class StripeRow implements Stripe {

	private final List<Atom> atoms = new ArrayList<Atom>();

	public Atom getLHeader() {
		return null;
	}

	public List<Atom> getAtoms() {
		return Collections.unmodifiableList(atoms);
	}

	public void add(Atom atom) {
		atoms.add(atom);
	}

	private static final Pattern bold = Pattern.compile("^" + WikiLanguage.UNICODE.tag("strong") + "(.*)$");
	private static final Pattern unbold = Pattern.compile("^" + WikiLanguage.UNICODE.slashTag("strong") + "(.*)$");
	private static final Pattern italic = Pattern.compile("^" + WikiLanguage.UNICODE.tag("em") + "(.*)$");
	private static final Pattern unitalic = Pattern.compile("^" + WikiLanguage.UNICODE.slashTag("em") + "(.*)$");
	private static final Pattern strike = Pattern.compile("^" + WikiLanguage.UNICODE.tag("strike") + "(.*)$");
	private static final Pattern unstrike = Pattern.compile("^" + WikiLanguage.UNICODE.slashTag("strike") + "(.*)$");

	private static Pattern getPattern(String line) {
		if (bold.matcher(line).find()) {
			return bold;
		}
		if (unbold.matcher(line).find()) {
			return unbold;
		}
		if (italic.matcher(line).find()) {
			return italic;
		}
		if (unitalic.matcher(line).find()) {
			return unitalic;
		}
		return null;
	}

	public static StripeRow parseUnicode(String line, FontConfiguration fontConfiguration) {
		final StripeRow result = new StripeRow();
		StringBuilder tmp = new StringBuilder();
		while (line.length() > 0) {
			final Pattern cmd = getPattern(line);
			if (cmd == null) {
				tmp.append(line.charAt(0));
				line = line.substring(1);
				continue;
			}
			if (tmp.length() > 0) {
				result.add(AtomText22.create(tmp.toString(), fontConfiguration));
				tmp.setLength(0);
			}
			final Matcher matcher = cmd.matcher(line);
			matcher.find();
			if (cmd == bold) {
				fontConfiguration = fontConfiguration.bold();
			} else if (cmd == unbold) {
				fontConfiguration = fontConfiguration.unbold();
			} else if (cmd == italic) {
				fontConfiguration = fontConfiguration.italic();
			} else if (cmd == unitalic) {
				fontConfiguration = fontConfiguration.unitalic();
			} else if (cmd == strike) {
				fontConfiguration = fontConfiguration.add(FontStyle.STRIKE);
			} else if (cmd == unstrike) {
				fontConfiguration = fontConfiguration.remove(FontStyle.STRIKE);
			} else {
				throw new IllegalStateException();
			}
			line = matcher.group(1);
		}
		assert line.length() == 0;
		if (tmp.length() > 0) {
			result.add(AtomText22.create(tmp.toString(), fontConfiguration));
		}
		return result;
	}

}
