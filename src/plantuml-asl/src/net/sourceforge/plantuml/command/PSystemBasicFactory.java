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
package net.sourceforge.plantuml.command;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.ErrorUmlType;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.error.PSystemErrorUtils;
import net.sourceforge.plantuml.utils.StartUtils;
import net.sourceforge.plantuml.version.IteratorCounter2;

public abstract class PSystemBasicFactory<P extends AbstractPSystem> extends PSystemAbstractFactory {

	public PSystemBasicFactory(DiagramType diagramType) {
		super(diagramType);
	}

	public PSystemBasicFactory() {
		this(DiagramType.UML);
	}

	public abstract P executeLine(UmlSource source, P system, String line);

	public abstract P initDiagram(UmlSource source, String startLine);

	private boolean isEmptyLine(StringLocated result) {
		return result.getTrimmed().getString().length() == 0;
	}

	@Override
	final public Diagram createSystem(UmlSource source, ISkinSimple skinParam) {
		source = source.removeInitialSkinparam();
		final IteratorCounter2 it = source.iterator2();
		final StringLocated startLine = it.next();
		P system = initDiagram(source, startLine.getString());
		boolean first = true;
		while (it.hasNext()) {
			final StringLocated s = it.next();
			if (first && s != null && isEmptyLine(s)) {
				continue;
			}
			first = false;
			if (StartUtils.isArobaseEndDiagram(s.getString())) {
				if (source.getTotalLineCount() == 2 && source.isStartDef() == false) {
					return buildEmptyError(source, s.getLocation(), it.getTrace());
				}
				return system;
			}
			system = executeLine(source, system, s.getString());
			if (system == null) {
				final ErrorUml err = new ErrorUml(ErrorUmlType.SYNTAX_ERROR, "Syntax Error?", 0, s.getLocation());
				// return PSystemErrorUtils.buildV1(source, err, null);
				return PSystemErrorUtils.buildV2(source, err, null, it.getTrace());
			}
		}
		return system;
	}

}
