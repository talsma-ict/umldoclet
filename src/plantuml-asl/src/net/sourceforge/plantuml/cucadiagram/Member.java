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

import net.sourceforge.plantuml.Guillemet;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlMode;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.skin.VisibilityModifier;

public class Member implements CharSequence {

	private final String display;
	private final CharSequence raw;
	private final boolean staticModifier;
	private final boolean abstractModifier;
	private final Url url;
	private final boolean hasUrl;

	private final VisibilityModifier visibilityModifier;

	@Override
	public String toString() {
		return raw.toString();
	}

	public char charAt(int index) {
		return raw.charAt(index);
	}

	public int length() {
		return raw.length();
	}

	public CharSequence subSequence(int start, int end) {
		return raw.subSequence(start, end);
	}

	public static Member method(CharSequence tmpDisplay) {
		return new Member(true, tmpDisplay, true);
	}

	public static Member field(CharSequence tmpDisplay) {
		return new Member(true, tmpDisplay, false);
	}

	public static Member method(CharSequence tmpDisplay, boolean manageModifier) {
		return new Member(manageModifier, tmpDisplay, true);
	}

	public static Member field(CharSequence tmpDisplay, boolean manageModifier) {
		return new Member(manageModifier, tmpDisplay, false);
	}

	private Member(boolean manageModifier, CharSequence tmpDisplay, boolean isMethod) {
		this.raw = tmpDisplay;
		tmpDisplay = tmpDisplay.toString().replaceAll("(?i)\\{(method|field)\\}\\s*", "");
		if (manageModifier) {
			final Pattern2 finalUrl = MyPattern.cmpile("^(.*?)(?:\\[(" + UrlBuilder.getRegexp() + ")\\])?$");
			final Matcher2 matcher = finalUrl.matcher(tmpDisplay);
			if (matcher.matches() == false) {
				throw new IllegalStateException();
			}
			tmpDisplay = matcher.group(1);
			final String urlString = matcher.group(2);
			if (urlString == null) {
				this.url = null;
			} else {
				this.url = new UrlBuilder(null, UrlMode.STRICT).getUrl(urlString);
			}
		} else {
			this.url = null;
		}
		this.hasUrl = this.url != null;
		final String lower = StringUtils.goLowerCase(tmpDisplay.toString());

		if (manageModifier) {
			this.staticModifier = lower.contains("{static}") || lower.contains("{classifier}");
			this.abstractModifier = lower.contains("{abstract}");
			String displayClean = tmpDisplay.toString().replaceAll("(?i)\\{(static|classifier|abstract)\\}\\s*", "")
					.trim();
			if (displayClean.length() == 0) {
				displayClean = " ";
			}

			if (VisibilityModifier.isVisibilityCharacter(displayClean)) {
				visibilityModifier = VisibilityModifier.getVisibilityModifier(displayClean, isMethod == false);
				this.display = StringUtils.trin(Guillemet.GUILLEMET.manageGuillemet(displayClean.substring(1)));
			} else {
				this.display = Guillemet.GUILLEMET.manageGuillemet(displayClean);
				visibilityModifier = null;
			}
		} else {
			this.staticModifier = false;
			this.visibilityModifier = null;
			this.abstractModifier = false;
			tmpDisplay = StringUtils.trin(tmpDisplay.toString());
			this.display = tmpDisplay.length() == 0 ? " "
					: Guillemet.GUILLEMET.manageGuillemet(StringUtils.trin(tmpDisplay.toString()));
		}
	}

	public String getDisplay(boolean withVisibilityChar) {
		if (withVisibilityChar) {
			return getDisplayWithVisibilityChar();
		}
		return getDisplayWithoutVisibilityChar();
	}

	private String getDisplayWithoutVisibilityChar() {
		return display;
	}

	private String getDisplayWithVisibilityChar() {
		if (isPrivate()) {
			return "-" + display;
		}
		if (isPublic()) {
			return "+" + display;
		}
		if (isPackagePrivate()) {
			return "~" + display;
		}
		if (isProtected()) {
			return "#" + display;
		}
		if (isIEMandatory()) {
			return "*" + display;
		}
		return display;
	}

	@Override
	public boolean equals(Object obj) {
		final Member other = (Member) obj;
		return this.display.equals(other.display);
	}

	@Override
	public int hashCode() {
		return display.hashCode();
	}

	public final boolean isStatic() {
		return staticModifier;
	}

	public final boolean isAbstract() {
		return abstractModifier;
	}

	private boolean isPrivate() {
		return visibilityModifier == VisibilityModifier.PRIVATE_FIELD
				|| visibilityModifier == VisibilityModifier.PRIVATE_METHOD;
	}

	private boolean isProtected() {
		return visibilityModifier == VisibilityModifier.PROTECTED_FIELD
				|| visibilityModifier == VisibilityModifier.PROTECTED_METHOD;
	}

	private boolean isPublic() {
		return visibilityModifier == VisibilityModifier.PUBLIC_FIELD
				|| visibilityModifier == VisibilityModifier.PUBLIC_METHOD;
	}

	private boolean isPackagePrivate() {
		return visibilityModifier == VisibilityModifier.PACKAGE_PRIVATE_FIELD
				|| visibilityModifier == VisibilityModifier.PACKAGE_PRIVATE_METHOD;
	}

	private boolean isIEMandatory() {
		return visibilityModifier == VisibilityModifier.IE_MANDATORY;
	}

	public final VisibilityModifier getVisibilityModifier() {
		return visibilityModifier;
	}

	public final Url getUrl() {
		return url;
	}

	public boolean hasUrl() {
		return hasUrl;
	}



}
