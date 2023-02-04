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

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.cucadiagram.ICucaDiagram;
import net.sourceforge.plantuml.descdiagram.DescriptionDiagram;
import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.utils.Log;
import net.sourceforge.plantuml.xmi.XmlDiagramTransformer;

public final class CucaDiagramGraphmlMaker {

	private final ICucaDiagram diagram;

	public CucaDiagramGraphmlMaker(ICucaDiagram diagram) throws IOException {
		this.diagram = diagram;
	}

	public static String getModel(UmlDiagram classDiagram) {
		return "model1";
	}

	public void createFiles(OutputStream fos) throws IOException {
		try {
			final XmlDiagramTransformer xmi;
			if (diagram instanceof DescriptionDiagram)
				xmi = new GraphmlDescriptionDiagram((DescriptionDiagram) diagram);
			else
				throw new UnsupportedOperationException();

			xmi.transformerXml(fos);
		} catch (ParserConfigurationException e) {
			Log.error(e.toString());
			Logme.error(e);
			throw new IOException(e.toString());
		} catch (TransformerException e) {
			Log.error(e.toString());
			Logme.error(e);
			throw new IOException(e.toString());
		}
	}

}
