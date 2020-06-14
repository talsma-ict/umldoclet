/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.cucadiagram.Stereotype;

public class StyleDefinition {

	private final List<String> namesStrings = new ArrayList<String>();

	private StyleDefinition(List<String> copy) {
		this.namesStrings.addAll(copy);
	}

	public static StyleDefinition of(SName... names) {
		final List<String> result = new ArrayList<String>();
		for (SName name : names) {
			result.add(name.name());
		}
		return new StyleDefinition(result);
	}

	// public StyleDefinition with(SName other) {
	// final List<String> result = new ArrayList<String>(namesStrings);
	// result.add(other.name());
	// return new StyleDefinition(result);
	// }

	public StyleDefinition with(Stereotype stereotype) {
		final List<String> result = new ArrayList<String>(namesStrings);
		if (stereotype != null) {
			for (String name : stereotype.getStyleNames()) {
				result.add(name);
			}
		}
		result.add(SName.stereotype.name());
		return new StyleDefinition(result);
	}

	public StyleDefinition mergeWith(List<Style> others) {
		final List<String> copy = new ArrayList<String>(namesStrings);
		for (Style other : others) {
			copy.add(other.getStyleName());
		}
		return new StyleDefinition(copy);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (String name : namesStrings) {
			sb.append(name);
			sb.append(" ");
		}
		return sb.toString().trim();
	}

	public Style getMergedStyle(StyleBuilder styleBuilder) {
		if (styleBuilder == null) {
			return null;
		}
		return styleBuilder.getMergedStyle(namesStrings);
	}

}
