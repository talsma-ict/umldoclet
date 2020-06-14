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
package net.sourceforge.plantuml.command.note;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlBuilder.ModeUrl;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.graphic.color.ColorParser;

public final class FactoryTipOnEntityCommand implements SingleMultiFactoryCommand<AbstractEntityDiagram> {

	private final IRegex partialPattern;
	private final String key;

	public FactoryTipOnEntityCommand(String key, IRegex partialPattern) {
		this.partialPattern = partialPattern;
		this.key = key;
	}

	private RegexConcat getRegexConcatMultiLine(IRegex partialPattern, final boolean withBracket) {
		if (withBracket) {
			return RegexConcat.build(FactoryTipOnEntityCommand.class.getName() + key + withBracket, RegexLeaf.start(), //
					new RegexLeaf("note"), //
					RegexLeaf.spaceOneOrMore(), //
					new RegexLeaf("POSITION", "(right|left)"), //
					RegexLeaf.spaceOneOrMore(), //
					new RegexLeaf("of"), //
					RegexLeaf.spaceOneOrMore(), //
					partialPattern, //
					RegexLeaf.spaceZeroOrMore(), //
					ColorParser.exp1(), //
					RegexLeaf.spaceZeroOrMore(), //
					new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
					RegexLeaf.spaceZeroOrMore(), //
					new RegexLeaf("\\{"), //
					RegexLeaf.end() //
					);
		}
		return RegexConcat.build(FactoryTipOnEntityCommand.class.getName() + key + withBracket, RegexLeaf.start(), //
				new RegexLeaf("note"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("POSITION", "(right|left)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("of"), //
				RegexLeaf.spaceOneOrMore(), //
				partialPattern, //
				RegexLeaf.spaceZeroOrMore(), //
				ColorParser.exp1(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
				RegexLeaf.end() //
				);
	}

	public Command<AbstractEntityDiagram> createSingleLine() {
		throw new UnsupportedOperationException();
	}

	public Command<AbstractEntityDiagram> createMultiLine(final boolean withBracket) {
		return new CommandMultilines2<AbstractEntityDiagram>(getRegexConcatMultiLine(partialPattern, withBracket),
				MultilinesStrategy.KEEP_STARTING_QUOTE) {

			@Override
			public String getPatternEnd() {
				if (withBracket) {
					return "(?i)^(\\})$";
				}
				return "(?i)^[%s]*(end[%s]?note)$";
			}

			protected CommandExecutionResult executeNow(final AbstractEntityDiagram system, BlocLines lines) {
				// StringUtils.trim(lines, false);
				final RegexResult line0 = getStartingPattern().matcher(lines.getFirst499().getTrimmed().getString());
				lines = lines.subExtract(1, 1);
				lines = lines.removeEmptyColumns();

				Url url = null;
				if (line0.get("URL", 0) != null) {
					final UrlBuilder urlBuilder = new UrlBuilder(system.getSkinParam().getValue("topurl"),
							ModeUrl.STRICT);
					url = urlBuilder.getUrl(line0.get("URL", 0));
				}

				return executeInternal(line0, system, url, lines);
			}
		};
	}

	private CommandExecutionResult executeInternal(RegexResult line0, AbstractEntityDiagram diagram, Url url,
			BlocLines lines) {

		final String pos = line0.get("POSITION", 0);

		final Code code = Code.of(line0.get("ENTITY", 0));
		final String member = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(line0.get("ENTITY", 1));
		if (code == null) {
			return CommandExecutionResult.error("Nothing to note to");
		}
		final IEntity cl1 = diagram.getOrCreateLeaf(code, null, null);
		final Position position = Position.valueOf(StringUtils.goUpperCase(pos)).withRankdir(
				diagram.getSkinParam().getRankdir());

		final Code codeTip = code.addSuffix("$$$" + position.name());
		IEntity tips = diagram.getLeafsget(codeTip);
		if (tips == null) {
			tips = diagram.getOrCreateLeaf(codeTip, LeafType.TIPS, null);
			final LinkType type = new LinkType(LinkDecor.NONE, LinkDecor.NONE).getInvisible();
			final Link link;
			if (position == Position.RIGHT) {
				link = new Link(cl1, (IEntity) tips, type, Display.NULL, 1, diagram.getSkinParam()
						.getCurrentStyleBuilder());
			} else {
				link = new Link((IEntity) tips, cl1, type, Display.NULL, 1, diagram.getSkinParam()
						.getCurrentStyleBuilder());
			}
			diagram.addLink(link);
		}
		tips.putTip(member, lines.toDisplay());
		return CommandExecutionResult.ok();
	}

}
