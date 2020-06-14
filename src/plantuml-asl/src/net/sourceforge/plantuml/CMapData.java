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
package net.sourceforge.plantuml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CMapData {

	private final StringBuilder stringBuilder = new StringBuilder();

	public String asString(String nameId) {
		return "<map id=\"" + nameId + "_map\" name=\"" + nameId + "_map\">\n" + stringBuilder.toString() + "</map>\n";
	}

	public boolean containsData() {
		return stringBuilder.length() > 0;
	}

	public void appendString(String s) {
		stringBuilder.append(s);
	}

	public void appendLong(long s) {
		stringBuilder.append(s);
	}

	private void appendUrl(int seq, Url url, double scale) {
		appendString("<area shape=\"rect\" id=\"id");
		appendLong(seq);
		appendString("\" href=\"");
		appendString(url.getUrl());
		appendString("\" title=\"");
		final String tooltip = url.getTooltip().replaceAll("\\\\n", BackSlash.NEWLINE).replaceAll("&", "&#38;")
				.replaceAll("\"", "&#34;").replaceAll("\'", "&#39;");
		appendString(tooltip);
		appendString("\" alt=\"\" coords=\"");
		appendString(url.getCoords(scale));
		appendString("\"/>");

		appendString(BackSlash.NEWLINE);
	}

	public static CMapData cmapString(Set<Url> allUrlEncountered, double scale) {
		final CMapData cmapdata = new CMapData();

		final List<Url> all = new ArrayList<Url>(allUrlEncountered);
		Collections.sort(all, Url.SURFACE_COMPARATOR);

		int seq = 1;
		for (Url u : all) {
			if (u.hasData() == false) {
				continue;
			}
			cmapdata.appendUrl(seq, u, scale);
			seq++;
		}
		return cmapdata;
	}

}
