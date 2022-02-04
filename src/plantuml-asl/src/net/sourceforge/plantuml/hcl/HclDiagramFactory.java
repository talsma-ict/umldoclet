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
package net.sourceforge.plantuml.hcl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.command.PSystemAbstractFactory;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.jsondiagram.JsonDiagram;
import net.sourceforge.plantuml.jsondiagram.StyleExtractor;

public class HclDiagramFactory extends PSystemAbstractFactory {

	public HclDiagramFactory() {
		super(DiagramType.HCL);
	}

	@Override
	public Diagram createSystem(UmlSource source, ISkinSimple skinParam) {
		final List<String> highlighted = new ArrayList<>();
		JsonValue data = null;
		StyleExtractor styleExtractor = null;
		try {
			final HclSource list = new HclSource();
			styleExtractor = new StyleExtractor(source.iterator2());
			final Iterator<String> it = styleExtractor.getIterator();
			it.next();
			while (true) {
				final String line = it.next();
				if (it.hasNext() == false)
					break;

				list.add(line);
			}
			HclParser parser = new HclParser(list);
			data = parser.parseMe();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final JsonDiagram result = new JsonDiagram(source, UmlDiagramType.HCL, data, highlighted);
//		if (styleExtractor != null) {
//			styleExtractor.applyStyles(result.getSkinParam());
//			final String title = styleExtractor.getTitle();
//			if (title != null)
//				result.setTitle(DisplayPositioned.single(Display.getWithNewlines(title), HorizontalAlignment.CENTER,
//						VerticalAlignment.CENTER));
//		}
		return result;
	}

}
