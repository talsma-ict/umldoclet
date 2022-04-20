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
package net.sourceforge.plantuml.openiconic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.PlainDiagram;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.donors.PSystemDonors;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockHorizontal;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.openiconic.data.DummyIcon;

public class PSystemListOpenIconic extends PlainDiagram {

	public PSystemListOpenIconic(UmlSource source) {
		super(source);
	}

	@Override
	protected UDrawable getRootDrawable(FileFormatOption fileFormatOption) throws IOException {
		final List<String> lines = new ArrayList<>();
		lines.add("<b>List Open Iconic");
		lines.add("<i>Credit to");
		lines.add("https://useiconic.com/open");
		lines.add(" ");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(getRessourceAllTxt()))) {
			String s = null;
			while ((s = br.readLine()) != null) {
				// lines.add("<&yen> " + s);
				// System.err.println("s=" + s);
				lines.add("<&" + s + "> " + s);
			}
		}
		final List<TextBlock> cols = PSystemDonors.getCols(lines, 7, 0);
		return new TextBlockHorizontal(cols, VerticalAlignment.TOP);
	}

	private InputStream getRessourceAllTxt() {
		return DummyIcon.class.getResourceAsStream("all.txt");
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Open iconic)");
	}

}
