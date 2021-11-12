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
package net.sourceforge.plantuml.cucadiagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.Guillemet;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.command.regex.RegexComposed;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.sprite.SpriteUtils;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class StereotypeDecoration {
	private final static RegexComposed circleChar = new RegexConcat( //
			new RegexLeaf("\\<\\<"), //
			RegexLeaf.spaceZeroOrMore(), //
			new RegexLeaf("\\(?"), //
			new RegexLeaf("CHAR", "(\\S)"), //
			RegexLeaf.spaceZeroOrMore(), //
			new RegexLeaf(","), //
			RegexLeaf.spaceZeroOrMore(), //
			new RegexLeaf("COLOR", "(#[0-9a-fA-F]{6}|\\w+)"), //
			RegexLeaf.spaceZeroOrMore(), //
			new RegexOptional(new RegexLeaf("LABEL", "[),](.*?)")), //
			new RegexLeaf("\\>\\>") //
	);

	private final static RegexComposed circleSprite = new RegexConcat( //
			new RegexLeaf("\\<\\<"), //
			RegexLeaf.spaceZeroOrMore(), //
			new RegexLeaf("\\(?\\$"), //
			new RegexLeaf("NAME", "(" + SpriteUtils.SPRITE_NAME + ")"), //
			new RegexLeaf("SCALE", "((?:\\{scale=|\\*)([0-9.]+)\\}?)?"), //
			RegexLeaf.spaceZeroOrMore(), //
			new RegexOptional( //
					new RegexConcat( //
							new RegexLeaf(","), //
							RegexLeaf.spaceZeroOrMore(), //
							new RegexLeaf("COLOR", "(#[0-9a-fA-F]{6}|\\w+)") //
					)), //
			RegexLeaf.spaceZeroOrMore(), //
			new RegexOptional(new RegexLeaf("LABEL", "[),](.*?)")), //
			new RegexLeaf("\\>\\>") //
	);

	final String label;
	final HColor htmlColor;
	final char character;
	final String spriteName;
	final double spriteScale;

	private StereotypeDecoration(String label, HColor htmlColor, char character, String spriteName,
			double spriteScale) {
		this.label = label;
		this.htmlColor = htmlColor;
		this.character = character;
		this.spriteName = spriteName;
		this.spriteScale = spriteScale;
	}

	static StereotypeDecoration buildSimple(String name) {
		final String spriteName;
		final double spriteScale;
		if (name.startsWith("<<$") && name.endsWith(">>")) {
			final RegexResult mCircleSprite = StereotypeDecoration.circleSprite.matcher(name);
			spriteName = mCircleSprite.get("NAME", 0);
			spriteScale = Parser.getScale(mCircleSprite.get("SCALE", 0), 1);
		} else {
			spriteName = null;
			spriteScale = 0;
		}
		return new StereotypeDecoration(name, null, '\0', spriteName, spriteScale);
	}

	public static StereotypeDecoration buildComplex(String full, HColorSet htmlColorSet) throws NoSuchColorException {

		String label = "";
		HColor htmlColor = null;
		char character = '\0';
		String spriteName = null;
		double spriteScale = 0;

		final List<String> list = cutLabels(full, Guillemet.DOUBLE_COMPARATOR);
		for (String name : list) {
			final RegexResult mCircleChar = circleChar.matcher(name);
			final RegexResult mCircleSprite = circleSprite.matcher(name);

			if (mCircleSprite != null) {
				if (StringUtils.isNotEmpty(mCircleSprite.get("LABEL", 0)))
					name = "<<" + mCircleSprite.get("LABEL", 0) + ">>";
				else
					name = "";

				final String colName = mCircleSprite.get("COLOR", 0);
				final HColor col = colName == null ? null : htmlColorSet.getColorLEGACY(colName);
				htmlColor = col == null ? HColorUtils.BLACK : col;
				character = '\0';
				spriteName = mCircleSprite.get("NAME", 0);
				spriteScale = Parser.getScale(mCircleSprite.get("SCALE", 0), 1);
			} else if (mCircleChar != null) {
				if (StringUtils.isNotEmpty(mCircleChar.get("LABEL", 0)))
					name = "<<" + mCircleChar.get("LABEL", 0) + ">>";
				else
					name = "";

				final String colName = mCircleChar.get("COLOR", 0);
				htmlColor = colName == null ? null : htmlColorSet.getColorLEGACY(colName);
				character = mCircleChar.get("CHAR", 0).charAt(0);
			}

			label = label + name;
		}

		return new StereotypeDecoration(label, htmlColor, character, spriteName, spriteScale);
	}

	static List<String> cutLabels(final String label, Guillemet guillemet) {
		final List<String> result = new ArrayList<>();
		final Pattern2 p = MyPattern.cmpile("\\<{2,3}.*?\\>{2,3}");
		final Matcher2 m = p.matcher(label);
		while (m.find()) {
			final String group = m.group();
			if (group.startsWith("<<<") == false)
				result.add(guillemet.manageGuillemetStrict(group));
		}
		return Collections.unmodifiableList(result);
	}

}
