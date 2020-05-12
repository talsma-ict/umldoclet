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
package net.sourceforge.plantuml.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.WithSprite;
import net.sourceforge.plantuml.command.note.SingleMultiFactoryCommand;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.sprite.Sprite;
import net.sourceforge.plantuml.sprite.SpriteColorBuilder4096;
import net.sourceforge.plantuml.sprite.SpriteGrayLevel;

public final class CommandFactorySprite implements SingleMultiFactoryCommand<WithSprite> {

	private IRegex getRegexConcatMultiLine() {
		return RegexConcat.build(CommandFactorySprite.class.getName() + "multi", RegexLeaf.start(), //
				new RegexLeaf("sprite"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("\\$?"), //
				new RegexLeaf("NAME", "([-.\\p{L}0-9_]+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional(new RegexLeaf("DIM", "\\[(\\d+)x(\\d+)/(?:(\\d+)(z)?|(color))\\]")), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("\\{"), RegexLeaf.end());
	}

	private IRegex getRegexConcatSingleLine() {
		return RegexConcat.build(CommandFactorySprite.class.getName() + "single", RegexLeaf.start(), //
				new RegexLeaf("sprite"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("\\$?"), //
				new RegexLeaf("NAME", "([-.\\p{L}0-9_]+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional(new RegexLeaf("DIM", "\\[(\\d+)x(\\d+)/(?:(\\d+)(z)|(color))\\]")), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("DATA", "([-_A-Za-z0-9]+)"), RegexLeaf.end());
	}

	public Command<WithSprite> createSingleLine() {
		return new SingleLineCommand2<WithSprite>(getRegexConcatSingleLine()) {

			@Override
			protected CommandExecutionResult executeArg(final WithSprite system, LineLocation location,
					RegexResult arg) {
				return executeInternal(system, arg, Arrays.asList((String) arg.get("DATA", 0)));
			}

		};
	}

	public Command<WithSprite> createMultiLine(boolean withBracket) {
		return new CommandMultilines2<WithSprite>(getRegexConcatMultiLine(), MultilinesStrategy.REMOVE_STARTING_QUOTE) {

			@Override
			public String getPatternEnd() {
				return "(?i)^end[%s]?sprite|\\}$";
			}

			protected CommandExecutionResult executeNow(final WithSprite system, BlocLines lines) {
				lines = lines.trim().removeEmptyLines();
				final RegexResult line0 = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());

				lines = lines.subExtract(1, 1);
				lines = lines.removeEmptyColumns();
				if (lines.size() == 0) {
					return CommandExecutionResult.error("No sprite defined.");
				}
				return executeInternal(system, line0, lines.getLinesAsStringForSprite());
			}

		};
	}

	private CommandExecutionResult executeInternal(WithSprite system, RegexResult line0, final List<String> strings) {
		final Sprite sprite;
		if (line0.get("DIM", 0) == null) {
			sprite = SpriteGrayLevel.GRAY_16.buildSprite(-1, -1, strings);
		} else {
			final int width = Integer.parseInt(line0.get("DIM", 0));
			final int height = Integer.parseInt(line0.get("DIM", 1));
			if (line0.get("DIM", 4) == null) {
				final int nbLevel = Integer.parseInt(line0.get("DIM", 2));
				if (nbLevel != 4 && nbLevel != 8 && nbLevel != 16) {
					return CommandExecutionResult.error("Only 4, 8 or 16 graylevel are allowed.");
				}
				final SpriteGrayLevel level = SpriteGrayLevel.get(nbLevel);
				if (line0.get("DIM", 3) == null) {
					sprite = level.buildSprite(width, height, strings);
				} else {
					sprite = level.buildSpriteZ(width, height, concat(strings));
					if (sprite == null) {
						return CommandExecutionResult.error("Cannot decode sprite.");
					}
				}
			} else {
				sprite = SpriteColorBuilder4096.buildSprite(strings);
			}
		}
		system.addSprite(line0.get("NAME", 0), sprite);
		return CommandExecutionResult.ok();
	}

	private String concat(final List<String> strings) {
		final StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			sb.append(StringUtils.trin(s));
		}
		return sb.toString();
	}

}
