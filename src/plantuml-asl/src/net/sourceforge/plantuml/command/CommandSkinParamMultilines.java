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
package net.sourceforge.plantuml.command;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;

public class CommandSkinParamMultilines extends CommandMultilinesBracket<TitledDiagram> {

	public CommandSkinParamMultilines() {
		super("^skinparam[%s]*(?:[%s]+([\\w.]*(?:\\<\\<.*\\>\\>)?[\\w.]*))?[%s]*\\{$");
	}

	@Override
	protected boolean isLineConsistent(String line, int level) {
		line = StringUtils.trin(line);
		if (hasStartingQuote(line)) {
			return true;
		}
		return SkinLoader.p1.matcher(line).matches();
	}

	private boolean hasStartingQuote(CharSequence line) {
		// return MyPattern.mtches(line, "[%s]*[%q].*");
		return MyPattern.mtches(line, CommandMultilinesComment.COMMENT_SINGLE_LINE);
	}

	public CommandExecutionResult execute(TitledDiagram diagram, BlocLines lines) {
		final SkinLoader skinLoader = new SkinLoader(diagram);

		final Matcher2 mStart = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
		if (mStart.find() == false) {
			throw new IllegalStateException();
		}
		final String group1 = mStart.group(1);

		return skinLoader.execute(lines, group1);

	}

}
