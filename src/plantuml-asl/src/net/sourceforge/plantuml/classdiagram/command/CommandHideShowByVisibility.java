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
package net.sourceforge.plantuml.classdiagram.command;

import java.util.EnumSet;
import java.util.Set;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.skin.VisibilityModifier;

public class CommandHideShowByVisibility extends SingleLineCommand2<UmlDiagram> {

	public CommandHideShowByVisibility() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandHideShowByVisibility.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("COMMAND", "(hide|show)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("VISIBILITY",
						"((?:public|private|protected|package)?(?:[,%s]+(?:public|private|protected|package))*)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("PORTION", "(members?|attributes?|fields?|methods?)"), RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(UmlDiagram classDiagram, LineLocation location, RegexResult arg) {
		if (classDiagram instanceof ClassDiagram) {
			return executeArgClass((ClassDiagram) classDiagram, arg);
		}
		// Just ignored
		return CommandExecutionResult.ok();
	}

	private CommandExecutionResult executeArgClass(ClassDiagram classDiagram, RegexResult arg) {

		final EntityPortion portion = getEntityPortion(arg.get("PORTION", 0));

		final Set<VisibilityModifier> visibilities = EnumSet.<VisibilityModifier> noneOf(VisibilityModifier.class);
		final StringTokenizer st = new StringTokenizer(StringUtils.goLowerCase(arg.get("VISIBILITY", 0)), " ,");
		while (st.hasMoreTokens()) {
			addVisibilities(st.nextToken(), portion, visibilities);
		}

		classDiagram.hideOrShow(visibilities, arg.get("COMMAND", 0).equalsIgnoreCase("show"));

		return CommandExecutionResult.ok();
	}

	private void addVisibilities(String token, EntityPortion portion, Set<VisibilityModifier> result) {
		if (token.equals("public") && (portion == EntityPortion.MEMBER || portion == EntityPortion.FIELD)) {
			result.add(VisibilityModifier.PUBLIC_FIELD);
		}
		if (token.equals("public") && (portion == EntityPortion.MEMBER || portion == EntityPortion.METHOD)) {
			result.add(VisibilityModifier.PUBLIC_METHOD);
		}
		if (token.equals("private") && (portion == EntityPortion.MEMBER || portion == EntityPortion.FIELD)) {
			result.add(VisibilityModifier.PRIVATE_FIELD);
		}
		if (token.equals("private") && (portion == EntityPortion.MEMBER || portion == EntityPortion.METHOD)) {
			result.add(VisibilityModifier.PRIVATE_METHOD);
		}
		if (token.equals("protected") && (portion == EntityPortion.MEMBER || portion == EntityPortion.FIELD)) {
			result.add(VisibilityModifier.PROTECTED_FIELD);
		}
		if (token.equals("protected") && (portion == EntityPortion.MEMBER || portion == EntityPortion.METHOD)) {
			result.add(VisibilityModifier.PROTECTED_METHOD);
		}
		if (token.equals("package") && (portion == EntityPortion.MEMBER || portion == EntityPortion.FIELD)) {
			result.add(VisibilityModifier.PACKAGE_PRIVATE_FIELD);
		}
		if (token.equals("package") && (portion == EntityPortion.MEMBER || portion == EntityPortion.METHOD)) {
			result.add(VisibilityModifier.PACKAGE_PRIVATE_METHOD);
		}
	}

	private EntityPortion getEntityPortion(String s) {
		final String sub = StringUtils.goLowerCase(s.substring(0, 3));
		if (sub.equals("met")) {
			return EntityPortion.METHOD;
		}
		if (sub.equals("mem")) {
			return EntityPortion.MEMBER;
		}
		if (sub.equals("att") || sub.equals("fie")) {
			return EntityPortion.FIELD;
		}
		throw new IllegalArgumentException();
	}

}
