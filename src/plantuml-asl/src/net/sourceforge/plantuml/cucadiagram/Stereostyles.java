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
package net.sourceforge.plantuml.cucadiagram;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;

public class Stereostyles {

	public static final Stereostyles NONE = new Stereostyles();

	private final Set<String> names = new LinkedHashSet<>();

	private Stereostyles() {
	}

	public boolean isEmpty() {
		return names.isEmpty();
	}

	public static Stereostyles build(String label) {
		final Stereostyles result = new Stereostyles();
		final Pattern2 p = MyPattern.cmpile("\\<{3}(.*?)\\>{3}");
		final Matcher2 m = p.matcher(label);
		while (m.find()) {
			result.names.add(m.group(1));
		}
		return result;
	}

	public Collection<String> getStyleNames() {
		return Collections.unmodifiableCollection(names);
	}

}
