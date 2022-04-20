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
package net.sourceforge.plantuml.wbs;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.mindmap.IdeaShape;
import net.sourceforge.plantuml.style.NoStyleAvailableException;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class WBSDiagram extends UmlDiagram {

	public DiagramDescription getDescription() {
		return new DiagramDescription("Work Breakdown Structure");
	}

	public WBSDiagram(ThemeStyle style, UmlSource source) {
		super(style, source, UmlDiagramType.WBS, null);
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {

		return createImageBuilder(fileFormatOption).drawable(getTextBlock()).write(os);
	}

	private TextBlockBackcolored getTextBlock() {
		return new TextBlockBackcolored() {

			public void drawU(UGraphic ug) {
				drawMe(ug);
			}

			public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
				return null;
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return getDrawingElement().calculateDimension(stringBounder);

			}

			public MinMax getMinMax(StringBounder stringBounder) {
				throw new UnsupportedOperationException();
			}

			public HColor getBackcolor() {
				return null;
			}
		};
	}

	private void drawMe(UGraphic ug) {
		getDrawingElement().drawU(ug);

	}

	private TextBlock getDrawingElement() {
		return new Fork(getSkinParam(), root);
	}

	public final static Pattern2 patternStereotype = MyPattern
			.cmpile("^\\s*(.*?)(?:\\s*\\<\\<\\s*(.*)\\s*\\>\\>)\\s*$");

	public CommandExecutionResult addIdea(HColor backColor, int level, String label, Direction direction,
			IdeaShape shape) {
		final Matcher2 m = patternStereotype.matcher(label);
		String stereotype = null;
		if (m.matches()) {
			label = m.group(1);
			stereotype = m.group(2);
		}
		final Display display = Display.getWithNewlines(label);
		return addIdea(backColor, level, display, stereotype, direction, shape);
	}

	public CommandExecutionResult addIdea(HColor backColor, int level, Display display, String stereotype,
			Direction direction, IdeaShape shape) {
		try {
			if (level == 0) {
				if (root != null) {
					return CommandExecutionResult.error("Error 44");
				}
				initRoot(backColor, display, stereotype, shape);
				return CommandExecutionResult.ok();
			}
			return add(backColor, level, display, stereotype, direction, shape);
		} catch (NoStyleAvailableException e) {
			// e.printStackTrace();
			return CommandExecutionResult.error("General failure: no style available.");
		}
	}

	private WElement root;
	private WElement last;
	private String first;

	private void initRoot(HColor backColor, Display display, String stereotype, IdeaShape shape) {
		root = new WElement(backColor, display, stereotype, getSkinParam().getCurrentStyleBuilder(), shape);
		last = root;
	}

	private WElement getParentOfLast(int nb) {
		WElement result = last;
		for (int i = 0; i < nb; i++) {
			result = result.getParent();
		}
		return result;
	}

	public int getSmartLevel(String type) {
		if (root == null) {
			assert first == null;
			first = type;
			return 0;
		}
		type = type.replace('\t', ' ');
		if (type.contains(" ") == false) {
			return type.length() - 1;
		}
		if (type.endsWith(first)) {
			return type.length() - first.length();
		}
		if (type.trim().length() == 1) {
			return type.length() - 1;
		}
		if (type.startsWith(first)) {
			return type.length() - first.length();
		}
		throw new UnsupportedOperationException("type=<" + type + ">[" + first + "]");
	}

	private CommandExecutionResult add(HColor backColor, int level, Display display, String stereotype,
			Direction direction, IdeaShape shape) {
		try {
			if (level == last.getLevel() + 1) {
				final WElement newIdea = last.createElement(backColor, level, display, stereotype, direction, shape,
						getSkinParam().getCurrentStyleBuilder());
				last = newIdea;
				return CommandExecutionResult.ok();
			}
			if (level <= last.getLevel()) {
				final int diff = last.getLevel() - level + 1;
				final WElement newIdea = getParentOfLast(diff).createElement(backColor, level, display, stereotype,
						direction, shape, getSkinParam().getCurrentStyleBuilder());
				last = newIdea;
				return CommandExecutionResult.ok();
			}
			return CommandExecutionResult.error("Bad tree structure");
		} catch (NoStyleAvailableException e) {
			// e.printStackTrace();
			return CommandExecutionResult.error("General failure: no style available.");
		}
	}

}
