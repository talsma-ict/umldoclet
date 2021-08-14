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

import net.sourceforge.plantuml.core.Diagram;

public class CommandMultilinesComment extends CommandMultilines<Diagram> {

	public static final String COMMENT_MULTILINE_START = "^[%s]*/[%q]([^%q]|[%q][^/])*$";
	public static final String COMMENT_MULTILINE_END = "^([^%q]|[%q][^/])*[%q]/[%s]*$";
	public static final String COMMENT_SINGLE_LINE = "^[%s]*([%q].*||/[%q].*[%q]/[%s]*)$";
	public static final String INNER_COMMENT = "/[%q].*?[%q]/";

	private CommandMultilinesComment() {
		super(COMMENT_MULTILINE_START);
	}

	@Override
	public String getPatternEnd() {
		return COMMENT_MULTILINE_END;
	}

	public CommandExecutionResult execute(final Diagram diagram, BlocLines lines) {
		return CommandExecutionResult.ok();
	}

}
