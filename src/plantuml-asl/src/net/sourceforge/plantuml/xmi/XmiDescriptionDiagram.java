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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.GroupRoot;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.descdiagram.DescriptionDiagram;
import net.sourceforge.plantuml.utils.UniqueSequence;
import net.sourceforge.plantuml.version.Version;
import net.sourceforge.plantuml.xml.XmlFactories;

public class XmiDescriptionDiagram implements IXmiClassDiagram {

	private final DescriptionDiagram diagram;
	private final Document document;
	private final Element ownedElement;

	public XmiDescriptionDiagram(DescriptionDiagram diagram) throws ParserConfigurationException {
		this.diagram = diagram;

		final DocumentBuilder builder = XmlFactories.newDocumentBuilder();
		this.document = builder.newDocument();
		document.setXmlVersion("1.0");
		document.setXmlStandalone(true);

		final Element xmi = document.createElement("XMI");
		xmi.setAttribute("xmi.version", "1.1");
		xmi.setAttribute("xmlns:UML", "href://org.omg/UML/1.3");
		document.appendChild(xmi);

		final Element header = document.createElement("XMI.header");
		xmi.appendChild(header);

		final Element metamodel = document.createElement("XMI.metamodel");
		metamodel.setAttribute("xmi.name", "UML");
		metamodel.setAttribute("xmi.version", "1.3");
		header.appendChild(metamodel);

		final Element content = document.createElement("XMI.content");
		xmi.appendChild(content);

		// <UML:Model xmi.id="UMLModel.4" name="Design Model"
		// visibility="public" isSpecification="false" isRoot="false"
		// isLeaf="false" isAbstract="false">
		final Element model = document.createElement("UML:Model");
		model.setAttribute("xmi.id", CucaDiagramXmiMaker.getModel(diagram));
		model.setAttribute("name", "PlantUML "+Version.versionString());
		content.appendChild(model);

		// <UML:Namespace.ownedElement>
		this.ownedElement = document.createElement("UML:Namespace.ownedElement");
		model.appendChild(ownedElement);

		for (final IGroup gr : diagram.getGroups(false)) {
			if (gr.getParentContainer() instanceof GroupRoot) {
				addState(gr, ownedElement);
			}
		}

		for (final IEntity ent : diagram.getLeafsvalues()) {
			if (ent.getParentContainer() instanceof GroupRoot) {
				addState(ent, ownedElement);
			}
		}

		for (final Link link : diagram.getLinks()) {
			addLink(link);
		}
	}

	private void addState(final IEntity tobeAdded, Element container) {
		final Element elementState = createEntityNode(tobeAdded);
		container.appendChild(elementState);
		for (final IEntity ent : diagram.getGroups(false)) {
			if (ent.getParentContainer() == tobeAdded) {
				addState(ent, elementState);
			}
		}
		for (final IEntity ent : diagram.getLeafsvalues()) {
			if (ent.getParentContainer() == tobeAdded) {
				addState(ent, elementState);
			}
		}
	}

	public static String forXMI(String s) {
		return s.replace(':', ' ');
	}

	public static String forXMI(Display s) {
		return s.get(0).toString().replace(':', ' ');
	}

	private void addLink(Link link) {
		final String assId = "ass" + UniqueSequence.getValue();

		final Element association = document.createElement("UML:Association");
		association.setAttribute("xmi.id", assId);
		association.setAttribute("namespace", CucaDiagramXmiMaker.getModel(diagram));
		if (Display.isNull(link.getLabel()) == false) {
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

		if (link.getType().getDecor2() == LinkDecor.COMPOSITION) {
			end1.setAttribute("aggregation", "composite");
		}
		if (link.getType().getDecor2() == LinkDecor.AGREGATION) {
			end1.setAttribute("aggregation", "aggregate");
		}
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
		if (link.getType().getDecor1() == LinkDecor.COMPOSITION) {
			end2.setAttribute("aggregation", "composite");
		}
		if (link.getType().getDecor1() == LinkDecor.AGREGATION) {
			end2.setAttribute("aggregation", "aggregate");
		}
		// }
		end2.appendChild(endparticipant2);
		connection.appendChild(end2);

		association.appendChild(connection);

		ownedElement.appendChild(association);

	}

	private Element createEntityNode(IEntity entity) {
		final Element cla = document.createElement("UML:Component");

		cla.setAttribute("xmi.id", entity.getUid());
		cla.setAttribute("name", entity.getDisplay().get(0).toString());
		cla.setAttribute("namespace", CucaDiagramXmiMaker.getModel(diagram));

		final Element feature = document.createElement("UML:Classifier.feature");
		cla.appendChild(feature);

//		for (Member m : entity.getBodier().getFieldsToDisplay()) {
//			final Element attribute = document.createElement("UML:Attribute");
//			attribute.setAttribute("xmi.id", "att" + UniqueSequence.getValue());
//			attribute.setAttribute("name", m.getDisplay(false));
//			feature.appendChild(attribute);
//		}
//
//		for (Member m : entity.getBodier().getMethodsToDisplay()) {
//			final Element operation = document.createElement("UML:Operation");
//			operation.setAttribute("xmi.id", "att" + UniqueSequence.getValue());
//			operation.setAttribute("name", m.getDisplay(false));
//			feature.appendChild(operation);
//		}
		return cla;
	}

	public void transformerXml(OutputStream os) throws TransformerException, ParserConfigurationException {
		final Source source = new DOMSource(document);

		final Result resultat = new StreamResult(os);

		final Transformer transformer = XmlFactories.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		transformer.setOutputProperty(OutputKeys.ENCODING, UTF_8.name());
		// tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(source, resultat);
	}

}
