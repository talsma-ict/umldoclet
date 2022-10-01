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
package net.sourceforge.plantuml.jsondiagram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.command.PSystemAbstractFactory;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.json.Json;
import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.json.ParseException;

public class JsonDiagramFactory extends PSystemAbstractFactory {

	public JsonDiagramFactory() {
		super(DiagramType.JSON);
	}

	@Override
	public Diagram createSystem(UmlSource source, Map<String, String> skinParam) {
		final List<String> highlighted = new ArrayList<>();
		StyleExtractor styleExtractor = null;
		JsonValue json;
		try {
			final StringBuilder sb = new StringBuilder();
			styleExtractor = new StyleExtractor(source.iterator2());
			final Iterator<String> it = styleExtractor.getIterator();
			it.next();
			while (true) {
				final String line = it.next();
				if (it.hasNext() == false)
					break;

				if (line.startsWith("#")) {
					if (line.startsWith("#highlight ")) {
						highlighted.add(line.substring("#highlight ".length()).trim());
						continue;
					}
				} else {
					sb.append(line);
					sb.append(BackSlash.CHAR_NEWLINE);
				}
			}
			json = Json.parse(sb.toString());
		} catch (ParseException e) {
			json = null;
		}
		final JsonDiagram result = new JsonDiagram(source, UmlDiagramType.JSON, json, highlighted);
		if (styleExtractor != null)
			styleExtractor.applyStyles(result.getSkinParam());

		return result;
	}

}
