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
package net.sourceforge.plantuml.mindmap;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tetris {

	private final StripeFrontier frontier = new StripeFrontier();
	private final List<SymetricalTeePositioned> elements = new ArrayList<>();
	private double minY = Double.MAX_VALUE;
	private double maxY = -Double.MAX_VALUE;
	private String name;

	public Tetris(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name + "(" + elements.size() + ")";
	}

	public void balance() {
		if (elements.size() == 0) {
			return;
		}
		if (minY != Double.MAX_VALUE) {
			throw new IllegalStateException();
		}
		for (SymetricalTeePositioned element : elements) {
			minY = Math.min(minY, element.getMinY());
			maxY = Math.max(maxY, element.getMaxY());
		}
		final double mean = (minY + maxY) / 2;
		for (SymetricalTeePositioned stp : elements) {
			stp.move(-mean);
		}
		// System.err.println("Balanced=" + this + " " + elements);
	}

	public double getHeight() {
		if (elements.size() == 0) {
			return 0;
		}
		return maxY - minY;
	}

	public double getWidth() {
		double result = 0;
		for (SymetricalTeePositioned tee : elements) {
			result = Math.max(result, tee.getMaxX());
		}
		return result;
	}

	public void add(SymetricalTee tee) {
		// System.err.println("Adding in " + this + " " + tee);

		if (frontier.isEmpty()) {
			final SymetricalTeePositioned p1 = new SymetricalTeePositioned(tee);
			addInternal(p1);
			return;
		}

		// System.err.println("frontier=" + frontier);

		final double c1 = frontier.getContact(0, tee.getElongation1());
		final double c2 = frontier.getContact(tee.getElongation1(), tee.getElongation1() + tee.getElongation2());

		// System.err.println("c1=" + c1 + " c2=" + c2);

		final SymetricalTeePositioned p1 = new SymetricalTeePositioned(tee);
		p1.moveSoThatSegmentA1isOn(c1);

		final SymetricalTeePositioned p2 = new SymetricalTeePositioned(tee);
		p2.moveSoThatSegmentA2isOn(c2);

		final SymetricalTeePositioned result = p1.getMax(p2);

		// System.err.println("p1=" + p1.getY() + " p2=" + p2.getY());
		// System.err.println("result=" + result.getY());
		addInternal(result);
	}

	private void addInternal(SymetricalTeePositioned result) {
		this.elements.add(result);
		final Line2D b1 = result.getSegmentB1();
		frontier.addSegment(b1.getX1(), b1.getX2(), b1.getY1());
		assert b1.getY1() == b1.getY2();

		final Line2D b2 = result.getSegmentB2();
		if (b2.getX1() != b2.getX2()) {
			frontier.addSegment(b2.getX1(), b2.getX2(), b2.getY1());
		}
		assert b2.getY1() == b2.getY2();
	}

	public List<SymetricalTeePositioned> getElements() {
		return Collections.unmodifiableList(elements);
	}

}
