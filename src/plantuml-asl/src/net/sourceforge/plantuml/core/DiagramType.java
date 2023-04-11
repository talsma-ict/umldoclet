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
package net.sourceforge.plantuml.core;

import net.sourceforge.plantuml.utils.StartUtils;

public enum DiagramType {
    // ::remove folder when __HAXE__
	UML, BPM, DITAA, DOT, PROJECT, JCCKIT, SALT, FLOW, CREOLE, JUNGLE, CUTE, MATH, LATEX, DEFINITION, GANTT, NW,
	MINDMAP, WBS, WIRE, JSON, GIT, BOARD, YAML, HCL, EBNF, REGEX, UNKNOWN;

	static public DiagramType getTypeFromArobaseStart(String s) {
		s = s.toLowerCase();
		// if (s.startsWith("@startuml2")) {
		// return UML2;
		// }
		if (StartUtils.startsWithSymbolAnd("startwire", s))
			return WIRE;

		if (StartUtils.startsWithSymbolAnd("startbpm", s))
			return BPM;

		if (StartUtils.startsWithSymbolAnd("startuml", s))
			return UML;

		if (StartUtils.startsWithSymbolAnd("startdot", s))
			return DOT;

		if (StartUtils.startsWithSymbolAnd("startjcckit", s))
			return JCCKIT;

		if (StartUtils.startsWithSymbolAnd("startditaa", s))
			return DITAA;

		if (StartUtils.startsWithSymbolAnd("startproject", s))
			return PROJECT;

		if (StartUtils.startsWithSymbolAnd("startsalt", s))
			return SALT;

		if (StartUtils.startsWithSymbolAnd("startflow", s))
			return FLOW;

		if (StartUtils.startsWithSymbolAnd("startcreole", s))
			return CREOLE;

		if (StartUtils.startsWithSymbolAnd("starttree", s))
			return JUNGLE;

		if (StartUtils.startsWithSymbolAnd("startcute", s))
			return CUTE;

		if (StartUtils.startsWithSymbolAnd("startmath", s))
			return MATH;

		if (StartUtils.startsWithSymbolAnd("startlatex", s))
			return LATEX;

		if (StartUtils.startsWithSymbolAnd("startdef", s))
			return DEFINITION;

		if (StartUtils.startsWithSymbolAnd("startgantt", s))
			return GANTT;

		if (StartUtils.startsWithSymbolAnd("startnwdiag", s))
			return NW;

		if (StartUtils.startsWithSymbolAnd("startmindmap", s))
			return MINDMAP;

		if (StartUtils.startsWithSymbolAnd("startwbs", s))
			return WBS;

		if (StartUtils.startsWithSymbolAnd("startjson", s))
			return JSON;

		if (StartUtils.startsWithSymbolAnd("startgit", s))
			return GIT;

		if (StartUtils.startsWithSymbolAnd("startboard", s))
			return BOARD;

		if (StartUtils.startsWithSymbolAnd("startyaml", s))
			return YAML;

		if (StartUtils.startsWithSymbolAnd("starthcl", s))
			return HCL;

		if (StartUtils.startsWithSymbolAnd("startebnf", s))
			return EBNF;

		if (StartUtils.startsWithSymbolAnd("startregex", s))
			return REGEX;

		return UNKNOWN;
	}
}
