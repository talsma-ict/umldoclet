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
package net.sourceforge.plantuml.definition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.PlainDiagram;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class PSystemDefinition extends PlainDiagram implements UDrawable {

	private final List<String> lines = new ArrayList<>();
	private final String startLine;

	public PSystemDefinition(UmlSource source, String startLine) {
		super(source);
		this.startLine = startLine;
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Definition)");
	}

	@Override
	protected UDrawable getRootDrawable(FileFormatOption fileFormatOption) throws IOException {
		return this;
	}

	public void drawU(UGraphic ug) {
		final UFont font = UFont.sansSerif(14);
		final FontConfiguration fc = FontConfiguration.create(font, HColorUtils.BLACK, HColorUtils.BLACK, false);
		Display.getWithNewlines(startLine).create(fc, HorizontalAlignment.LEFT, new SpriteContainerEmpty()).drawU(ug);
	}

	public void doCommandLine(String line) {
		this.lines.add(line);
	}

}
