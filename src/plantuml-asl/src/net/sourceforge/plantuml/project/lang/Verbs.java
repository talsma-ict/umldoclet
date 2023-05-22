/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexOptional;
import net.sourceforge.plantuml.regex.RegexOr;

public class Verbs {

	public static IRegex are = new RegexLeaf("are");

	public static IRegex areColored = new RegexLeaf("are[%s]+colou?red");

	public static IRegex displayOnSameRowAs = new RegexLeaf("displays?[%s]+on[%s]+same[%s]+row[%s]+as");

	public static IRegex ends = new RegexLeaf("ends");

	public static IRegex ends2 = new RegexLeaf("ends[%s]*(the[%s]*|on[%s]*|at[%s]*)*");

	public static IRegex happens = new RegexLeaf("happens?[%s]*(at[%s]*|the[%s]*|on[%s]*)*");

	public static IRegex pauses = new RegexLeaf("pauses?[%s]*(at[%s]*|the[%s]*|on[%s]*|from[%s]*)*");

	public static IRegex isDeleted = new RegexLeaf("is[%s]+deleted");

	public static IRegex is = new RegexLeaf("is");

	public static IRegex isColored = new RegexLeaf("is[%s]+colou?red");

	public static IRegex isColoredForCompletion = new RegexLeaf("is[%s]+colou?red[%s]+for[%s]+completion");

	public static IRegex isOff = new RegexConcat(new RegexLeaf("is"), //
			RegexLeaf.spaceOneOrMore(), //
			new RegexLeaf("off"), //
			RegexLeaf.spaceOneOrMore(), //
			new RegexOr(//
					new RegexLeaf("on"), //
					new RegexLeaf("for"), //
					new RegexLeaf("the"), //
					new RegexLeaf("at") //
			));

	public static IRegex isOn = new RegexConcat(new RegexLeaf("is"), //
			RegexLeaf.spaceOneOrMore(), //
			new RegexLeaf("on"), //
			RegexLeaf.spaceOneOrMore(), //
			new RegexOr(//
					new RegexLeaf("on"), //
					new RegexLeaf("for"), //
					new RegexLeaf("the"), //
					new RegexLeaf("at") //
			) //
	);

	public static IRegex isOrAre = new RegexLeaf("(is|are)");

	public static IRegex isOrAreNamed = new RegexLeaf("(is|are)[%s]+named");

	public static IRegex lasts = new RegexLeaf("(lasts|requires)");

	public static IRegex linksTo = new RegexLeaf("links to");

	public static IRegex occurs = new RegexLeaf("occurs?");

	public static IRegex starts3 = new RegexLeaf("starts[%s]*(the[%s]*|on[%s]*|at[%s]*)*");

	public static IRegex starts2 = new RegexLeaf("starts");

	public static IRegex starts = new RegexConcat(new RegexLeaf("start"), //
			new RegexOptional(new RegexLeaf("s")), //
			RegexLeaf.spaceZeroOrMore(), //
			new RegexOptional(new RegexOr(//
					new RegexLeaf("on"), //
					new RegexLeaf("for"), //
					new RegexLeaf("the"), //
					new RegexLeaf("at") //
			)) //
	);

	public static IRegex just = new RegexLeaf("just");

	public static IRegex justBefore = new RegexLeaf("just[%s]*before");

	public static IRegex justAfter = new RegexLeaf("just[%s]*after");

}
