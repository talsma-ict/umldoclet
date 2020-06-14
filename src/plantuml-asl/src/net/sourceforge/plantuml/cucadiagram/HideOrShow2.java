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
package net.sourceforge.plantuml.cucadiagram;

public class HideOrShow2 {

	private final String what;
	private final boolean show;

	private boolean isApplyable(ILeaf leaf) {
		if (what.startsWith("$")) {
			return isApplyableTag(leaf, what.substring(1));
		}
		if (what.startsWith("<<") && what.endsWith(">>")) {
			return isApplyableStereotype(leaf, what.substring(2, what.length() - 2).trim());
		}
		final String fullName = leaf.getCode().getFullName();
		// System.err.println("fullName=" + fullName);
		return match(fullName, what);
	}

	private boolean isApplyableStereotype(ILeaf leaf, String pattern) {
		final Stereotype stereotype = leaf.getStereotype();
		if (stereotype == null) {
			return false;
		}
		for (String label : stereotype.getMultipleLabels()) {
			if (match(label, pattern)) {
				return true;
			}

		}
		return false;
	}

	private boolean isApplyableTag(ILeaf leaf, String pattern) {
		for (Stereotag tag : leaf.stereotags()) {
			if (match(tag.getName(), pattern)) {
				return true;
			}
		}
		return false;
	}

	private boolean match(String s, String pattern) {
		if (pattern.contains("*")) {
			// System.err.println("f1=" + pattern);
			// System.err.println("f2=" + Pattern.quote(pattern));
			// System.err.println("f3=" + Matcher.quoteReplacement(pattern));
			String reg = "^" + pattern.replace("*", ".*") + "$";
			return s.matches(reg);

		}
		return s.equals(pattern);
	}

	public HideOrShow2(String what, boolean show) {
		this.what = what;
		this.show = show;
	}

	public boolean apply(boolean hidden, ILeaf leaf) {
		if (isApplyable(leaf)) {
			return !show;
		}
		return hidden;
	}

}
