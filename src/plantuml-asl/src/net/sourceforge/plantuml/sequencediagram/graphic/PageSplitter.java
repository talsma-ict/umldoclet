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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.sequencediagram.Newpage;

class PageSplitter {

	private final double fullHeight;
	private final List<Double> positions;
	private final List<Display> titles;
	private final double headerHeight;
	private final double tailHeight;
	private final double signatureHeight;
	private final double newpageHeight;
	private final Display diagramTitle;

	PageSplitter(double fullHeight, double headerHeight, Map<Newpage, Double> newpages, double tailHeight,
			double signatureHeight, double newpageHeight, Display diagramTitle) {
		this.fullHeight = fullHeight;
		this.diagramTitle = diagramTitle;
		this.titles = new ArrayList<Display>();
		this.positions = new ArrayList<Double>();

		for (Map.Entry<Newpage, Double> ent : newpages.entrySet()) {
			titles.add(ent.getKey().getTitle());
			positions.add(ent.getValue());
		}

		this.headerHeight = headerHeight;
		this.tailHeight = tailHeight;
		this.signatureHeight = signatureHeight;
		this.newpageHeight = newpageHeight;
	}

	public List<Page> getPages() {

		if (positions.size() == 0) {
			return Arrays.asList(onePage());
		}
		final List<Page> result = new ArrayList<Page>();

		result.add(firstPage());
		for (int i = 0; i < positions.size() - 1; i++) {
			result.add(createPage(i));
		}
		result.add(lastPage());

		return result;
	}

	private Page lastPage() {
		final double newpage1 = positions.get(positions.size() - 1) - this.newpageHeight;
		final double newpage2 = this.fullHeight - this.tailHeight - this.signatureHeight;
		final Display title = titles.get(positions.size() - 1);
		return new Page(headerHeight, newpage1, newpage2, tailHeight, signatureHeight, title);
	}

	private Page firstPage() {
		final double newpage1 = this.headerHeight;
		final double newpage2 = positions.get(0) + this.newpageHeight;
		return new Page(headerHeight, newpage1, newpage2, tailHeight, 0, diagramTitle);
	}

	private Page onePage() {
		final double newpage1 = this.headerHeight;
		final double newpage2 = this.fullHeight - this.tailHeight - this.signatureHeight;
		return new Page(headerHeight, newpage1, newpage2, tailHeight, signatureHeight, diagramTitle);
	}

	private Page createPage(int i) {
		final double newpage1 = positions.get(i) - this.newpageHeight;
		final double newpage2 = positions.get(i + 1) + this.newpageHeight;
		final Display title = titles.get(i);
		return new Page(headerHeight, newpage1, newpage2, tailHeight, 0, title);
	}

}
