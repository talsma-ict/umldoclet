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

import java.util.Map;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.ErrorUmlType;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.error.PSystemErrorUtils;
import net.sourceforge.plantuml.utils.StartUtils;
import net.sourceforge.plantuml.version.IteratorCounter2;

public abstract class PSystemSingleLineFactory extends PSystemAbstractFactory {

	protected abstract AbstractPSystem executeLine(UmlSource source, String line);

	protected PSystemSingleLineFactory() {
		super(DiagramType.UML);
	}

	@Override
	final public Diagram createSystem(UmlSource source, Map<String, String> skinParam) {

		if (source.getTotalLineCount() != 3)
			return null;

		final IteratorCounter2 it = source.iterator2();
		if (source.isEmpty()) {
			final LineLocation location = it.next().getLocation();
			return buildEmptyError(source, location, it.getTrace());
		}

		final StringLocated startLine = it.next();
		if (StartUtils.isArobaseStartDiagram(startLine.getString()) == false)
			throw new UnsupportedOperationException();

		if (it.hasNext() == false)
			return buildEmptyError(source, startLine.getLocation(), it.getTrace());

		final StringLocated s = it.next();
		if (StartUtils.isArobaseEndDiagram(s.getString()))
			return buildEmptyError(source, s.getLocation(), it.getTrace());

		final AbstractPSystem sys = executeLine(source, s.getString());
		if (sys == null) {
			final ErrorUml err = new ErrorUml(ErrorUmlType.SYNTAX_ERROR, "Syntax Error?", 0, s.getLocation());
			// return PSystemErrorUtils.buildV1(source, err, null);
			return PSystemErrorUtils.buildV2(source, err, null, it.getTrace());
		}
		return sys;

	}

}
