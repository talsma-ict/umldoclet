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
package net.sourceforge.plantuml.command;

import java.util.List;

import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.ErrorUmlType;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.api.PSystemFactory;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.error.PSystemError;
import net.sourceforge.plantuml.error.PSystemErrorUtils;

public abstract class PSystemAbstractFactory implements PSystemFactory {

	public static final String EMPTY_DESCRIPTION = "Empty description";
	private final DiagramType type;

	protected PSystemAbstractFactory(DiagramType type) {
		this.type = type;
	}

	final protected PSystemError buildEmptyError(UmlSource source, LineLocation lineLocation,
			List<StringLocated> trace) {
		final ErrorUml err = new ErrorUml(ErrorUmlType.SYNTAX_ERROR, EMPTY_DESCRIPTION, 0, lineLocation);
		final PSystemError result = PSystemErrorUtils.buildV2(source, err, null, trace);
		return result;
	}

	final protected PSystemError buildExecutionError(UmlSource source, String stringError, LineLocation lineLocation,
			List<StringLocated> trace) {
		final ErrorUml err = new ErrorUml(ErrorUmlType.EXECUTION_ERROR, stringError, 0, lineLocation);
		final PSystemError result = PSystemErrorUtils.buildV2(source, err, null, trace);
		return result;
	}

	final public DiagramType getDiagramType() {
		return type;
	}

}
