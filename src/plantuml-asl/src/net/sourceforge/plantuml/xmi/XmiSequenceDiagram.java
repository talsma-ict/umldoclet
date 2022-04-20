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
package net.sourceforge.plantuml.xmi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;

public abstract class XmiSequenceDiagram {

	protected final SequenceDiagram diagram;

	public abstract void build();

	protected final Document document;

	public XmiSequenceDiagram(SequenceDiagram diagram, Document document) {
		super();
		this.diagram = diagram;
		this.document = document;
	}

	protected Element createElement(String tag, String[][] attributes) {
		return setAttributes(document.createElement(tag), attributes);
	}
	
	protected Element setAttribute(Element element, String name, String value) {
		element.setAttribute(name, value);
		return element;
	}

	protected Element setAttributes(Element element, String[][] attributes) {
		for (String[] attr : attributes) {
			element.setAttribute(attr[0], attr[1]);
		}
		return element;
	}
	
	protected String getDisplayString(Display display) {
		return String.join("\n", display.asList());
	}

	protected String getXmiId(String tag, Object object) {
		return Integer.toHexString(tag.hashCode()) + "_" + Integer.toHexString(object.hashCode());
	}
}
