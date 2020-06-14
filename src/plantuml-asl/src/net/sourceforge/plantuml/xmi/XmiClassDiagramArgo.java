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
package net.sourceforge.plantuml.xmi;

import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.utils.UniqueSequence;

import org.w3c.dom.Element;

public class XmiClassDiagramArgo extends XmiClassDiagramAbstract implements IXmiClassDiagram {

	public XmiClassDiagramArgo(ClassDiagram classDiagram) throws ParserConfigurationException {
		super(classDiagram);

		for (final IEntity ent : classDiagram.getLeafsvalues()) {
			if (classDiagram.isStandalone(ent) == false) {
				continue;
			}
			final Element cla = createEntityNode(ent);
			if (cla == null) {
				continue;
			}
			ownedElement.appendChild(cla);
			done.add(ent);
		}

		// if (fileFormat != FileFormat.XMI_STANDARD) {
		for (final Link link : classDiagram.getLinks()) {
			addLink(link);
		}
		// }
	}

	private void addLink(Link link) {
		final String assId = "ass" + UniqueSequence.getValue();
		// if ((link.getType().getDecor1() == LinkDecor.EXTENDS || link.getType().getDecor2() == LinkDecor.EXTENDS)
		// && fileFormat == FileFormat.XMI_STAR) {
		// addExtension(link, assId);
		// return;
		// }
		final Element association = document.createElement("UML:Association");
		association.setAttribute("xmi.id", assId);
		association.setAttribute("namespace", CucaDiagramXmiMaker.getModel(classDiagram));
		if (link.getLabel() != null) {
			association.setAttribute("name", forXMI(link.getLabel()));
		}

		final Element connection = document.createElement("UML:Association.connection");
		final Element end1 = document.createElement("UML:AssociationEnd");
		end1.setAttribute("xmi.id", "end" + UniqueSequence.getValue());
		end1.setAttribute("association", assId);
		end1.setAttribute("type", link.getEntity1().getUid());
		if (link.getQualifier1() != null) {
			end1.setAttribute("name", forXMI(link.getQualifier1()));
		}
		final Element endparticipant1 = document.createElement("UML:AssociationEnd.participant");
		// if (fileFormat == FileFormat.XMI_ARGO) {
		if (done.contains(link.getEntity1())) {
			endparticipant1.appendChild(createEntityNodeRef(link.getEntity1()));
		} else {
			final Element element = createEntityNode(link.getEntity1());
			if (element == null) {
				return;
			}
			endparticipant1.appendChild(element);
			done.add(link.getEntity1());
		}
		// } else if (fileFormat == FileFormat.XMI_STAR) {
		// if (link.getType().getDecor2() == LinkDecor.COMPOSITION) {
		// end1.setAttribute("aggregation", "composite");
		// }
		// if (link.getType().getDecor2() == LinkDecor.AGREGATION) {
		// end1.setAttribute("aggregation", "aggregate");
		// }
		// }
		end1.appendChild(endparticipant1);
		connection.appendChild(end1);

		final Element end2 = document.createElement("UML:AssociationEnd");
		end2.setAttribute("xmi.id", "end" + UniqueSequence.getValue());
		end2.setAttribute("association", assId);
		end2.setAttribute("type", link.getEntity2().getUid());
		if (link.getQualifier2() != null) {
			end2.setAttribute("name", forXMI(link.getQualifier2()));
		}
		final Element endparticipant2 = document.createElement("UML:AssociationEnd.participant");
		// if (fileFormat == FileFormat.XMI_ARGO) {
		if (done.contains(link.getEntity2())) {
			endparticipant2.appendChild(createEntityNodeRef(link.getEntity2()));
		} else {
			final Element element = createEntityNode(link.getEntity2());
			if (element == null) {
				return;
			}
			endparticipant2.appendChild(element);
			done.add(link.getEntity2());
		}
		// } else if (fileFormat == FileFormat.XMI_STAR) {
		// if (link.getType().getDecor1() == LinkDecor.COMPOSITION) {
		// end2.setAttribute("aggregation", "composite");
		// }
		// if (link.getType().getDecor1() == LinkDecor.AGREGATION) {
		// end2.setAttribute("aggregation", "aggregate");
		// }
		// }
		end2.appendChild(endparticipant2);
		connection.appendChild(end2);

		association.appendChild(connection);

		ownedElement.appendChild(association);

	}

	private Element createEntityNodeRef(IEntity entity) {
		final Element cla = document.createElement("UML:Class");
		cla.setAttribute("xmi.idref", entity.getUid());
		return cla;
	}

}
