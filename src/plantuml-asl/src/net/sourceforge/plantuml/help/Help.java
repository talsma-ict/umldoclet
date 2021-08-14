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
package net.sourceforge.plantuml.help;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBlock1;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.UFont;

public class Help extends UmlDiagram {

	private final List<CharSequence> lines = new ArrayList<>();

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Help)");
	}

	public Help(UmlSource source) {
		super(source, UmlDiagramType.HELP);
	}

	@Override
	public ImageBuilder createImageBuilder(FileFormatOption fileFormatOption) throws IOException {
		return super.createImageBuilder(fileFormatOption)
				.annotations(false);
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormat)
			throws IOException {
		final Display display = Display.create(lines);
		final UFont font = UFont.serif(16);
		final FontConfiguration fontConfiguration = FontConfiguration.blackBlueTrue(font);
		final Sheet sheet = Parser.build(fontConfiguration, HorizontalAlignment.LEFT, getSkinParam(), CreoleMode.FULL)
				.createSheet(display);
		final SheetBlock1 sheetBlock = new SheetBlock1(sheet, LineBreakStrategy.NONE, 0);
		return createImageBuilder(fileFormat)
				.drawable(sheetBlock)
				.write(os);
	}

	public void add(CharSequence line) {
		this.lines.add(line);
	}

	@Override
	public ClockwiseTopRightBottomLeft getDefaultMargins() {
		return ClockwiseTopRightBottomLeft.same(0);
	}

}
