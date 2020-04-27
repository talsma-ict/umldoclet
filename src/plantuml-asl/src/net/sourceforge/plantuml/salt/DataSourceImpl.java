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
package net.sourceforge.plantuml.salt;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;

public class DataSourceImpl implements DataSource {

	private int i = 0;
	private final List<Terminated<String>> data = new ArrayList<Terminated<String>>();

	public DataSourceImpl(List<String> data) {
		final Pattern2 p = MyPattern.cmpile("\\{(?:[-+^#!*/]|S-|SI|S)?");

		for (String s : data) {
			final StringTokenizer st = new StringTokenizer(s, "|}", true);
			while (st.hasMoreTokens()) {
				final String token = StringUtils.trin(st.nextToken());
				if (token.equals("|")) {
					continue;
				}
				final Terminator terminator = st.hasMoreTokens() ? Terminator.NEWCOL : Terminator.NEWLINE;
				final Matcher2 m = p.matcher(token);
				final boolean found = m.find();
				if (found == false) {
					addInternal(token, terminator);
					continue;
				}
				int lastStart = 0;
				int end = 0;
				do {
					final int start = m.start();
					if (start > lastStart) {
						addInternal(token.substring(lastStart, start), Terminator.NEWCOL);
					}
					end = m.end();
					final Terminator t = end == token.length() ? terminator : Terminator.NEWCOL;
					addInternal(token.substring(start, end), t);
					lastStart = end;
				} while (m.find());
				if (end < token.length()) {
					addInternal(token.substring(end), terminator);
				}
			}
		}
	}


	private void addInternal(String s, Terminator t) {
		s = StringUtils.trin(s);
		if (s.length() > 0) {
			data.add(new Terminated<String>(s, t));
		}
	}

	public Terminated<String> peek(int nb) {
		return data.get(i + nb);
	}

	public boolean hasNext() {
		return i < data.size();
	}

	public Terminated<String> next() {
		final Terminated<String> result = data.get(i);
		i++;
		return result;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public String toString() {
		return super.toString() + " " + (hasNext() ? peek(0) : "$$$");
	}

}
