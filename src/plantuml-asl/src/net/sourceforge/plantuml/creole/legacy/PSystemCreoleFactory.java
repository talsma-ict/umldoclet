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
package net.sourceforge.plantuml.creole.legacy;

import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.command.PSystemBasicFactory;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;

public class PSystemCreoleFactory extends PSystemBasicFactory<PSystemCreole> {

	public PSystemCreoleFactory() {
		super(DiagramType.CREOLE);
	}

	@Override
	public PSystemCreole initDiagram(ThemeStyle style, UmlSource source, String startLine) {
		if (getDiagramType() == DiagramType.CREOLE)
			return new PSystemCreole(source);

		return null;
	}

	@Override
	public PSystemCreole executeLine(ThemeStyle style, UmlSource source, PSystemCreole system, String line) {
		system.doCommandLine(line);
		return system;
	}

}
