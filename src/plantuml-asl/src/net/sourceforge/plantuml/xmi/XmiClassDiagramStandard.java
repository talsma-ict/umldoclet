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
package net.sourceforge.plantuml.xmi;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.cucadiagram.IEntity;

public class XmiClassDiagramStandard extends XmiClassDiagramAbstract implements IXmiClassDiagram {

	public XmiClassDiagramStandard(ClassDiagram classDiagram) throws ParserConfigurationException {
		super(classDiagram);

		for (final IEntity ent : classDiagram.getLeafsvalues()) {
			// if (fileFormat == FileFormat.XMI_ARGO && isStandalone(ent) == false) {
			// continue;
			// }
			final Element cla = createEntityNode(ent);
			if (cla == null) {
				continue;
			}
			ownedElement.appendChild(cla);
			done.add(ent);
		}

		// if (fileFormat != FileFormat.XMI_STANDARD) {
		// for (final Link link : classDiagram.getLinks()) {
		// addLink(link);
		// }
		// }
	}

	// private boolean isStandalone(IEntity ent) {
	// for (final Link link : classDiagram.getLinks()) {
	// if (link.getEntity1() == ent || link.getEntity2() == ent) {
	// return false;
	// }
	// }
	// return true;
	// }

}
