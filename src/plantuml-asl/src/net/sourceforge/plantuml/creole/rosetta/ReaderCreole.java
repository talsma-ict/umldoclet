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

public class ReaderCreole extends ReaderAbstractWiki implements ReaderWiki {

	@Override
	protected String singleLineFormat(String wiki) {

		// Legacy HTML
		wiki = wiki.replace("<b>", WikiLanguage.UNICODE.tag("strong"));
		wiki = wiki.replace("</b>", WikiLanguage.UNICODE.slashTag("strong"));
		wiki = wiki.replace("<i>", WikiLanguage.UNICODE.tag("em"));
		wiki = wiki.replace("</i>", WikiLanguage.UNICODE.slashTag("em"));

		// Em & Strong
		wiki = wiki.replaceAll("\\*\\*(.+?)\\*\\*",
				WikiLanguage.UNICODE.tag("strong") + "$1" + WikiLanguage.UNICODE.slashTag("strong"));
		wiki = wiki.replaceAll("//(.+?)//",
				WikiLanguage.UNICODE.tag("em") + "$1" + WikiLanguage.UNICODE.slashTag("em"));

		// Strike
		wiki = wiki.replaceAll("--([^-]+?)--",
				WikiLanguage.UNICODE.tag("strike") + "$1" + WikiLanguage.UNICODE.slashTag("strike"));

		return wiki;
	}

	public List<String> transform(List<String> raw) {
		final List<String> uhtml = new ArrayList<String>();
		for (int i = 0; i < raw.size(); i++) {
			String current = raw.get(i);
			uhtml.add(singleLineFormat(current));
		}
		return Collections.unmodifiableList(uhtml);
	}

}
