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
package net.sourceforge.plantuml.wbs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.Colors;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.AbstractCommonUGraphic;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.AbstractTextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.mindmap.IdeaShape;
import net.sourceforge.plantuml.regex.Matcher2;
import net.sourceforge.plantuml.regex.MyPattern;
import net.sourceforge.plantuml.regex.Pattern2;
import net.sourceforge.plantuml.skin.UmlDiagramType;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.NoStyleAvailableException;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.utils.Direction;

public class WBSDiagram extends UmlDiagram {

	private WElement root;
	private WElement last;
	private String first;
	private final Map<String, WElement> codes = new LinkedHashMap<>();
	private final List<WBSLink> links = new ArrayList<>();

	public DiagramDescription getDescription() {
		return new DiagramDescription("Work Breakdown Structure");
	}

	public WBSDiagram(UmlSource source) {
		super(source, UmlDiagramType.WBS, null);
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {

		return createImageBuilder(fileFormatOption).drawable(getTextBlock()).write(os);
	}

	@Override
	protected TextBlock getTextBlock() {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				drawMe(ug);
			}

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				return getDrawingElement().calculateDimension(stringBounder);
			}
		};
	}

	private void drawMe(UGraphic ug) {
		UTranslate translate = null;
		if (ug instanceof AbstractCommonUGraphic)
			translate = ((AbstractCommonUGraphic) ug).getTranslate();

		final Fork fork = getDrawingElement();
		fork.drawU(ug);

		if (translate == null)
			return;

		ug = ug.apply(translate.reverse());
		for (WBSLink link : links)
			link.drawU(ug);

	}

	private Fork getDrawingElement() {
		return new Fork(getSkinParam(), root);
	}

	public final static Pattern2 patternStereotype = MyPattern
			.cmpile("^\\s*(.*?)(?:\\s*\\<\\<\\s*(.*)\\s*\\>\\>)\\s*$");

	public CommandExecutionResult addIdea(String code, HColor backColor, int level, String label, Direction direction,
			IdeaShape shape) {
		final Matcher2 m = patternStereotype.matcher(label);
		String stereotype = null;
		if (m.matches()) {
			label = m.group(1);
			stereotype = m.group(2);
		}
		final Display display = Display.getWithNewlines(label);
		return addIdea(code, backColor, level, display, stereotype, direction, shape);
	}

	public CommandExecutionResult addIdea(String code, HColor backColor, int level, Display display, String stereotype,
			Direction direction, IdeaShape shape) {
		try {
			if (level == 0) {
				if (root != null)
					return CommandExecutionResult.error("Error 44");

				initRoot(backColor, display, stereotype, shape);
				return CommandExecutionResult.ok();
			}
			return add(code, backColor, level, display, stereotype, direction, shape);
		} catch (NoStyleAvailableException e) {
			// Logme.error(e);
			return CommandExecutionResult.error("General failure: no style available.");
		}
	}

	private void initRoot(HColor backColor, Display display, String stereotype, IdeaShape shape) {
		root = new WElement(backColor, display, stereotype, getSkinParam().getCurrentStyleBuilder(), shape);
		last = root;
	}

	private WElement getParentOfLast(int nb) {
		WElement result = last;
		for (int i = 0; i < nb; i++)
			result = result.getParent();

		return result;
	}

	public int getSmartLevel(String type) {
		if (root == null) {
			assert first == null;
			first = type;
			return 0;
		}
		type = type.replace('\t', ' ');
		if (type.contains(" ") == false)
			return type.length() - 1;

		if (type.endsWith(first))
			return type.length() - first.length();

		if (type.trim().length() == 1)
			return type.length() - 1;

		if (type.startsWith(first))
			return type.length() - first.length();

		throw new UnsupportedOperationException("type=<" + type + ">[" + first + "]");
	}

	private CommandExecutionResult add(String code, HColor backColor, int level, Display display, String stereotype,
			Direction direction, IdeaShape shape) {
		try {
			if (level == last.getLevel() + 1) {
				final WElement newIdea = last.createElement(backColor, level, display, stereotype, direction, shape,
						getSkinParam().getCurrentStyleBuilder());
				last = newIdea;
				if (code != null)
					codes.put(code, newIdea);
				return CommandExecutionResult.ok();
			}
			if (level <= last.getLevel()) {
				final int diff = last.getLevel() - level + 1;
				final WElement newIdea = getParentOfLast(diff).createElement(backColor, level, display, stereotype,
						direction, shape, getSkinParam().getCurrentStyleBuilder());
				last = newIdea;
				if (code != null)
					codes.put(code, newIdea);
				return CommandExecutionResult.ok();
			}
			return CommandExecutionResult.error("Bad tree structure");
		} catch (NoStyleAvailableException e) {
			// Logme.error(e);
			return CommandExecutionResult.error("General failure: no style available.");
		}
	}

	public CommandExecutionResult link(String code1, String code2, Colors colors, Stereotype stereotype) {
		final WElement element1 = codes.get(code1);
		if (element1 == null)
			return CommandExecutionResult.error("No such node " + code1);
		final WElement element2 = codes.get(code2);
		if (element2 == null)
			return CommandExecutionResult.error("No such node " + code2);
		HColor color = colors.getColor(ColorType.LINE);

		if (color == null) {
			final Style style = StyleSignatureBasic.of(SName.root, SName.element, SName.wbsDiagram, SName.arrow)
					.withTOBECHANGED(stereotype).getMergedStyle(getCurrentStyleBuilder());

			color = style.value(PName.LineColor).asColor(getSkinParam().getIHtmlColorSet());
		}

		links.add(new WBSLink(element1, element2, color));

		return CommandExecutionResult.ok();
	}

}
