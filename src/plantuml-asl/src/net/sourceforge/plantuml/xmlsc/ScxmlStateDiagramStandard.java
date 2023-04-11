/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.xmlsc;

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

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.statediagram.StateDiagram;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.text.Guillemet;
import net.sourceforge.plantuml.xml.XmlFactories;

public class ScxmlStateDiagramStandard {
	// ::remove folder when __CORE__

	private final StateDiagram diagram;
	private final Document document;

	public ScxmlStateDiagramStandard(StateDiagram diagram) throws ParserConfigurationException {
		this.diagram = diagram;

		final DocumentBuilder builder = XmlFactories.newDocumentBuilder();
		this.document = builder.newDocument();
		document.setXmlVersion("1.0");
		document.setXmlStandalone(true);

		final Element scxml = document.createElement("scxml");
		scxml.setAttribute("xmlns", "http://www.w3.org/2005/07/scxml");
		scxml.setAttribute("version", "1.0");
		final String initial = getInitial();
		if (initial != null)
			scxml.setAttribute("initial", initial);

		document.appendChild(scxml);

		for (final Entity ent : diagram.getEntityFactory().leafs())
			if (ent.getParentContainer().isRoot())
				scxml.appendChild(createState(ent));

		for (Entity ent : diagram.getEntityFactory().groups())
			if (ent.getParentContainer().isRoot())
				exportGroup(scxml, ent);

	}

	private Element exportGroup(Element dest, Entity ent) {
		final Element gr = createGroup(ent);
		dest.appendChild(gr);
		for (Entity leaf : ent.leafs())
			gr.appendChild(createState(leaf));
		for (Entity child : ent.groups())
			exportGroup(gr, child);
		return gr;
	}

	private String getInitial() {
		for (final Entity ent : diagram.getEntityFactory().leafs())
			if (ent.getLeafType() == LeafType.CIRCLE_START)
				return getId(ent);

		return null;
	}

	private Element createGroup(Entity entity) {
		return createState(entity);
	}

	private Element createState(Entity entity) {
		final LeafType type = entity.getLeafType();

		final Element state = document.createElement("state");
		if (type == LeafType.NOTE) {
			state.setAttribute("stereotype", "note");
			state.setAttribute("id", entity.getName());
			final Display display = entity.getDisplay();
			final StringBuilder sb = new StringBuilder();
			for (CharSequence s : display) {
				sb.append(s);
				sb.append("\n");
			}
			if (sb.length() > 0)
				sb.setLength(sb.length() - 1);
			final Comment comment = document.createComment(sb.toString());
			state.appendChild(comment);

		} else {
			state.setAttribute("id", getId(entity));
			final Stereotype stereotype = entity.getStereotype();
			if (stereotype != null)
				state.setAttribute("stereotype", stereotype.getLabels(Guillemet.NONE).get(0));

			for (final Link link : diagram.getLinks())
				if (link.getEntity1() == entity)
					addLink(state, link);
		}

		return state;
	}

	private void addLink(Element state, Link link) {
		final Element transition = document.createElement("transition");
		final Display label = link.getLabel();
		if (Display.isNull(label) == false) {
			final String event = label.get(0).toString();
			transition.setAttribute("event", event);
		}
		transition.setAttribute("target", getId(link.getEntity2()));
		state.appendChild(transition);

	}

	private String getId(Entity entity) {
		return entity.getName().replaceAll("\\*", "");
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
