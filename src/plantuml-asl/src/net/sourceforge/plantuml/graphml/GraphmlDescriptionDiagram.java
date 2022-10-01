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
package net.sourceforge.plantuml.graphml;

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

import net.sourceforge.plantuml.cucadiagram.GroupRoot;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.descdiagram.DescriptionDiagram;
import net.sourceforge.plantuml.xmi.XmlDiagramTransformer;
import net.sourceforge.plantuml.xml.XmlFactories;

public class GraphmlDescriptionDiagram implements XmlDiagramTransformer {

	private final DescriptionDiagram diagram;
	private final Document document;

	public GraphmlDescriptionDiagram(DescriptionDiagram diagram) throws ParserConfigurationException {
		this.diagram = diagram;

		final DocumentBuilder builder = XmlFactories.newDocumentBuilder();
		this.document = builder.newDocument();
		document.setXmlVersion("1.0");
		document.setXmlStandalone(true);

		final Element graphml = document.createElement("graphml");
		graphml.setAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		graphml.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		graphml.setAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		document.appendChild(graphml);

		final Element graph = document.createElement("graph");
		graph.setAttribute("edgedefault", "undirected");
		graphml.appendChild(graph);

		for (final IEntity ent : diagram.getLeafsvalues())
			if (ent.getParentContainer() instanceof GroupRoot)
				addElement(ent, graph);

	}

	private void addElement(IEntity tobeAdded, Element container) {
		final Element element = createEntityNode(tobeAdded);
		container.appendChild(element);

	}

	private Element createEntityNode(IEntity entity) {
		final Element cla = document.createElement("node");
		cla.setAttribute("id", entity.getCode().getName());
		return cla;
	}

	public void transformerXml(OutputStream os) throws TransformerException, ParserConfigurationException {
		final Source source = new DOMSource(document);

		final Result resultat = new StreamResult(os);

		final Transformer transformer = XmlFactories.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, UTF_8.name());
		transformer.transform(source, resultat);
	}

}
