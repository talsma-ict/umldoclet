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

import java.io.IOException;
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

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.api.ImageDataSimple;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.sequencediagram.graphic.FileMaker;
import net.sourceforge.plantuml.xml.XmlFactories;

public final class SequenceDiagramXmiMaker implements FileMaker {

	private final SequenceDiagram diagram;
	private final FileFormat fileFormat;

	public SequenceDiagramXmiMaker(SequenceDiagram sequenceDiagram, FileFormat fileFormat) {
		this.diagram = sequenceDiagram;
		this.fileFormat = fileFormat;
	}

	@Override
	public ImageData createOne(OutputStream os, int index, boolean isWithMetadata) throws IOException {
		DocumentBuilder builder;
		ImageData imageData = new ImageDataSimple(0, 0);
		try {
			builder = XmlFactories.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return imageData;
		}
		Document document = builder.newDocument();
		document.setXmlVersion("1.0");
		document.setXmlStandalone(true);

		XmiSequenceDiagram xmi;
		if (fileFormat == FileFormat.XMI_ARGO)
			xmi = new XmiSequenceDiagramArgo(diagram, document);
		else
			xmi = new XmiSequenceDiagramStandard(diagram, document);
		
		xmi.build();
		
		try {
			writeDocument(document, os);
		} catch (TransformerException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		return imageData;
	}
	
	
	@Override
	public int getNbPages() {
		return 1;
	}

	private void writeDocument(Document document, OutputStream os)
			throws TransformerException, ParserConfigurationException {
		final Source source = new DOMSource(document);

		final Result resultat = new StreamResult(os);

		final Transformer transformer = XmlFactories.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, UTF_8.name());
		transformer.transform(source, resultat);
	}

}
