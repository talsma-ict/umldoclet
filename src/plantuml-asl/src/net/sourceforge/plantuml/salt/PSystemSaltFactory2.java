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
package net.sourceforge.plantuml.salt;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.PSystemCommandFactory;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;

public class PSystemSaltFactory2 extends PSystemCommandFactory {

	public PSystemSaltFactory2(DiagramType init) {
		super(init);
	}

	@Override
	protected List<Command> createCommands() {

		final List<Command> cmds = new ArrayList<>();
		if (getDiagramType() == DiagramType.UML) {
			cmds.add(new CommandSalt());
		}
		addCommonCommands2(cmds);
		addTitleCommands(cmds);
		cmds.add(new CommandAnything());

		return cmds;
	}

	@Override
	public PSystemSalt createEmptyDiagram(UmlSource source, ISkinSimple skinParam) {
		final PSystemSalt result = new PSystemSalt(source);
		if (getDiagramType() == DiagramType.SALT) {
			result.setIamSalt(true);
		}
		return result;
	}

}
