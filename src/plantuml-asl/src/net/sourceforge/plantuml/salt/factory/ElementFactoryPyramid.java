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
package net.sourceforge.plantuml.salt.factory;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.salt.DataSource;
import net.sourceforge.plantuml.salt.Positionner2;
import net.sourceforge.plantuml.salt.SaltDictionary;
import net.sourceforge.plantuml.salt.Terminated;
import net.sourceforge.plantuml.salt.Terminator;
import net.sourceforge.plantuml.salt.element.Element;
import net.sourceforge.plantuml.salt.element.ElementPyramid;
import net.sourceforge.plantuml.salt.element.ElementText;
import net.sourceforge.plantuml.salt.element.TableStrategy;

public class ElementFactoryPyramid extends AbstractElementFactoryComplex {

	public ElementFactoryPyramid(DataSource dataSource, SaltDictionary dictionary) {
		super(dataSource, dictionary);
	}

	public Terminated<Element> create() {
		if (ready() == false) {
			throw new IllegalStateException();
		}
		final Terminated<String> tmp = getDataSource().next();
		final String header = tmp.getElement();
		assert header.startsWith("{");
		String title = null;

		TableStrategy strategy = TableStrategy.DRAW_NONE;
		if (header.length() == 2) {
			strategy = TableStrategy.fromChar(header.charAt(1));
		}
		if (strategy == TableStrategy.DRAW_OUTSIDE_WITH_TITLE && tmp.getTerminator() == Terminator.NEWCOL) {
			title = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(getDataSource().next().getElement(), "\"");
		}

		final Positionner2 positionner = new Positionner2();

		while (getDataSource().peek(0).getElement().equals("}") == false) {
			final Terminated<Element> next = getNextElement();
			if (isStar(next.getElement())) {
				positionner.mergeLeft(next.getTerminator());
			} else {
				positionner.add(next);
			}
		}
		final Terminated<String> next = getDataSource().next();
		return new Terminated<Element>(new ElementPyramid(positionner, strategy, title, getDictionary()),
				next.getTerminator());
	}

	private boolean isStar(Element element) {
		if (element instanceof ElementText == false) {
			return false;
		}
		return "*".equals(((ElementText) element).getText());
	}

	public boolean ready() {
		final String text = getDataSource().peek(0).getElement();
		if (text.equals("{") || text.equals("{+") || text.equals("{^") || text.equals("{#") || text.equals("{!")
				|| text.equals("{-")) {
			final String text1 = getDataSource().peek(1).getElement();
			if (text1.matches("[NSEW]=|T")) {
				return false;
			}
			return true;
		}
		return false;
	}
}
