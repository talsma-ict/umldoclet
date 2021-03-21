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
package net.sourceforge.plantuml.creole.legacy;

import java.awt.font.LineMetrics;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.creole.atom.AbstractAtom;
import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorAutomatic;
import net.sourceforge.plantuml.ugraphic.color.HColorSimple;
import net.sourceforge.plantuml.utils.CharHidder;

public final class AtomText extends AbstractAtom implements Atom {

	protected interface DelayedDouble {
		public double getDouble(StringBounder stringBounder);
	}

	private final FontConfiguration fontConfiguration;
	private final String text;
	private final DelayedDouble marginLeft;
	private final DelayedDouble marginRight;
	private final Url url;
	private final boolean manageSpecialChars;
	private TextBlock visibility;

	protected AtomText(String text, FontConfiguration style, Url url, DelayedDouble marginLeft,
			DelayedDouble marginRight, boolean manageSpecialChars) {
		if (text.contains("" + BackSlash.hiddenNewLine())) {
			throw new IllegalArgumentException(text);
		}

//		if (text.length() > 0) {
//			final VisibilityModifier visibilityModifier = VisibilityModifier.getByUnicode(text.charAt(0));
//			if (visibilityModifier != null) {
//				final HColor back = HColorUtils.GREEN;
//				final HColor fore = HColorUtils.RED;
//				visibility = visibilityModifier.getUBlock(11, fore, back, url != null);
//				text = text.substring(1);
//			}
//		}

		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		String s = CharHidder.unhide(text);
		if (manageSpecialChars) {
			s = StringUtils.showComparatorCharacters(s);
			s = StringUtils.manageAmpDiese(s);
			s = StringUtils.manageUnicodeNotationUplus(s);
			s = StringUtils.manageTildeArobaseStart(s);
			s = StringUtils.manageEscapedTabs(s);
		}
		this.manageSpecialChars = manageSpecialChars;
		this.text = s;
		this.fontConfiguration = style;
		this.url = url;
	}

	private AtomText withText(String text) {
		return new AtomText(text, fontConfiguration, url, marginLeft, marginRight, manageSpecialChars);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D rect = stringBounder.calculateDimension(fontConfiguration.getFont(), text);
		Log.debug("g2d=" + rect);
		Log.debug("Size for " + text + " is " + rect);
		double h = rect.getHeight();
		if (h < 10) {
			h = 10;
		}
		double width = text.indexOf("\t") == -1 ? rect.getWidth() : getWidth(stringBounder, text);
		final double left = marginLeft.getDouble(stringBounder);
		final double right = marginRight.getDouble(stringBounder);
		if (visibility != null) {
			width += visibility.calculateDimension(stringBounder).getWidth();
		}

		return new Dimension2DDouble(width + left + right, h);
	}

	public void drawU(UGraphic ug) {
		if (url != null) {
			ug.startUrl(url);
		}
		if (ug.matchesProperty("SPECIALTXT")) {
			ug.draw(this);
		} else {
			if (visibility != null) {
				visibility.drawU(ug.apply(UTranslate.dy(2)));
				final double width = visibility.calculateDimension(ug.getStringBounder()).getWidth();
				ug = ug.apply(UTranslate.dx(width));
			}
			HColor textColor = fontConfiguration.getColor();
			FontConfiguration useFontConfiguration = fontConfiguration;
			if (textColor instanceof HColorAutomatic && ug.getParam().getBackcolor() != null) {
				textColor = ((HColorSimple) ug.getParam().getBackcolor()).opposite();
				useFontConfiguration = fontConfiguration.changeColor(textColor);
			}
			if (marginLeft != AtomTextUtils.ZERO) {
				ug = ug.apply(UTranslate.dx(marginLeft.getDouble(ug.getStringBounder())));
			}

			final StringTokenizer tokenizer = new StringTokenizer(text, "\t", true);

			// final int ypos = fontConfiguration.getSpace();
			final Dimension2D rect = ug.getStringBounder().calculateDimension(fontConfiguration.getFont(), text);
			final double descent = getDescent();
			final double ypos = rect.getHeight() - descent;

			double x = 0;
			if (tokenizer.hasMoreTokens()) {
				final double tabSize = getTabSize(ug.getStringBounder());
				while (tokenizer.hasMoreTokens()) {
					final String s = tokenizer.nextToken();
					if (s.equals("\t")) {
						final double remainder = x % tabSize;
						x += tabSize - remainder;
					} else {
						final Dimension2D dim = ug.getStringBounder().calculateDimension(fontConfiguration.getFont(),
								s);
						final UText utext = new UText(s, useFontConfiguration);
						ug.apply(new UTranslate(x, ypos)).draw(utext);
						x += dim.getWidth();
					}
				}
			}
		}
		if (url != null) {
			ug.closeUrl();
		}
	}

	private double getWidth(StringBounder stringBounder, String text) {
		final StringTokenizer tokenizer = new StringTokenizer(text, "\t", true);
		final double tabSize = getTabSize(stringBounder);
		double x = 0;
		while (tokenizer.hasMoreTokens()) {
			final String s = tokenizer.nextToken();
			if (s.equals("\t")) {
				final double remainder = x % tabSize;
				x += tabSize - remainder;
			} else {
				final Dimension2D dim = stringBounder.calculateDimension(fontConfiguration.getFont(), s);
				x += dim.getWidth();
			}
		}
		return x;
	}

	private String tabString() {
		final int nb = fontConfiguration.getTabSize();
		if (nb >= 1 && nb < 7) {
			return "        ".substring(0, nb);
		}
		return "        ";
	}

	private double getDescent() {
		final LineMetrics fm = TextBlockUtils.getLineMetrics(fontConfiguration.getFont(), text);
		return fm.getDescent();
	}

	private double getTabSize(StringBounder stringBounder) {
		return stringBounder.calculateDimension(fontConfiguration.getFont(), tabString()).getWidth();
	}

	private final Collection<String> splitted() {
		final List<String> result = new ArrayList<String>();
		for (int i = 0; i < text.length(); i++) {
			final char ch = text.charAt(i);
			if (isOfWord(ch)) {
				final StringBuilder tmp = new StringBuilder();
				tmp.append(ch);
				while (i + 1 < text.length() && isOfWord(text.charAt(i + 1))) {
					i++;
					tmp.append(text.charAt(i));
				}
				result.add(tmp.toString());
			} else {
				result.add("" + text.charAt(i));
			}
		}
		return result;
	}

	public List<Atom> getSplitted(StringBounder stringBounder, LineBreakStrategy maxWidthAsString) {
		final double maxWidth = maxWidthAsString.getMaxWidth();
		if (maxWidth == 0) {
			throw new IllegalStateException();
		}
		final List<Atom> result = new ArrayList<Atom>();
		final StringTokenizer st = new StringTokenizer(text, " ", true);
		final StringBuilder currentLine = new StringBuilder();
		while (st.hasMoreTokens()) {
			final String token1 = st.nextToken();
			for (String tmp : Arrays.asList(token1)) {
				final double w = getWidth(stringBounder, currentLine + tmp);
				if (w > maxWidth) {
					result.add(withText(currentLine.toString()));
					currentLine.setLength(0);
					if (tmp.startsWith(" ") == false) {
						currentLine.append(tmp);
					}
				} else {
					currentLine.append(tmp);
				}
			}
		}
		result.add(withText(currentLine.toString()));
		return Collections.unmodifiableList(result);
	}

	@Override
	public List<Atom> splitInTwo(StringBounder stringBounder, double width) {
		final StringBuilder tmp = new StringBuilder();
		for (String token : splitted()) {
			if (tmp.length() > 0 && getWidth(stringBounder, tmp.toString() + token) > width) {
				final Atom part1 = withText(tmp.toString());
				String remain = text.substring(tmp.length());
				while (remain.startsWith(" ")) {
					remain = remain.substring(1);
				}

				final Atom part2 = withText(remain);
				return Arrays.asList(part1, part2);
			}
			tmp.append(token);
		}
		return Collections.singletonList((Atom) this);
	}

	private boolean isOfWord(char ch) {
		return Character.isWhitespace(ch) == false;
	}

	public final String getText() {
		return text;
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return fontConfiguration.getSpace();
	}

	@Override
	public String toString() {
		return text + " " + fontConfiguration;
	}

	public FontConfiguration getFontConfiguration() {
		return fontConfiguration;
	}

}
