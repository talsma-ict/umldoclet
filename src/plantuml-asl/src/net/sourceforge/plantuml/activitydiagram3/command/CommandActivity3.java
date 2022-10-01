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
package net.sourceforge.plantuml.activitydiagram3.command;

import java.util.regex.Matcher;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlMode;
import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandActivity3 extends SingleLineCommand2<ActivityDiagram3> {

	public static final String endingGroup() {
		return "(" //
				+ ";" //
				+ "|" //
				+ Matcher.quoteReplacement("\\\\") // that is simply \ character
				+ "|" //
				+ "(?<![/|<>}\\]])[/<}]" // About /<}
				+ "|" //
				+ "(?<![/|}\\]])\\]" // About ]
				+ "|" //
				+ "(?<!\\</?\\w{1,5})(?<!\\<img[^>]{1,999})(?<!\\<[&$]\\w{1,999})(?<!\\>)\\>" // About >
				+ "|" //
				+ "(?<!\\|.{1,999})\\|" // About |
				+ ")";
	}

	public static void main(String[] args) {
		System.err.println(Matcher.quoteReplacement("\\\\"));
		System.err.println(Matcher.quoteReplacement("\\\\").equals("\\\\\\\\"));
	}

	public CommandActivity3() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandActivity3.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<{2}.*\\>{2})?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(":"), //
				new RegexLeaf("LABEL", "(.*)"), //
				new RegexLeaf("STYLE", endingGroup()), //
				RegexLeaf.end());
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram3 diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {

		final Url url;
		if (arg.get("URL", 0) == null) {
			url = null;
		} else {
			final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), UrlMode.STRICT);
			url = urlBuilder.getUrl(arg.get("URL", 0));
		}

		Colors colors = color().getColor(arg, diagram.getSkinParam().getIHtmlColorSet());
		final String stereo = arg.get("STEREO", 0);
		Stereotype stereotype = null;
		if (stereo != null) {
			stereotype = Stereotype.build(stereo);
			colors = colors.applyStereotype(stereotype, diagram.getSkinParam(), ColorParam.activityBackground);
		}
		final BoxStyle style = BoxStyle.fromChar(arg.get("STYLE", 0).charAt(0));
		final Display display = Display.getWithNewlines2(arg.get("LABEL", 0));
		return diagram.addActivity(display, style, url, colors, stereotype);
	}

}
