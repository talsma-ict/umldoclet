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
package net.sourceforge.plantuml.objectdiagram;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.classdiagram.command.CommandLinkClass;
import net.sourceforge.plantuml.classdiagram.command.CommandUrl;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandEndPackage;
import net.sourceforge.plantuml.command.CommandFootboxIgnored;
import net.sourceforge.plantuml.command.CommandPackage;
import net.sourceforge.plantuml.command.CommandPage;
import net.sourceforge.plantuml.command.CommandRankDir;
import net.sourceforge.plantuml.command.UmlDiagramFactory;
import net.sourceforge.plantuml.command.note.FactoryNoteCommand;
import net.sourceforge.plantuml.command.note.FactoryNoteOnEntityCommand;
import net.sourceforge.plantuml.command.note.FactoryNoteOnLinkCommand;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.objectdiagram.command.CommandAddData;
import net.sourceforge.plantuml.objectdiagram.command.CommandCreateEntityObject;
import net.sourceforge.plantuml.objectdiagram.command.CommandCreateEntityObjectMultilines;

public class ObjectDiagramFactory extends UmlDiagramFactory {

	private final ISkinSimple skinParam;

	public ObjectDiagramFactory(ISkinSimple skinParam) {
		this.skinParam = skinParam;
	}

	@Override
	protected List<Command> createCommands() {

		final List<Command> cmds = new ArrayList<Command>();
		cmds.add(new CommandFootboxIgnored());

		addCommonCommands1(cmds);
		cmds.add(new CommandRankDir());
		cmds.add(new CommandPage());
		cmds.add(new CommandAddData());
		cmds.add(new CommandLinkClass(UmlDiagramType.OBJECT));
		//
		cmds.add(new CommandCreateEntityObject());
		final FactoryNoteCommand factoryNoteCommand = new FactoryNoteCommand();

		cmds.add(factoryNoteCommand.createSingleLine());
		cmds.add(new CommandPackage());
		cmds.add(new CommandEndPackage());
		// addCommand(new CommandNamespace());
		// addCommand(new CommandEndNamespace());
		// addCommand(new CommandStereotype());
		//
		// addCommand(new CommandImport());
		final FactoryNoteOnEntityCommand factoryNoteOnEntityCommand = new FactoryNoteOnEntityCommand("object",
				new RegexLeaf("ENTITY", "([\\p{L}0-9_.]+|[%g][^%g]+[%g])"));
		cmds.add(factoryNoteOnEntityCommand.createSingleLine());

		cmds.add(new CommandUrl());

		cmds.add(factoryNoteCommand.createMultiLine(false));
		cmds.add(factoryNoteOnEntityCommand.createMultiLine(true));
		cmds.add(factoryNoteOnEntityCommand.createMultiLine(false));
		cmds.add(new CommandCreateEntityObjectMultilines());

		final FactoryNoteOnLinkCommand factoryNoteOnLinkCommand = new FactoryNoteOnLinkCommand();
		cmds.add(factoryNoteOnLinkCommand.createSingleLine());
		cmds.add(factoryNoteOnLinkCommand.createMultiLine(false));

		// addCommand(new CommandNoopClass());
		return cmds;

	}

	@Override
	public ObjectDiagram createEmptyDiagram() {
		return new ObjectDiagram(skinParam);
	}
}
