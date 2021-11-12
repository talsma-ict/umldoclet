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
package net.sourceforge.plantuml.activitydiagram3.command;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandPartition3 extends SingleLineCommand2<ActivityDiagram3> {

	public CommandPartition3() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandPartition3.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "(partition|package|rectangle|card)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexOptional(//
						new RegexConcat( //
								color("BACK1").getRegex(), //
								RegexLeaf.spaceOneOrMore())), //
				new RegexLeaf("NAME", "([%g][^%g]+[%g]|\\S+)"), //
				new RegexOptional(//
						new RegexConcat( //
								RegexLeaf.spaceOneOrMore(), //
								color("BACK2").getRegex())), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<{2}.*\\>{2})?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("\\{?"), //
				RegexLeaf.end());
	}

	private USymbol getUSymbol(String type) {
		if ("card".equalsIgnoreCase(type)) {
			return USymbol.CARD;
		}
		if ("package".equalsIgnoreCase(type)) {
			return USymbol.PACKAGE;
		}
		if ("rectangle".equalsIgnoreCase(type)) {
			return USymbol.RECTANGLE;
		}
		return USymbol.FRAME;
	}

	private ColorParam getColorParamBorder(final USymbol symbol) {
		if (symbol == USymbol.FRAME) {
			return ColorParam.partitionBorder;
		}
		return symbol.getColorParamBorder();
	}

	private ColorParam getColorParamBack(final USymbol symbol) {
		if (symbol == USymbol.FRAME) {
			return ColorParam.partitionBackground;
		}
		return symbol.getColorParamBack();
	}

	private static ColorParser color(String id) {
		return ColorParser.simpleColor(ColorType.BACK, id);
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram3 diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		final String partitionTitle = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(arg.get("NAME", 0));

		final String b1 = arg.get("BACK1", 0);
		final Colors colors = color(b1 == null ? "BACK2" : "BACK1").getColor(diagram.getSkinParam().getThemeStyle(),
				arg, diagram.getSkinParam().getIHtmlColorSet());

		final USymbol symbol = getUSymbol(arg.get("TYPE", 0));
		final String stereo = arg.get("STEREO", 0);
		final Stereotype stereotype = stereo == null ? null : Stereotype.build(stereo);

		final HColor backColorInSkinparam = diagram.getSkinParam().getHtmlColor(getColorParamBack(symbol), stereotype,
				false);
		HColor backColor;
		if (backColorInSkinparam == null) {
			backColor = colors.getColor(ColorType.BACK);
		} else {
			backColor = backColorInSkinparam;
		}
		HColor titleColor = colors.getColor(ColorType.HEADER);

		// Warning : titleColor unused in FTileGroupW
		HColor borderColor = diagram.getSkinParam().getHtmlColor(getColorParamBorder(symbol), stereotype, false);
		if (borderColor == null) {
			borderColor = HColorUtils.BLACK;
		}
		double roundCorner = symbol.getSkinParameter().getRoundCorner(diagram.getSkinParam(), stereotype);

		if (UseStyle.useBetaStyle()) {
			final Style stylePartition = getDefaultStyleDefinitionPartition()
					.getMergedStyle(diagram.getSkinParam().getCurrentStyleBuilder());
			borderColor = stylePartition.value(PName.LineColor).asColor(diagram.getSkinParam().getThemeStyle(),
					diagram.getSkinParam().getIHtmlColorSet());
			backColor = colors.getColor(ColorType.BACK);
			if (backColor == null) {
				backColor = stylePartition.value(PName.BackGroundColor).asColor(diagram.getSkinParam().getThemeStyle(),
						diagram.getSkinParam().getIHtmlColorSet());
			}
			titleColor = HColorUtils.BLUE;// stylePartition.value(PName.FontColor).asColor(diagram.getSkinParam().getIHtmlColorSet());
			roundCorner = stylePartition.value(PName.RoundCorner).asDouble();
		}

		diagram.startGroup(Display.getWithNewlines(partitionTitle), backColor, titleColor, borderColor, symbol,
				roundCorner);

		return CommandExecutionResult.ok();
	}

	final public StyleSignature getDefaultStyleDefinitionPartition() {
		return StyleSignature.of(SName.root, SName.element, SName.activityDiagram, SName.partition);
	}

}
