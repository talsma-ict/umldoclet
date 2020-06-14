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
package net.sourceforge.plantuml.creole;

import java.awt.font.LineMetrics;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorAutomatic;
import net.sourceforge.plantuml.graphic.HtmlColorSimple;
import net.sourceforge.plantuml.graphic.ImgValign;
import net.sourceforge.plantuml.graphic.Splitter;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.openiconic.OpenIcon;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.sprite.Sprite;
import net.sourceforge.plantuml.utils.CharHidder;

public class AtomText extends AbstractAtom implements Atom {

	interface DelayedDouble {
		public double getDouble(StringBounder stringBounder);
	}

	private static DelayedDouble ZERO = new DelayedDouble() {
		public double getDouble(StringBounder stringBounder) {
			return 0;
		}
	};

	private final FontConfiguration fontConfiguration;
	private final String text;
	private final DelayedDouble marginLeft;
	private final DelayedDouble marginRight;
	private final Url url;

	public static Atom create(String text, FontConfiguration fontConfiguration) {
		return new AtomText(text, fontConfiguration, null, ZERO, ZERO);
	}

	public static Atom createUrl(Url url, FontConfiguration fontConfiguration, ISkinSimple skinSimple) {
		fontConfiguration = fontConfiguration.hyperlink();
		final Display display = Display.getWithNewlines(url.getLabel());
		if (display.size() > 1) {
			final List<Atom> all = new ArrayList<Atom>();
			for (CharSequence s : display.as()) {
				all.add(createAtomText(s.toString(), url, fontConfiguration, skinSimple));
			}
			return new AtomVerticalTexts(all);

		}
		return createAtomText(url.getLabel(), url, fontConfiguration, skinSimple);
	}

	private static Atom createAtomText(final String text, Url url, FontConfiguration fontConfiguration,
			ISkinSimple skinSimple) {
		final Pattern p = Pattern.compile(Splitter.openiconPattern + "|" + Splitter.spritePattern2 + "|"
				+ Splitter.imgPatternNoSrcColon);
		final Matcher m = p.matcher(text);
		final List<Atom> result = new ArrayList<Atom>();
		while (m.find()) {
			final StringBuffer sb = new StringBuffer();
			m.appendReplacement(sb, "");
			if (sb.length() > 0) {
				result.add(new AtomText(sb.toString(), fontConfiguration, url, ZERO, ZERO));
			}
			final String valOpenicon = m.group(1);
			final String valSprite = m.group(3);
			final String valImg = m.group(5);
			if (valOpenicon != null) {
				final OpenIcon openIcon = OpenIcon.retrieve(valOpenicon);
				if (openIcon != null) {
					final double scale = CommandCreoleImg.getScale(m.group(2), 1);
					result.add(new AtomOpenIcon(null, scale, openIcon, fontConfiguration, url));
				}
			} else if (valSprite != null) {
				final Sprite sprite = skinSimple.getSprite(valSprite);
				if (sprite != null) {
					final double scale = CommandCreoleImg.getScale(m.group(4), 1);
					result.add(new AtomSprite(null, scale, fontConfiguration, sprite, url));
				}
			} else if (valImg != null) {
				final double scale = CommandCreoleImg.getScale(m.group(6), 1);
				result.add(AtomImg.create(valImg, ImgValign.TOP, 0, scale, url));

			}
		}
		final StringBuffer sb = new StringBuffer();
		m.appendTail(sb);
		if (sb.length() > 0) {
			result.add(new AtomText(sb.toString(), fontConfiguration, url, ZERO, ZERO));
		}
		if (result.size() == 1) {
			return result.get(0);
		}
		return new AtomHorizontalTexts(result);
	}

	public static AtomText createHeading(String text, FontConfiguration fontConfiguration, int order) {
		if (order == 0) {
			fontConfiguration = fontConfiguration.bigger(4).bold();
		} else if (order == 1) {
			fontConfiguration = fontConfiguration.bigger(2).bold();
		} else if (order == 2) {
			fontConfiguration = fontConfiguration.bigger(1).bold();
		} else {
			fontConfiguration = fontConfiguration.italic();
		}
		return new AtomText(text, fontConfiguration, null, ZERO, ZERO);
	}

	public static Atom createListNumber(final FontConfiguration fontConfiguration, final int order, int localNumber) {
		final DelayedDouble left = new DelayedDouble() {
			public double getDouble(StringBounder stringBounder) {
				final Dimension2D dim = stringBounder.calculateDimension(fontConfiguration.getFont(), "9. ");
				return dim.getWidth() * order;
			}
		};
		final DelayedDouble right = new DelayedDouble() {
			public double getDouble(StringBounder stringBounder) {
				final Dimension2D dim = stringBounder.calculateDimension(fontConfiguration.getFont(), ".");
				return dim.getWidth();
			}
		};
		return new AtomText("" + (localNumber + 1) + ".", fontConfiguration, null, left, right);
	}

	@Override
	public String toString() {
		return text + " " + fontConfiguration;
	}

	private AtomText(String text, FontConfiguration style, Url url, DelayedDouble marginLeft, DelayedDouble marginRight) {
		if (text.contains("" + BackSlash.hiddenNewLine())) {
			throw new IllegalArgumentException(text);
		}
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.text = StringUtils.manageTildeArobaseStart(StringUtils.manageUnicodeNotationUplus(StringUtils
				.manageAmpDiese(StringUtils.showComparatorCharacters(CharHidder.unhide(text)))));
		this.fontConfiguration = style;
		this.url = url;
	}

	public FontConfiguration getFontConfiguration() {
		return fontConfiguration;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D rect = stringBounder.calculateDimension(fontConfiguration.getFont(), text);
		Log.debug("g2d=" + rect);
		Log.debug("Size for " + text + " is " + rect);
		double h = rect.getHeight();
		if (h < 10) {
			h = 10;
		}
		final double width = text.indexOf('\t') == -1 ? rect.getWidth() : getWidth(stringBounder);
		final double left = marginLeft.getDouble(stringBounder);
		final double right = marginRight.getDouble(stringBounder);

		return new Dimension2DDouble(width + left + right, h);
	}

	private double getDescent() {
		final LineMetrics fm = TextBlockUtils.getLineMetrics(fontConfiguration.getFont(), text);
		final double descent = fm.getDescent();
		return descent;
	}

	public double getFontSize2D() {
		return fontConfiguration.getFont().getSize2D();
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return fontConfiguration.getSpace();
	}

	private double getTabSize(StringBounder stringBounder) {
		return stringBounder.calculateDimension(fontConfiguration.getFont(), tabString()).getWidth();
	}

	private String tabString() {
		final int nb = fontConfiguration.getTabSize();
		if (nb >= 1 && nb < 7) {
			return "        ".substring(0, nb);
		}
		return "        ";
	}

	public void drawU(UGraphic ug) {
		if (ug.matchesProperty("SPECIALTXT")) {
			ug.draw(this);
			return;
		}
		if (url != null) {
			ug.startUrl(url);
		}
		HtmlColor textColor = fontConfiguration.getColor();
		FontConfiguration useFontConfiguration = fontConfiguration;
		if (textColor instanceof HtmlColorAutomatic && ug.getParam().getBackcolor() != null) {
			textColor = ((HtmlColorSimple) ug.getParam().getBackcolor()).opposite();
			useFontConfiguration = fontConfiguration.changeColor(textColor);
		}
		if (marginLeft != ZERO) {
			ug = ug.apply(new UTranslate(marginLeft.getDouble(ug.getStringBounder()), 0));
		}

		final StringTokenizer tokenizer = new StringTokenizer(text, "\t", true);

		double x = 0;
		// final int ypos = fontConfiguration.getSpace();
		final Dimension2D rect = ug.getStringBounder().calculateDimension(fontConfiguration.getFont(), text);
		final double descent = getDescent();
		final double ypos = rect.getHeight() - descent;
		if (tokenizer.hasMoreTokens()) {
			final double tabSize = getTabSize(ug.getStringBounder());
			while (tokenizer.hasMoreTokens()) {
				final String s = tokenizer.nextToken();
				if (s.equals("\t")) {
					final double remainder = x % tabSize;
					x += tabSize - remainder;
				} else {
					final UText utext = new UText(s, useFontConfiguration);
					final Dimension2D dim = ug.getStringBounder().calculateDimension(fontConfiguration.getFont(), s);
					ug.apply(new UTranslate(x, ypos)).draw(utext);
					x += dim.getWidth();
				}
			}
		}
		if (url != null) {
			ug.closeAction();
		}
	}

	private double getWidth(StringBounder stringBounder) {
		return getWidth(stringBounder, text);
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

	public List<AtomText> getSplitted(StringBounder stringBounder, LineBreakStrategy maxWidthAsString) {
		final double maxWidth = maxWidthAsString.getMaxWidth();
		if (maxWidth == 0) {
			throw new IllegalStateException();
		}
		final List<AtomText> result = new ArrayList<AtomText>();
		final StringTokenizer st = new StringTokenizer(text, " ", true);
		final StringBuilder currentLine = new StringBuilder();
		while (st.hasMoreTokens()) {
			final String token1 = st.nextToken();
			for (String tmp : splitLong1(stringBounder, maxWidth, token1)) {
				final double w = getWidth(stringBounder, currentLine + tmp);
				if (w > maxWidth) {
					result.add(new AtomText(currentLine.toString(), fontConfiguration, url, marginLeft, marginRight));
					currentLine.setLength(0);
					if (tmp.startsWith(" ") == false) {
						currentLine.append(tmp);
					}
				} else {
					currentLine.append(tmp);
				}
			}
		}
		result.add(new AtomText(currentLine.toString(), fontConfiguration, url, marginLeft, marginRight));
		return Collections.unmodifiableList(result);

	}

	@Override
	public List<Atom> splitInTwo(StringBounder stringBounder, double width) {
		final StringTokenizer st = new StringTokenizer(text, " ", true);
		final StringBuilder tmp = new StringBuilder();
		while (st.hasMoreTokens()) {
			final String token = st.nextToken();
			if (tmp.length() > 0 && getWidth(stringBounder, tmp.toString() + token) > width) {
				final Atom part1 = new AtomText(tmp.toString(), fontConfiguration, url, marginLeft, marginRight);
				String remain = text.substring(tmp.length());
				while (remain.startsWith(" ")) {
					remain = remain.substring(1);
				}

				final Atom part2 = new AtomText(remain, fontConfiguration, url, marginLeft, marginRight);
				return Arrays.asList(part1, part2);
			}
			tmp.append(token);
		}
		return Collections.singletonList((Atom) this);
	}

	private List<String> splitLong1(StringBounder stringBounder, double maxWidth, String add) {
		return Arrays.asList(add);
	}

	private List<String> splitLong2(StringBounder stringBounder, double maxWidth, String add) {
		final List<String> result = new ArrayList<String>();
		if (getWidth(stringBounder, add) <= maxWidth) {
			result.add(add);
			return result;
		}
		final StringBuilder current = new StringBuilder();
		for (int i = 0; i < add.length(); i++) {
			final char c = add.charAt(i);
			if (getWidth(stringBounder, current.toString() + c) > maxWidth) {
				result.add(current.toString());
				current.setLength(0);
			}
			current.append(c);
		}
		if (current.length() > 0) {
			result.add(current.toString());
		}
		return result;
	}

	public final String getText() {
		return text;
	}

}
