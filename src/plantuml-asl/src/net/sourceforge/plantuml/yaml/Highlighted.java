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
package net.sourceforge.plantuml.yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.stereo.Stereotype;

public class Highlighted {
    // ::remove folder when __HAXE__

	public static final String HIGHLIGHTED = "#highlight ";
	private final static Pattern pattern = Pattern.compile("^([^<>]+)(\\<\\<.*\\>\\>)?$");

	private final List<String> paths;
	private final Stereotype stereotype;

	private Highlighted(List<String> paths, Stereotype stereotype) {
		this.paths = paths;
		this.stereotype = stereotype;
	}

	public static boolean matchesDefinition(String line) {
		return line.startsWith(Highlighted.HIGHLIGHTED);
	}

	public static Highlighted build(String line) {
		if (matchesDefinition(line) == false)
			throw new IllegalStateException();
		line = line.substring(HIGHLIGHTED.length()).trim();

		final Matcher matcher = pattern.matcher(line);
		if (matcher.matches() == false)
			throw new IllegalStateException();
		final String paths = matcher.group(1).trim();
		final Stereotype stereotype = matcher.group(2) == null ? null : Stereotype.build(matcher.group(2));

		return new Highlighted(toList(paths), stereotype);
	}

	private static List<String> toList(String paths) {
		final List<String> result = new ArrayList<>();
		for (String s : paths.split("/"))
			result.add(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(s.trim(), "\""));

		return result;
	}

	public Highlighted upOneLevel(String key) {
		if (paths.size() <= 1)
			return null;
		final String first = paths.get(0);
		if ("**".equals(first))
			return new Highlighted(paths, stereotype);
		if ("*".equals(first) || first.equals(key))
			return new Highlighted(paths.subList(1, paths.size()), stereotype);
		return null;

	}

	public boolean isKeyHighlight(String key) {
		if (paths.size() == 2 && paths.get(0).equals("**") && paths.get(1).equals(key))
			return true;
		return paths.size() == 1 && paths.get(0).equals(key);
	}

	public final Stereotype getStereotype() {
		return stereotype;
	}

}
