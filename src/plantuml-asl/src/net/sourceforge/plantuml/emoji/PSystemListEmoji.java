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
package net.sourceforge.plantuml.emoji;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.PlainDiagram;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class PSystemListEmoji extends PlainDiagram {

	private final String text;

	@Override
	protected UDrawable getRootDrawable(FileFormatOption fileFormatOption) throws IOException {
		return new UDrawable() {
			public void drawU(UGraphic ug) {
				final TextBlockBackcolored header = GraphicStrings
						.createBlackOnWhite(Arrays.asList("<b><size:16>Emoji available on Unicode Block " + text,
								"(Blocks available: 26, 27, 1F3, 1F4, 1F5, 1F6, 1F9)"));
				header.drawU(ug);
				final StringBounder stringBounder = ug.getStringBounder();
				ug = ug.apply(UTranslate.dy(header.calculateDimension(stringBounder).getHeight()));

				final UGraphic top = ug;

				final Map<String, Emoji> some = new TreeMap<>();
				for (Map.Entry<String, Emoji> ent : Emoji.getAll().entrySet())
					if (ent.getKey().startsWith(text))
						some.put(ent.getKey(), ent.getValue());

				final int third = (some.size() + 2) / 3;
				int i = 0;

				for (Map.Entry<String, Emoji> ent : some.entrySet()) {
					final String code = ent.getKey();
					final String shortcut = ent.getValue().getShortcut();

					final StringBuilder sb = new StringBuilder();
					sb.append("<size:13>");
					sb.append("\"\"<U+003C>:" + code + ":<U+003E> \"\"");
					sb.append("<:" + code + ":>");
					sb.append(" ");
					sb.append("<#0:" + code + ":>");
					if (shortcut != null) {
						sb.append(" ");
						sb.append("\"\"<U+003C>:" + shortcut + ":<U+003E> \"\"");
					}

					final TextBlockBackcolored tmp = GraphicStrings.createBlackOnWhite(Arrays.asList(sb.toString()));
					tmp.drawU(ug);
					ug = ug.apply(UTranslate.dy(tmp.calculateDimension(stringBounder).getHeight()));

					i++;
					if (i == third)
						ug = top.apply(UTranslate.dx(500));
					if (i == 2 * third)
						ug = top.apply(UTranslate.dx(1000));

				}

			}
		};
	}

	public PSystemListEmoji(UmlSource source, String text) {
		super(source);
		this.text = text;
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(List Emoji)");
	}

}
