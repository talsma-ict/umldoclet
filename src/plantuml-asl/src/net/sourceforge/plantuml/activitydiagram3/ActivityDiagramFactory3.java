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
package net.sourceforge.plantuml.activitydiagram3;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.activitydiagram3.command.CommandActivity3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandActivityLegacy1;
import net.sourceforge.plantuml.activitydiagram3.command.CommandActivityLong3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandArrow3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandArrowLong3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandBackward3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandBreak;
import net.sourceforge.plantuml.activitydiagram3.command.CommandCase;
import net.sourceforge.plantuml.activitydiagram3.command.CommandCircleSpot3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandElse3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandElseIf2;
import net.sourceforge.plantuml.activitydiagram3.command.CommandElseLegacy1;
import net.sourceforge.plantuml.activitydiagram3.command.CommandEnd3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandEndPartition3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandEndSwitch;
import net.sourceforge.plantuml.activitydiagram3.command.CommandEndif3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandFork3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandForkAgain3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandForkEnd3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandGoto;
import net.sourceforge.plantuml.activitydiagram3.command.CommandGroup3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandGroupEnd3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandIf2;
import net.sourceforge.plantuml.activitydiagram3.command.CommandIf4;
import net.sourceforge.plantuml.activitydiagram3.command.CommandIfLegacy1;
import net.sourceforge.plantuml.activitydiagram3.command.CommandKill3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandLabel;
import net.sourceforge.plantuml.activitydiagram3.command.CommandLink3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandNote3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandNoteLong3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandPartition3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandRepeat3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandRepeatWhile3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandRepeatWhile3Multilines;
import net.sourceforge.plantuml.activitydiagram3.command.CommandSplit3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandSplitAgain3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandSplitEnd3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandStart3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandStop3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandSwimlane;
import net.sourceforge.plantuml.activitydiagram3.command.CommandSwimlane2;
import net.sourceforge.plantuml.activitydiagram3.command.CommandSwitch;
import net.sourceforge.plantuml.activitydiagram3.command.CommandWhile3;
import net.sourceforge.plantuml.activitydiagram3.command.CommandWhileEnd3;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandDecoratorMultine;
import net.sourceforge.plantuml.command.CommandFootboxIgnored;
import net.sourceforge.plantuml.command.UmlDiagramFactory;

public class ActivityDiagramFactory3 extends UmlDiagramFactory {

	private final ISkinSimple skinParam;

	public ActivityDiagramFactory3(ISkinSimple skinParam) {
		this.skinParam = skinParam;
	}

	@Override
	protected List<Command> createCommands() {

		final List<Command> cmds = new ArrayList<Command>();
		cmds.add(new CommandFootboxIgnored());

		addCommonCommands1(cmds);
		cmds.add(new CommandSwimlane());
		cmds.add(new CommandSwimlane2());
		cmds.add(new CommandPartition3());
		cmds.add(new CommandEndPartition3());
		cmds.add(new CommandGroup3());
		cmds.add(new CommandGroupEnd3());
		cmds.add(new CommandArrow3());
		cmds.add(new CommandArrowLong3());
		cmds.add(new CommandActivity3());
		cmds.add(new CommandIf4());
		cmds.add(new CommandIf2());
		cmds.add(new CommandDecoratorMultine(new CommandIf2(), 50));
		cmds.add(new CommandIfLegacy1());
		cmds.add(new CommandElseIf2());
		cmds.add(new CommandElse3());
		cmds.add(new CommandDecoratorMultine(new CommandElse3(), 50));
		cmds.add(new CommandElseLegacy1());
		cmds.add(new CommandEndif3());

		cmds.add(new CommandSwitch());
		cmds.add(new CommandCase());
		cmds.add(new CommandEndSwitch());

		cmds.add(new CommandRepeat3());
		cmds.add(new CommandRepeatWhile3());
		cmds.add(new CommandRepeatWhile3Multilines());
		cmds.add(new CommandBackward3());
		cmds.add(new CommandWhile3());
		cmds.add(new CommandWhileEnd3());
		
		cmds.add(new CommandFork3());
		cmds.add(new CommandForkAgain3());
		cmds.add(new CommandForkEnd3());
		
		cmds.add(new CommandSplit3());
		cmds.add(new CommandSplitAgain3());
		cmds.add(new CommandSplitEnd3());
		// cmds.add(new CommandGroup3());
		// cmds.add(new CommandGroupEnd3());
		cmds.add(new CommandStart3());
		cmds.add(new CommandStop3());
		cmds.add(new CommandCircleSpot3());
		cmds.add(new CommandBreak());
		cmds.add(new CommandEnd3());
		cmds.add(new CommandKill3());
		cmds.add(new CommandLink3());
		cmds.add(new CommandNote3());
		cmds.add(new CommandNoteLong3());

		cmds.add(new CommandActivityLong3());
		cmds.add(new CommandActivityLegacy1());

		cmds.add(new CommandLabel());
		cmds.add(new CommandGoto());
		cmds.add(new CommandDecoratorMultine(new CommandElseIf2(), 50));

		return cmds;
	}

	@Override
	public ActivityDiagram3 createEmptyDiagram() {
		return new ActivityDiagram3(skinParam);
	}

}
