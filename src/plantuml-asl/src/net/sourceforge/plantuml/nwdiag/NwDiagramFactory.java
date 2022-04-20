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
package net.sourceforge.plantuml.nwdiag;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandFootboxIgnored;
import net.sourceforge.plantuml.command.CommonCommands;
import net.sourceforge.plantuml.command.PSystemCommandFactory;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;

public class NwDiagramFactory extends PSystemCommandFactory {

	public NwDiagramFactory(DiagramType type) {
		super(type);
	}

	@Override
	public NwDiagram createEmptyDiagram(ThemeStyle style, UmlSource source, ISkinSimple skinParam) {
		return new NwDiagram(style, source);
	}

	@Override
	protected List<Command> createCommands() {

		final List<Command> cmds = new ArrayList<>();
		CommonCommands.addCommonCommands1(cmds);
		cmds.add(new CommandNwDiagInit());
		cmds.add(new CommandComment());
		cmds.add(new CommandElement());
		cmds.add(new CommandGroup());
		cmds.add(new CommandNetwork());
		cmds.add(new CommandLink());
		cmds.add(new CommandProperty());
		cmds.add(new CommandEndSomething());
		cmds.add(new CommandFootboxIgnored());
		return cmds;
	}

}
