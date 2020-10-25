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
package net.sourceforge.plantuml.descdiagram;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.classdiagram.command.CommandHideShow2;
import net.sourceforge.plantuml.classdiagram.command.CommandNamespaceSeparator;
import net.sourceforge.plantuml.classdiagram.command.CommandRemoveRestore;
import net.sourceforge.plantuml.classdiagram.command.CommandUrl;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandEndPackage;
import net.sourceforge.plantuml.command.CommandFootboxIgnored;
import net.sourceforge.plantuml.command.CommandPage;
import net.sourceforge.plantuml.command.CommandRankDir;
import net.sourceforge.plantuml.command.PSystemCommandFactory;
import net.sourceforge.plantuml.command.note.CommandFactoryNote;
import net.sourceforge.plantuml.command.note.CommandFactoryNoteOnEntity;
import net.sourceforge.plantuml.command.note.CommandFactoryNoteOnLink;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.descdiagram.command.CommandArchimate;
import net.sourceforge.plantuml.descdiagram.command.CommandArchimateMultilines;
import net.sourceforge.plantuml.descdiagram.command.CommandCreateElementFull;
import net.sourceforge.plantuml.descdiagram.command.CommandCreateElementMultilines;
import net.sourceforge.plantuml.descdiagram.command.CommandLinkElement;
import net.sourceforge.plantuml.descdiagram.command.CommandNewpage;
import net.sourceforge.plantuml.descdiagram.command.CommandPackageWithUSymbol;

public class DescriptionDiagramFactory extends PSystemCommandFactory {

	private final ISkinSimple skinParam;

	public DescriptionDiagramFactory(ISkinSimple skinParam) {
		this.skinParam = skinParam;
	}

	@Override
	public DescriptionDiagram createEmptyDiagram() {
		return new DescriptionDiagram(skinParam);
	}

	@Override
	protected List<Command> createCommands() {
		final List<Command> cmds = new ArrayList<Command>();

		cmds.add(new CommandFootboxIgnored());
		cmds.add(new CommandNamespaceSeparator());
		cmds.add(new CommandRankDir());
		cmds.add(new CommandNewpage(this));
		addCommonCommands1(cmds);

		cmds.add(new CommandPage());
		cmds.add(new CommandLinkElement());
		cmds.add(new CommandHideShow2());
		cmds.add(new CommandRemoveRestore());
		//
		cmds.add(new CommandPackageWithUSymbol());
		cmds.add(new CommandEndPackage());
		final CommandFactoryNote factoryNoteCommand = new CommandFactoryNote();
		cmds.add(factoryNoteCommand.createMultiLine(false));

		final CommandFactoryNoteOnEntity factoryNoteOnEntityCommand = new CommandFactoryNoteOnEntity("desc",
				new RegexOr("ENTITY", //
						new RegexLeaf("[\\p{L}0-9_.]+"), //
						new RegexLeaf("\\(\\)[%s]*[\\p{L}0-9_.]+"), //
						new RegexLeaf("\\(\\)[%s]*[%g][^%g]+[%g]"), //
						new RegexLeaf("\\[[^\\]*]+[^\\]]*\\]"), //
						new RegexLeaf("\\((?!\\*\\))[^\\)]+\\)"), //
						new RegexLeaf(":[^:]+:"), //
						new RegexLeaf("[%g][^%g]+[%g]") //
				));
		cmds.add(factoryNoteOnEntityCommand.createSingleLine());

		cmds.add(factoryNoteCommand.createSingleLine());
		cmds.add(new CommandUrl());
		cmds.add(new CommandCreateElementFull());
		cmds.add(new CommandCreateElementMultilines(0));
		cmds.add(new CommandCreateElementMultilines(1));

		cmds.add(factoryNoteOnEntityCommand.createMultiLine(true));
		cmds.add(factoryNoteOnEntityCommand.createMultiLine(false));
		cmds.add(factoryNoteCommand.createMultiLine(false));

		final CommandFactoryNoteOnLink factoryNoteOnLinkCommand = new CommandFactoryNoteOnLink();
		cmds.add(factoryNoteOnLinkCommand.createSingleLine());
		cmds.add(factoryNoteOnLinkCommand.createMultiLine(false));

		// cmds.add(new CommandHideShowSpecificClass());

		cmds.add(new CommandArchimate());
		cmds.add(new CommandArchimateMultilines());
		cmds.add(new CommandCreateDomain());

		return cmds;
	}

}
