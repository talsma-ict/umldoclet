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
package net.sourceforge.plantuml.ditaa;

import java.awt.Font;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.command.PSystemBasicFactory;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;

public class PSystemDitaaFactory extends PSystemBasicFactory<PSystemDitaa> {

	// private StringBuilder data;
	// // -E,--no-separation
	// private boolean performSeparationOfCommonEdges;
	//
	// // -S,--no-shadows
	// private boolean dropShadows;

	public PSystemDitaaFactory(DiagramType diagramType) {
		super(diagramType);
	}

	@Override
	public PSystemDitaa initDiagram(UmlSource source, String startLine) {
		boolean performSeparationOfCommonEdges = true;
		if (startLine != null && (startLine.contains("-E") || startLine.contains("--no-separation")))
			performSeparationOfCommonEdges = false;

		boolean dropShadows = true;
		if (startLine != null && (startLine.contains("-S") || startLine.contains("--no-shadows")))
			dropShadows = false;

		boolean allCornersAreRound = false;
		if (startLine != null && (startLine.contains("-r") || startLine.contains("--round-corners")))
			allCornersAreRound = true;

		boolean transparentBackground = false;
		if (startLine != null && (startLine.contains("-T") || startLine.contains("--transparent")))
			transparentBackground = true;

		boolean forceFontSize = false;
		if (startLine != null && startLine.contains("--font-size"))
			forceFontSize = true;

		final float scale = extractScale(startLine);
		final Font font = extractFont(startLine);
		if (getDiagramType() == DiagramType.UML)
			return null;
		else if (getDiagramType() == DiagramType.DITAA)
			return new PSystemDitaa(source, "", performSeparationOfCommonEdges, dropShadows, allCornersAreRound, transparentBackground, scale, font, forceFontSize);
		else
			throw new IllegalStateException(getDiagramType().name());

	}

	@Override
	public PSystemDitaa executeLine(UmlSource source, PSystemDitaa system, String line) {
		if (system == null && (line.equals("ditaa") || line.startsWith("ditaa("))) {
			boolean performSeparationOfCommonEdges = true;
			if (line.contains("-E") || line.contains("--no-separation"))
				performSeparationOfCommonEdges = false;

			boolean dropShadows = true;
			if (line.contains("-S") || line.contains("--no-shadows"))
				dropShadows = false;

			boolean allCornersAreRound = false;
			if (line.contains("-r") || line.contains("--round-corners"))
				allCornersAreRound = true;

			boolean transparentBackground = false;
			if (line.contains("-T") || line.contains("--transparent"))
				transparentBackground = true;

			boolean forceFontSize = false;
			if (line.contains("--font-size"))
				forceFontSize = true;

			final float scale = extractScale(line);
			final Font font = extractFont(line);
			return new PSystemDitaa(source, "", performSeparationOfCommonEdges, dropShadows, allCornersAreRound, transparentBackground, scale, font, forceFontSize);
		}
		if (system == null)
			return null;

		return system.add(line);
	}

	private float extractScale(String line) {
		if (line == null)
			return 1;

		final Pattern p = Pattern.compile("scale=([\\d.]+)");
		final Matcher m = p.matcher(line);
		if (m.find()) {
			final String number = m.group(1);
			return Float.parseFloat(number);
		}
		return 1;
	}

	private Font extractFont(String line) {
		if (line == null)
			return new Font("Dialog", Font.BOLD, 12);

		final Pattern pName = Pattern.compile("font-family=([a-zA-Z0-0 ]+)");
		final Matcher mName = pName.matcher(line);
		String fontName = "Dialog";
		if (mName.find())
		{
			fontName = mName.group(1);
		}

		final Pattern pVariant = Pattern.compile("font-variant=(BOLD|ITALIC|PLAIN)");
		final Matcher mVariant = pVariant.matcher(line);
		int fontVariant = Font.BOLD;
		if (mVariant.find())
		{
			switch (mVariant.group(1))
			{
				case "BOLD":
					fontVariant = Font.BOLD;
					break;
				case "ITALIC":
					fontVariant = Font.ITALIC;
					break;
				case "PLAIN":
					fontVariant = Font.PLAIN;
					break;
			}
		}

		final Pattern pSize = Pattern.compile("font-size=([\\d]+)");
		final Matcher mSize = pSize.matcher(line);
		int fontSize = 12;
		if (mSize.find())
		{
			fontSize = Integer.parseInt(mSize.group(1));
		}

		return new Font(fontName, fontVariant, fontSize);
	}
}
