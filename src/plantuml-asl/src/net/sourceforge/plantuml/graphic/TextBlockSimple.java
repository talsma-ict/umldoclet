/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.graphic;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.EmbeddedDiagram;
import net.sourceforge.plantuml.Guillemet;
import net.sourceforge.plantuml.SpriteContainer;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class TextBlockSimple extends AbstractTextBlock implements TextBlock {

	private List<Line> lines2;

	private final Display texts;
	private final FontConfiguration fontConfiguration;
	private final UFont fontForStereotype;
	private final HorizontalAlignment horizontalAlignment;
	private final SpriteContainer spriteContainer;
	private final double maxMessageSize;
	private final HtmlColor htmlColorForStereotype;

	protected TextBlockSimple(Display texts, FontConfiguration fontConfiguration,
			HorizontalAlignment horizontalAlignment, SpriteContainer spriteContainer, double maxMessageSize) {
		this(texts, fontConfiguration, horizontalAlignment, spriteContainer, maxMessageSize, null, null);
	}

	public TextBlockSimple(Display texts, FontConfiguration fontConfiguration, HorizontalAlignment horizontalAlignment,
			SpriteContainer spriteContainer, double maxMessageSize, UFont fontForStereotype,
			HtmlColor htmlColorForStereotype) {
		this.texts = texts;
		this.fontConfiguration = fontConfiguration;
		this.horizontalAlignment = horizontalAlignment;
		this.spriteContainer = spriteContainer;
		this.maxMessageSize = maxMessageSize;
		this.fontForStereotype = fontForStereotype;
		this.htmlColorForStereotype = htmlColorForStereotype;
	}

	private List<Line> getLines(StringBounder stringBounder) {
		if (lines2 == null) {
			if (stringBounder == null) {
				throw new IllegalStateException();
			}
			this.lines2 = new ArrayList<Line>();
			for (CharSequence s : texts) {
				if (s instanceof Stereotype) {
					lines2.addAll(createLinesForStereotype(
							fontConfiguration.forceFont(fontForStereotype, htmlColorForStereotype), (Stereotype) s,
							horizontalAlignment, spriteContainer));
				} else if (s instanceof EmbeddedDiagram) {
					lines2.add(((EmbeddedDiagram) s).asDraw(null));
				} else {
					addInLines(stringBounder, s.toString());
				}
			}
		}
		return lines2;
	}

	private void addInLines(StringBounder stringBounder, String s) {
		if (maxMessageSize == 0) {
			addSingleLine(s);
		} else if (maxMessageSize > 0) {
			final StringTokenizer st = new StringTokenizer(s, " ", true);
			final StringBuilder currentLine = new StringBuilder();
			while (st.hasMoreTokens()) {
				final String token = st.nextToken();
				final double w = getTextWidth(stringBounder, currentLine + token);
				if (w > maxMessageSize) {
					addSingleLineNoSpace(currentLine.toString());
					currentLine.setLength(0);
					if (token.startsWith(" ") == false) {
						currentLine.append(token);
					}
				} else {
					currentLine.append(token);
				}
			}
			addSingleLineNoSpace(currentLine.toString());
		} else if (maxMessageSize < 0) {
			final StringBuilder currentLine = new StringBuilder();
			for (int i = 0; i < s.length(); i++) {
				final char c = s.charAt(i);
				final double w = getTextWidth(stringBounder, currentLine.toString() + c);
				if (w > -maxMessageSize) {
					addSingleLineNoSpace(currentLine.toString());
					currentLine.setLength(0);
					if (c != ' ') {
						currentLine.append(c);
					}
				} else {
					currentLine.append(c);
				}
			}
			addSingleLineNoSpace(currentLine.toString());
		}
	}

	private void addSingleLineNoSpace(String s) {
		if (s.length() == 0 || MyPattern.mtches(s, "^[%s]*$ ")) {
			return;
		}
		lines2.add(SingleLine.withSomeHtmlTag(s, fontConfiguration, horizontalAlignment, spriteContainer));
	}

	private void addSingleLine(String s) {
		lines2.add(SingleLine.withSomeHtmlTag(s, fontConfiguration, horizontalAlignment, spriteContainer));
	}

	private double getTextWidth(StringBounder stringBounder, String s) {
		final Line line = SingleLine.withSomeHtmlTag(s, fontConfiguration, horizontalAlignment, spriteContainer);
		return line.calculateDimension(stringBounder).getWidth();
	}

	private List<SingleLine> createLinesForStereotype(FontConfiguration fontConfiguration, Stereotype s,
			HorizontalAlignment horizontalAlignment, SpriteContainer spriteContainer) {
		assert s.getLabel(Guillemet.DOUBLE_COMPARATOR) != null;
		final List<SingleLine> result = new ArrayList<SingleLine>();
		for (String st : s.getLabels(spriteContainer.guillemet())) {
			result.add(SingleLine.withSomeHtmlTag(st, fontConfiguration, horizontalAlignment, spriteContainer));
		}
		return Collections.unmodifiableList(result);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return getTextDimension(stringBounder);
	}

	protected final Dimension2D getTextDimension(StringBounder stringBounder) {
		double width = 0;
		double height = 0;
		for (Line line : getLines(stringBounder)) {
			final Dimension2D size2D = line.calculateDimension(stringBounder);
			height += size2D.getHeight();
			width = Math.max(width, size2D.getWidth());
		}
		return new Dimension2DDouble(width, height);
	}

	public void drawU(UGraphic ug) {
		double y = 0;
		final Dimension2D dimText = getTextDimension(ug.getStringBounder());

		for (Line line : getLines(ug.getStringBounder())) {
			final HorizontalAlignment lineHorizontalAlignment = line.getHorizontalAlignment();
			double deltaX = 0;
			if (lineHorizontalAlignment == HorizontalAlignment.CENTER) {
				final double diff = dimText.getWidth() - line.calculateDimension(ug.getStringBounder()).getWidth();
				deltaX = diff / 2.0;
			} else if (lineHorizontalAlignment == HorizontalAlignment.RIGHT) {
				final double diff = dimText.getWidth() - line.calculateDimension(ug.getStringBounder()).getWidth();
				deltaX = diff;
			}
			line.drawU(ug.apply(new UTranslate(deltaX, y)));
			y += line.calculateDimension(ug.getStringBounder()).getHeight();
		}
	}

}
