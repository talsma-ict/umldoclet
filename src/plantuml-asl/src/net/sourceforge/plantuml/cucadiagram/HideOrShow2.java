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
package net.sourceforge.plantuml.cucadiagram;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.stereo.Stereotag;
import net.sourceforge.plantuml.stereo.Stereotype;

public class HideOrShow2 {

	private final String what;
	private final boolean show;

	@Override
	public String toString() {
		return what + " (" + show + ")";
	}

	private boolean isApplyable(Entity leaf) {
		if (what.startsWith("$"))
			return isApplyableTag(leaf, what.substring(1));

		if (what.startsWith("<<") && what.endsWith(">>"))
			return isApplyableStereotype(leaf.getStereotype(), what.substring(2, what.length() - 2).trim());

		if (isAboutUnlinked())
			return isApplyableUnlinked(leaf);

		final String fullName = leaf.getName();
		// System.err.println("fullName=" + fullName);
		return match(fullName, what);
	}

	private boolean isApplyable(Stereotype stereotype) {
		if (what.startsWith("<<") && what.endsWith(">>"))
			return isApplyableStereotype(stereotype, what.substring(2, what.length() - 2).trim());
		return false;
	}

	public boolean isAboutUnlinked() {
		return what.equalsIgnoreCase("@unlinked");
	}

	private boolean isApplyableUnlinked(Entity leaf) {
		if (leaf.isAloneAndUnlinked())
			return true;

		return false;
	}

	private boolean isApplyableStereotype(Stereotype stereotype, String pattern) {
		if (stereotype == null)
			return false;

		for (String label : stereotype.getMultipleLabels())
			if (match(label, pattern))
				return true;

		return false;
	}

	private boolean isApplyableTag(Entity leaf, String pattern) {
		for (Stereotag tag : leaf.stereotags())
			if (match(tag.getName(), pattern))
				return true;

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

	public boolean apply(boolean hidden, Entity leaf) {
		if (isApplyable(leaf))
			return !show;

		return hidden;
	}

	public boolean apply(boolean hidden, Stereotype stereotype) {
		if (isApplyable(stereotype))
			return !show;

		return hidden;
	}

}
