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
package net.sourceforge.plantuml;

import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;

public class UrlBuilder {

	private static final String S_QUOTED = "\\[\\[[%s]*" + //
			"[%g]([^%g]+)[%g]" + // Quoted part
			"(?:[%s]*\\{([^{}]*)\\})?" + // Optional tooltip
			"(?:[%s]([^%s\\{\\}\\[\\]][^\\[\\]]*))?" + // Optional label
			"[%s]*\\]\\]";

	private static final String S_ONLY_TOOLTIP = "\\[\\[[%s]*" + //
			"\\{(.*)\\}" + // Tooltip
			"[%s]*\\]\\]";

	private static final String S_ONLY_TOOLTIP_AND_LABEL = "\\[\\[[%s]*" + //
			"\\{([^{}]*)\\}" + // Tooltip
			"[%s]*" + //
			"([^\\[%s\\{\\}\\[\\]][^\\[\\]]*)" // Label
			+ "[%s]*\\]\\]";

	private static final String S_LINK_TOOLTIP_NOLABEL = "\\[\\[[%s]*" + //
			"([^\\s%g{}\\[\\]]+?)" + // Link
			"[%s]*\\{(.+)\\}" + // Tooltip
			"[%s]*\\]\\]";

	private static final String S_LINK_WITH_OPTIONAL_TOOLTIP_WITH_OPTIONAL_LABEL = "\\[\\[[%s]*" + //
			"([^%s%g\\[\\]]+?)" + // Link
			"(?:[%s]*\\{([^{}]*)\\})?" + // Optional tooltip
			"(?:[%s]([^%s\\{\\}\\[\\]][^\\[\\]]*))?" + // Optional label
			"[%s]*\\]\\]";

	public static String getRegexp() {
		return S_QUOTED + "|" + //
				S_ONLY_TOOLTIP + "|" + //
				S_ONLY_TOOLTIP_AND_LABEL + "|" + //
				S_LINK_TOOLTIP_NOLABEL + "|" + //
				S_LINK_WITH_OPTIONAL_TOOLTIP_WITH_OPTIONAL_LABEL;
	}

	private static final Pattern2 QUOTED = MyPattern.cmpile(S_QUOTED);
	private static final Pattern2 ONLY_TOOLTIP = MyPattern.cmpile(S_ONLY_TOOLTIP);
	private static final Pattern2 ONLY_TOOLTIP_AND_LABEL = MyPattern.cmpile(S_ONLY_TOOLTIP_AND_LABEL);
	private static final Pattern2 LINK_TOOLTIP_NOLABEL = MyPattern.cmpile(S_LINK_TOOLTIP_NOLABEL);
	private static final Pattern2 LINK_WITH_OPTIONAL_TOOLTIP_WITH_OPTIONAL_LABEL = MyPattern
			.cmpile(S_LINK_WITH_OPTIONAL_TOOLTIP_WITH_OPTIONAL_LABEL);

	private final String topurl;
	private UrlMode mode;

	public UrlBuilder(String topurl, UrlMode mode) {
		this.topurl = topurl;
		this.mode = mode;
	}

	public Url getUrl(String s) {
		Matcher2 m;
		m = QUOTED.matcher(s);
		if (matchesOrFind(m)) {
			return new Url(withTopUrl(m.group(1)), m.group(2), m.group(3));
		}

		m = ONLY_TOOLTIP.matcher(s);
		if (matchesOrFind(m)) {
			return new Url("", m.group(1), null);
		}

		m = ONLY_TOOLTIP_AND_LABEL.matcher(s);
		if (matchesOrFind(m)) {
			return new Url("", m.group(1), m.group(2));
		}

		m = LINK_TOOLTIP_NOLABEL.matcher(s);
		if (matchesOrFind(m)) {
			return new Url(withTopUrl(m.group(1)), m.group(2), null);
		}

		m = LINK_WITH_OPTIONAL_TOOLTIP_WITH_OPTIONAL_LABEL.matcher(s);
		if (matchesOrFind(m)) {
			return new Url(withTopUrl(m.group(1)), m.group(2), m.group(3));
		}

		return null;

	}

	private boolean matchesOrFind(Matcher2 m) {
		if (mode == UrlMode.STRICT) {
			return m.matches();
		} else if (mode == UrlMode.ANYWHERE) {
			return m.find();
		} else {
			throw new IllegalStateException();
		}
	}

	private String withTopUrl(String url) {
		if (url.startsWith("http:") == false && url.startsWith("https:") == false && url.startsWith("file:") == false
				&& topurl != null) {
			return topurl + url;
		}
		return url;
	}

}
