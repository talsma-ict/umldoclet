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
package net.sourceforge.plantuml.jsondiagram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.parser.StyleParser;
import net.sourceforge.plantuml.style.parser.StyleParsingException;
import net.sourceforge.plantuml.text.StringLocated;
import net.sourceforge.plantuml.utils.BlocLines;

public class StyleExtractor {

	private final List<String> list = new ArrayList<>();
	private final List<StringLocated> style = new ArrayList<>();
	private String title = null;
	private boolean handwritten = false;
	private double scale = 1;

	public StyleExtractor(Iterator<StringLocated> data) {
		while (data.hasNext()) {
			StringLocated line = data.next();
			final String s = line.getString().trim();
			if (s.length() == 0)
				continue;
			if (startStyle(s)) {
				while (data.hasNext()) {
					style.add(line);
					if (endStyle(line))
						break;
					line = data.next();
				}
			} else if (list.size() >= 1 && s.startsWith("!assume ")) {
				// Ignore
			} else if (list.size() >= 1 && s.startsWith("!pragma ")) {
				// Ignore
			} else if (list.size() >= 1 && s.startsWith("hide ")) {
				// Ignore
			} else if (list.size() >= 1 && s.startsWith("scale ")) {
				// Ignore
				try {
					final double v = Double.parseDouble(s.replaceAll("\\D", ""));
					if (v > 0)
						scale = v;
				} catch (Exception e) {
				}
			} else if (list.size() >= 1 && s.startsWith("title ")) {
				this.title = s.substring("title ".length()).trim();
			} else if (list.size() >= 1 && s.startsWith("skinparam ")) {
				if (s.contains("handwritten") && s.contains("true"))
					handwritten = true;
				if (s.contains("{")) {
					while (data.hasNext()) {
						if (line.getString().trim().equals("}"))
							break;
						line = data.next();
					}
				}
			} else {
				list.add(line.getString());
			}
		}

	}

	private boolean startStyle(String line) {
		return line.equals("<style>");
	}

	private boolean endStyle(StringLocated line) {
		return line.getString().trim().equals("</style>");
	}

	public void applyStyles(ISkinParam skinParam) throws StyleParsingException {
		if (style.size() > 0) {
			final StyleBuilder styleBuilder = skinParam.getCurrentStyleBuilder();
			final BlocLines blocLines = BlocLines.from(style);
			for (Style modifiedStyle : StyleParser.parse(blocLines, styleBuilder))
				skinParam.muteStyle(modifiedStyle);
		}
	}

	public Iterator<String> getIterator() {
		return list.iterator();
	}

	public String getTitle() {
		return title;
	}

	public final boolean isHandwritten() {
		return handwritten;
	}

	public double getScale() {
		return scale;
	}

}
