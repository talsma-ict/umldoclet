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
package net.sourceforge.plantuml.creole.legacy;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.creole.Neutron;
import net.sourceforge.plantuml.creole.NeutronType;
import net.sourceforge.plantuml.creole.atom.AbstractAtom;
import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
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

	protected AtomText(String text, FontConfiguration style, Url url, DelayedDouble marginLeft,
			DelayedDouble marginRight, boolean manageSpecialChars) {
		if (text.contains("" + BackSlash.hiddenNewLine()))
			throw new IllegalArgumentException(text);

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

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		final XDimension2D rect = stringBounder.calculateDimension(fontConfiguration.getFont(), text);
		Log.debug("g2d=" + rect);
		Log.debug("Size for " + text + " is " + rect);
		double h = rect.getHeight();
		if (h < 10)
			h = 10;

		double width = text.indexOf("\t") == -1 ? rect.getWidth() : getWidth(stringBounder, text);
		final double left = marginLeft.getDouble(stringBounder);
		final double right = marginRight.getDouble(stringBounder);
		return new XDimension2D(width + left + right, h);
	}

	public void drawU(UGraphic ug) {
		if (url != null)
			ug.startUrl(url);

		if (ug.matchesProperty("SPECIALTXT")) {
			ug.draw(this);
		} else {
			HColor textColor = fontConfiguration.getColor();
			FontConfiguration useFontConfiguration = fontConfiguration;

			HColor backcolor = ug.getParam().getBackcolor();
			if (backcolor.isTransparent())
				backcolor = ug.getDefaultBackground();

			textColor = textColor.getAppropriateColor(backcolor);
			useFontConfiguration = fontConfiguration.changeColor(textColor);

			if (marginLeft != AtomTextUtils.ZERO)
				ug = ug.apply(UTranslate.dx(marginLeft.getDouble(ug.getStringBounder())));

			final StringTokenizer tokenizer = new StringTokenizer(text, "\t", true);

			// final int ypos = fontConfiguration.getSpace();
			final XDimension2D rect = ug.getStringBounder().calculateDimension(fontConfiguration.getFont(), text);
			final double descent = getDescent(ug.getStringBounder());
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
						final XDimension2D dim = ug.getStringBounder().calculateDimension(fontConfiguration.getFont(),
								s);
						final UText utext = new UText(s, useFontConfiguration);
						ug.apply(new UTranslate(x, ypos)).draw(utext);
						x += dim.getWidth();
					}
				}
			}
		}
		if (url != null)
			ug.closeUrl();

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
				final XDimension2D dim = stringBounder.calculateDimension(fontConfiguration.getFont(), s);
				x += dim.getWidth();
			}
		}
		return x;
	}

	private String tabString() {
		final int nb = fontConfiguration.getTabSize();
		if (nb >= 1 && nb < 7)
			return "        ".substring(0, nb);

		return "        ";
	}

	private double getDescent(StringBounder stringBounder) {
		return stringBounder.getDescent(fontConfiguration.getFont(), text);
	}

	private double getTabSize(StringBounder stringBounder) {
		return stringBounder.calculateDimension(fontConfiguration.getFont(), tabString()).getWidth();
	}

	@Override
	public final List<Neutron> getNeutrons() {
		final List<Neutron> result = new ArrayList<>();
		final StringBuilder pending = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			final char ch = text.charAt(i);
			if (pending.length() == 0) {
				pending.append(ch);
				continue;
			}
			final NeutronType pendingType = Neutron.getNeutronTypeFromChar(pending.charAt(0));
			final NeutronType currentType = Neutron.getNeutronTypeFromChar(ch);
			if (pendingType != currentType) {
				addPending(result, pending.toString());
				pending.setLength(0);
			}
			pending.append(ch);
			// if (Neutron.isSentenceBoundary(ch) || Neutron.isChineseSentenceBoundary(ch))
			if (Neutron.isChineseSentenceBoundary(ch)) {
				addPending(result, pending.toString());
				pending.setLength(0);
				result.add(Neutron.zwspSeparator());
			}

		}
		if (pending.length() > 0) {
			addPending(result, pending.toString());
		}
		return result;
	}

	private void addPending(List<Neutron> result, String pending) {
		final Neutron tmp = Neutron.create(withText(pending));
		if (tmp.getType() == NeutronType.SPACE)
			result.add(Neutron.zwspSeparator());
		result.add(tmp);
		if (tmp.getType() == NeutronType.SPACE)
			result.add(Neutron.zwspSeparator());
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
