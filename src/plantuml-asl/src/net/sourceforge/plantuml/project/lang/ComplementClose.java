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
package net.sourceforge.plantuml.project.lang;

import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;

public class ComplementClose implements Something {

	public IRegex toRegex(String suffix) {
		return new RegexLeaf("CLOSED" + suffix, "(closed?(?: for \\[([^\\[\\]]+?)\\])?)");
	}

	public Failable<String> getMe(GanttDiagram project, RegexResult arg, String suffix) {
		final String value = arg.get("CLOSED" + suffix, 0);
		final int x = value.indexOf('[');
		if (x > 0) {
			final int y = value.lastIndexOf(']');
			final String s = value.substring(x + 1, y);
			return Failable.ok(s);
		}
		return Failable.ok("");
	}
}
