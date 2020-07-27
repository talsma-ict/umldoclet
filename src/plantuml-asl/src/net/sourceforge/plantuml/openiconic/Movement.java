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
package net.sourceforge.plantuml.openiconic;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Movement {

	private final SvgCommandLetter letter;
	private final List<SvgCommandNumber> arguments;

	public Movement(Iterator<SvgCommand> it) {
		this.letter = (SvgCommandLetter) it.next();
		final int nb = letter.argumentNumber();
		final SvgCommandNumber args[] = new SvgCommandNumber[nb];
		for (int i = 0; i < nb; i++) {
			args[i] = (SvgCommandNumber) it.next();
		}
		this.arguments = Arrays.asList(args);
	}

	private Movement(SvgCommandLetter letter, SvgCommandNumber... args) {
		this.letter = letter;
		this.arguments = Arrays.asList(args);
	}

	private Movement(SvgCommandLetter letter) {
		this.letter = letter;
		this.arguments = Collections.emptyList();
	}

	public char getLetter() {
		return letter.getLetter();
	}

	private Movement(SvgCommandLetter letter, SvgPosition... pos) {
		this.letter = letter;
		final SvgCommandNumber args[] = new SvgCommandNumber[pos.length * 2];
		for (int i = 0; i < pos.length; i++) {
			args[2 * i] = pos[i].getX();
			args[2 * i + 1] = pos[i].getY();
		}
		this.arguments = Arrays.asList(args);
	}

	public Movement mutoToC(SvgPosition mirrorControlPoint) {
		if (is('S') == false) {
			throw new UnsupportedOperationException();
		}
		if (mirrorControlPoint == null) {
			// return this;
			// throw new IllegalArgumentException();
			return new Movement(new SvgCommandLetter("C"), this.getSvgPosition(0), this.getSvgPosition(0),
					lastPosition());
		}
		return new Movement(new SvgCommandLetter("C"), mirrorControlPoint, this.getSvgPosition(0), lastPosition());
	}

	public String toSvg() {
		final StringBuilder result = new StringBuilder();
		result.append(letter.toSvg());
		result.append(' ');
		for (SvgCommandNumber arg : arguments) {
			result.append(arg.toSvg());
			result.append(' ');
		}
		return result.toString();
	}

	public SvgPosition getSvgPosition(int index) {
		return new SvgPosition(arguments.get(index), arguments.get(index + 1));
	}

	public double getArgument(int index) {
		return arguments.get(index).getDouble();
	}

	public SvgPosition lastPosition() {
		if (letter.argumentNumber() == 0) {
			return null;
		}
		return getSvgPosition(arguments.size() - 2);
		// final SvgCommandNumber lastX = arguments.get(arguments.size() - 2);
		// final SvgCommandNumber lastY = arguments.get(arguments.size() - 1);
		// return new SvgPosition(lastX, lastY);
	}

	// public SvgPosition firstPosition() {
	// return getSvgPosition(0);
	// // final SvgCommandNumber firstX = arguments.get(0);
	// // final SvgCommandNumber firstY = arguments.get(1);
	// // return new SvgPosition(firstX, firstY);
	// }

	public Movement toAbsoluteUpperCase(SvgPosition delta) {
		if (delta == null) {
			throw new IllegalArgumentException();
		}
		if (letter.isUpperCase()) {
			return this;
		}
		if (letter.is('m')) {
			return new Movement(new SvgCommandLetter("M"), delta.add(getSvgPosition(0)));
		}
		if (letter.is('l')) {
			return new Movement(new SvgCommandLetter("L"), delta.add(getSvgPosition(0)));
		}
		if (letter.is('z')) {
			return new Movement(new SvgCommandLetter("Z"));
		}
		if (letter.is('c')) {
			return new Movement(new SvgCommandLetter("C"), delta.add(getSvgPosition(0)), delta.add(getSvgPosition(2)),
					delta.add(getSvgPosition(4)));
		}
		if (letter.is('s')) {
			return new Movement(new SvgCommandLetter("S"), delta.add(getSvgPosition(0)), delta.add(getSvgPosition(2)));
		}
		if (letter.is('a')) {
			final SvgPosition last = delta.add(lastPosition());
			// System.err.println("LAST=" + last);
			return new Movement(new SvgCommandLetter("A"), arguments.get(0), arguments.get(1), arguments.get(2),
					arguments.get(3), arguments.get(4), last.getX(), last.getY());
		}
		// A still to be done
		// System.err.println("Movement::goUpperCase " + letter);
		throw new UnsupportedOperationException("Movement::goUpperCase " + letter);
	}

	public SvgPosition getMirrorControlPoint() {
		if (letter.is('c')) {
			throw new IllegalStateException();
		}
		if (letter.is('s')) {
			throw new IllegalStateException();
		}
		if (letter.is('C')) {
			final SvgPosition center = lastPosition();
			final SvgPosition controlPoint = getSvgPosition(2);
			return center.getMirror(controlPoint);
		}
		if (letter.is('S')) {
			final SvgPosition center = lastPosition();
			final SvgPosition controlPoint = getSvgPosition(0);
			return center.getMirror(controlPoint);
		}
		return null;
	}

	public boolean is(char c) {
		return letter.is(c);
	}

}
