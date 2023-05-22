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
package net.sourceforge.plantuml.style.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.regex.Matcher2;
import net.sourceforge.plantuml.regex.MyPattern;
import net.sourceforge.plantuml.regex.Pattern2;
import net.sourceforge.plantuml.style.AutomaticCounter;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleLoader;
import net.sourceforge.plantuml.style.StyleScheme;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.style.Value;
import net.sourceforge.plantuml.style.ValueImpl;
import net.sourceforge.plantuml.text.StringLocated;
import net.sourceforge.plantuml.utils.BlocLines;

public class StyleParserLegacy {

	private final static String KEYNAMES = "[-.\\w(), ]+?";
	private final static Pattern2 keyName = MyPattern.cmpile("^[:]?(" + KEYNAMES + ")([%s]+\\*)?[%s]*\\{$");
	private final static Pattern2 propertyAndValue = MyPattern.cmpile("^([\\w]+):?[%s]+(.*?);?$");
	private final static Pattern2 closeBracket = MyPattern.cmpile("^\\}$");

	public static Collection<Style> parse(BlocLines lines, AutomaticCounter counter) throws StyleParsingException {

		final Collection<Style> foo = StyleParser.parse(lines, counter);

		lines = lines.eventuallyMoveAllEmptyBracket();
		final List<Style> result = new ArrayList<>();
		final CssVariables variables = new CssVariables();
		StyleScheme scheme = StyleScheme.REGULAR;

		Context context = new Context();
		final List<Map<PName, Value>> maps = new ArrayList<Map<PName, Value>>();
		boolean inComment = false;
		for (StringLocated s : lines) {
			String trimmed = s.getTrimmed().getString();

			if (trimmed.startsWith("/*") || trimmed.endsWith("*/"))
				continue;
			if (trimmed.startsWith("/'") || trimmed.endsWith("'/"))
				continue;

			if (trimmed.startsWith("/*") || trimmed.startsWith("/'")) {
				inComment = true;
				continue;
			}
			if (trimmed.endsWith("*/") || trimmed.endsWith("'/")) {
				inComment = false;
				continue;
			}
			if (inComment)
				continue;

			if (trimmed.matches("@media.*dark.*\\{")) {
				scheme = StyleScheme.DARK;
				continue;
			}

			if (trimmed.startsWith("--")) {
				variables.learn(trimmed);
				continue;
			}

			final int x = trimmed.lastIndexOf("//");
			if (x != -1)
				trimmed = trimmed.substring(0, x).trim();

			final Matcher2 mKeyNames = keyName.matcher(trimmed);
			if (mKeyNames.find()) {
				String names = mKeyNames.group(1);
				final boolean isRecurse = mKeyNames.group(2) != null;
				if (isRecurse)
					names += "*";

				context = context.push(names);
				maps.add(new EnumMap<PName, Value>(PName.class));
				continue;
			}
			final Matcher2 mPropertyAndValue = propertyAndValue.matcher(trimmed);
			if (mPropertyAndValue.find()) {
				final PName key = PName.getFromName(mPropertyAndValue.group(1), scheme);
				final String value = variables.value(mPropertyAndValue.group(2));
				if (key != null && maps.size() > 0)
					maps.get(maps.size() - 1).put(key, //
							scheme == StyleScheme.REGULAR ? //
									ValueImpl.regular(value, counter) : ValueImpl.dark(value, counter));

				continue;
			}
			final Matcher2 mCloseBracket = closeBracket.matcher(trimmed);
			if (mCloseBracket.find()) {
				if (context.size() > 0) {
					final Collection<StyleSignatureBasic> signatures = context.toSignatures();
					for (StyleSignatureBasic signature : signatures) {
						Map<PName, Value> tmp = maps.get(maps.size() - 1);
						if (signature.isWithDot())
							tmp = StyleLoader.addPriorityForStereotype(tmp);
						if (tmp.size() > 0) {
							final Style style = new Style(signature, tmp);
							result.add(style);
						}
					}
					context = context.pop();
					maps.remove(maps.size() - 1);
				} else {
					scheme = StyleScheme.REGULAR;
				}
			}
		}

		System.err.println("foo1=" + foo.size());
		System.err.println("result=" + result.size());
		if (foo.size() != result.size() || foo.size() < 10) {
			print_debug(foo);
			print_debug(result);
		}

		// return Collections.unmodifiableList(result);
		return Collections.unmodifiableCollection(foo);

	}

	private static void print_debug(Collection<Style> list) {
		System.err.println("=====================");
		int i = 0;
		for (Style style : list) {
			System.err.println("style=" + i + " " + style.getSignature());
			i++;
		}
		System.err.println("=====================");

	}

}
