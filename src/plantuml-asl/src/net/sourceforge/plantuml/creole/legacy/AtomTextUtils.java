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

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.creole.atom.AtomHorizontalTexts;
import net.sourceforge.plantuml.creole.atom.AtomImg;
import net.sourceforge.plantuml.creole.atom.AtomOpenIcon;
import net.sourceforge.plantuml.creole.atom.AtomSprite;
import net.sourceforge.plantuml.creole.atom.AtomVerticalTexts;
import net.sourceforge.plantuml.creole.legacy.AtomText.DelayedDouble;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.ImgValign;
import net.sourceforge.plantuml.graphic.Splitter;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.openiconic.OpenIcon;
import net.sourceforge.plantuml.sprite.Sprite;

public class AtomTextUtils {

	protected static DelayedDouble ZERO = new DelayedDouble() {
		public double getDouble(StringBounder stringBounder) {
			return 0;
		}
	};

	public static Atom createLegacy(String text, FontConfiguration fontConfiguration) {
		return new AtomText(text, fontConfiguration, null, ZERO, ZERO, true);
	}

	public static Atom create(String text, FontConfiguration fontConfiguration) {
		return new AtomText(text, fontConfiguration, null, ZERO, ZERO, false);
	}

	public static Atom createUrl(Url url, FontConfiguration fontConfiguration, ISkinSimple skinSimple) {
		fontConfiguration = fontConfiguration.hyperlink();
		final Display display = Display.getWithNewlines(url.getLabel());
		if (display.size() > 1) {
			final List<Atom> all = new ArrayList<>();
			for (CharSequence s : display.asList()) {
				all.add(createAtomText(s.toString(), url, fontConfiguration, skinSimple));
			}
			return new AtomVerticalTexts(all);

		}
		return createAtomText(url.getLabel(), url, fontConfiguration, skinSimple);
	}

	private static final Pattern p = Pattern.compile(Splitter.openiconPattern + "|" + Splitter.spritePattern2 + "|"
			+ Splitter.imgPatternNoSrcColon + "|" + Splitter.emojiPattern);

	private static Atom createAtomText(final String text, Url url, FontConfiguration fontConfiguration,
			ISkinSimple skinSimple) {
		final Matcher m = p.matcher(text);
		final List<Atom> result = new ArrayList<>();
		while (m.find()) {
			final StringBuffer sb = new StringBuffer();
			m.appendReplacement(sb, "");
			if (sb.length() > 0) {
				result.add(new AtomText(sb.toString(), fontConfiguration, url, ZERO, ZERO, true));
			}
			final String valOpenicon = m.group(1);
			final String valSprite = m.group(3);
			final String valImg = m.group(5);
			final String valEmoji = m.group(7);
			if (valEmoji != null)
				throw new UnsupportedOperationException();

			if (valOpenicon != null) {
				final OpenIcon openIcon = OpenIcon.retrieve(valOpenicon);
				if (openIcon != null) {
					final double scale = Parser.getScale(m.group(2), 1);
					result.add(new AtomOpenIcon(null, scale, openIcon, fontConfiguration, url));
				}
			} else if (valSprite != null) {
				final Sprite sprite = skinSimple.getSprite(valSprite);
				if (sprite != null) {
					final double scale = Parser.getScale(m.group(4), 1);
					result.add(
							new AtomSprite(null, scale, fontConfiguration, sprite, url, skinSimple.getColorMapper()));
				}
			} else if (valImg != null) {
				final double scale = Parser.getScale(m.group(6), 1);
				result.add(AtomImg.create(valImg, ImgValign.TOP, 0, scale, url));

			}
		}
		final StringBuffer sb = new StringBuffer();
		m.appendTail(sb);
		if (sb.length() > 0) {
			result.add(new AtomText(sb.toString(), fontConfiguration, url, ZERO, ZERO, true));
		}
		if (result.size() == 1) {
			return result.get(0);
		}
		return new AtomHorizontalTexts(result);
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
		return new AtomText("" + (localNumber + 1) + ".", fontConfiguration, null, left, right, true);
	}

}
