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
package net.sourceforge.plantuml.nwdiag;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.graphic.HtmlColor;

public class Network {

	private final String name;
	private final Map<DiagElement, String> localElements = new LinkedHashMap<DiagElement, String>();
	private HtmlColor color;

	private String ownAdress;

	@Override
	public String toString() {
		return name;
	}

	public Network(String name) {
		this.name = name;
	}

	public String getAdress(DiagElement element) {
		return localElements.get(element);
	}

	public void addElement(DiagElement element, Map<String, String> props) {
		String address = props.get("address");
		if (address == null) {
			address = "";
		}
		if (address.length() == 0 && localElements.containsKey(element)) {
			return;
		}
		localElements.put(element, address);
	}

	public boolean constainsLocally(String name) {
		for (DiagElement element : localElements.keySet()) {
			if (element.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public final String getOwnAdress() {
		return ownAdress;
	}

	public final void setOwnAdress(String ownAdress) {
		this.ownAdress = ownAdress;
	}

	public final String getName() {
		return name;
	}

	public final HtmlColor getColor() {
		return color;
	}

	public final void setColor(HtmlColor color) {
		this.color = color;
	}

}
