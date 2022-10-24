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
package net.sourceforge.plantuml.graphic;

import java.util.EnumSet;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlMode;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;

class HtmlCommandFactory {

	static final Pattern2 addStyle;
	static final Pattern2 removeStyle;

	static {
		final StringBuilder sbAddStyle = new StringBuilder();
		final StringBuilder sbRemoveStyle = new StringBuilder();

		for (FontStyle style : EnumSet.allOf(FontStyle.class)) {
			if (sbAddStyle.length() > 0) {
				sbAddStyle.append('|');
				sbRemoveStyle.append('|');
			}
			sbAddStyle.append(style.getActivationPattern());
			sbRemoveStyle.append(style.getDeactivationPattern());
		}

		addStyle = MyPattern.cmpile(sbAddStyle.toString());
		removeStyle = MyPattern.cmpile(sbRemoveStyle.toString());
	}

	private Pattern2 htmlTag = MyPattern.cmpile(Splitter.htmlTag);

	HtmlCommand getHtmlCommand(String s) {
		if (htmlTag.matcher(s).matches() == false)
			return new Text(s);

		if (MyPattern.mtches(s, Splitter.imgPattern))
			return Img.getInstance(s, true);

		if (MyPattern.mtches(s, Splitter.imgPatternNoSrcColon))
			return Img.getInstance(s, false);

		if (addStyle.matcher(s).matches())
			return AddStyle.fromString(s);

		if (removeStyle.matcher(s).matches())
			return new RemoveStyle(FontStyle.getStyle(s));

		if (MyPattern.mtches(s, Splitter.fontPattern))
			return new ColorAndSizeChange(s);

		if (MyPattern.mtches(s, Splitter.fontColorPattern2))
			return new ColorChange(s);

		if (MyPattern.mtches(s, Splitter.fontSizePattern2))
			return new SizeChange(s);

		if (MyPattern.mtches(s, Splitter.fontSup))
			return new ExposantChange(FontPosition.EXPOSANT);

		if (MyPattern.mtches(s, Splitter.fontSub))
			return new ExposantChange(FontPosition.INDICE);

		if (MyPattern.mtches(s, Splitter.endFontPattern))
			return new ResetFont();

		if (MyPattern.mtches(s, Splitter.endSupSub))
			return new ExposantChange(FontPosition.NORMAL);

		if (MyPattern.mtches(s, Splitter.fontFamilyPattern))
			return new FontFamilyChange(s);

		if (MyPattern.mtches(s, Splitter.spritePatternForMatch))
			return new SpriteCommand(s);

		if (MyPattern.mtches(s, Splitter.linkPattern)) {
			final UrlBuilder urlBuilder = new UrlBuilder(null, UrlMode.STRICT);
			final Url url = urlBuilder.getUrl(s);
			url.setMember(true);
			return new TextLink(url);
		}

		if (MyPattern.mtches(s, Splitter.svgAttributePattern))
			return new SvgAttributesChange(s);

		return null;
	}

}
