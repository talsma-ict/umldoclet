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
package net.sourceforge.plantuml.project.lang;

import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;

public class PairOfSomething implements Something {

	private final Something complement1;
	private final Something complement2;

	public PairOfSomething(Something complement1, Something complement2) {
		this.complement1 = complement1;
		this.complement2 = complement2;
	}

	public Failable<? extends Object> getMe(GanttDiagram system, RegexResult arg, String suffix) {
		final Failable<? extends Object> r1 = complement1.getMe(system, arg, "A" + suffix);
		final Failable<? extends Object> r2 = complement2.getMe(system, arg, "B" + suffix);
		if (r1.isFail()) {
			return r1;
		}
		if (r2.isFail()) {
			return r2;
		}
		final Object[] result = new Object[] { r1.get(), r2.get() };
		return Failable.ok(result);
	}

	public IRegex toRegex(String suffix) {
		final IRegex pattern1 = complement1.toRegex("A" + suffix);
		final IRegex pattern2 = complement2.toRegex("B" + suffix);
		return new RegexConcat(pattern1, new RegexLeaf("[%s]+"), pattern2);
	}

}
