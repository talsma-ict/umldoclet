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
package net.sourceforge.plantuml.creole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.graphic.HorizontalAlignment;

public class Sheet implements Iterable<Stripe> {

	private final List<Stripe> stripes = new ArrayList<Stripe>();
	private final HorizontalAlignment horizontalAlignment;

	public Sheet(HorizontalAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}
	
	@Override
	public String toString() {
		return stripes.toString();
	}

	public void add(Stripe stripe) {
		stripes.add(stripe);
	}

	public Iterator<Stripe> iterator() {
		return stripes.iterator();
	}

	public Stripe getLastStripe() {
		final int size = stripes.size();
		if (size == 0) {
			return null;
		}
		return stripes.get(size - 1);
	}

	public final HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}
}
