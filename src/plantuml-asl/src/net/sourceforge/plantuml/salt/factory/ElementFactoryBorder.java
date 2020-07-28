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
package net.sourceforge.plantuml.salt.factory;

import net.sourceforge.plantuml.salt.DataSource;
import net.sourceforge.plantuml.salt.Dictionary;
import net.sourceforge.plantuml.salt.Terminated;
import net.sourceforge.plantuml.salt.element.Element;
import net.sourceforge.plantuml.salt.element.ElementBorder;

public class ElementFactoryBorder extends AbstractElementFactoryComplex {

	public ElementFactoryBorder(DataSource dataSource, Dictionary dictionary) {
		super(dataSource, dictionary);
	}

	public Terminated<Element> create() {
		if (ready() == false) {
			throw new IllegalStateException();
		}
		final String header = getDataSource().next().getElement();
		assert header.startsWith("{");

//		TableStrategy strategy = TableStrategy.DRAW_NONE;
//		if (header.length() == 2) {
//			strategy = TableStrategy.fromChar(header.charAt(1));
//		}

		final ElementBorder result = new ElementBorder();

		while (getDataSource().peek(0).getElement().equals("}") == false) {
			final String pos = getDataSource().next().getElement();
			switch (pos.charAt(0)) {
			case 'N':
				result.setNorth(getNextElement().getElement());
				break;
			case 'S':
				result.setSouth(getNextElement().getElement());
				break;
			case 'E':
				result.setEast(getNextElement().getElement());
				break;
			case 'W':
				result.setWest(getNextElement().getElement());
				break;
			case 'C':
				result.setCenter(getNextElement().getElement());
				break;
			default:
				throw new IllegalStateException();

			}
		}
		final Terminated<String> next = getDataSource().next();
		return new Terminated<Element>(result, next.getTerminator());
	}

	public boolean ready() {
		final String text = getDataSource().peek(0).getElement();
		if (text.equals("{") || text.equals("{+") || text.equals("{#") || text.equals("{!") || text.equals("{-")) {
			final String text1 = getDataSource().peek(1).getElement();
			if (text1.matches("[NSEW]=")) {
				return true;
			}
			return false;
		}
		return false;
	}
}
