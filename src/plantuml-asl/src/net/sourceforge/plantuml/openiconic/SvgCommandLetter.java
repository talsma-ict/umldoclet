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

import net.sourceforge.plantuml.StringUtils;

public class SvgCommandLetter implements SvgCommand {

	final private char letter;

	public SvgCommandLetter(String letter) {
		if (letter.matches("[a-zA-Z]") == false) {
			throw new IllegalArgumentException();
		}
		this.letter = letter.charAt(0);
	}

	@Override
	public String toString() {
		return super.toString() + " " + letter;
	}

	public String toSvg() {
		return "" + letter;
	}

	public int argumentNumber() {
		switch (StringUtils.goLowerCase(letter)) {
		case 'm':
		case 'M':
		case 'l':
			return 2;
		case 'z':
			return 0;
		case 'c':
			return 6;
		case 's':
			return 4;
		case 'a':
			return 7;
		}
		throw new UnsupportedOperationException("" + letter);
	}

//	public UGraphic drawMe(UGraphic ug, Iterator<SvgCommand> it) {
//		System.err.println("drawMe " + letter);
//		final List<SvgCommandNumber> numbers = new ArrayList<>();
//		for (int i = 0; i < argumentNumber(); i++) {
//			numbers.add((SvgCommandNumber) it.next());
//		}
//		return drawMe(ug, numbers);
//	}
//
//	private UGraphic drawMe(UGraphic ug, List<SvgCommandNumber> numbers) {
//		switch (letter) {
//		case 'M':
//			final double x = numbers.get(0).getDouble();
//			final double y = numbers.get(1).getDouble();
//			return ug.apply(new UTranslate(x, y));
//		}
//		return ug;
//
//	}

	public boolean isUpperCase() {
		return Character.isUpperCase(letter);
	}

	public boolean is(char c) {
		return this.letter == c;
	}

	public char getLetter() {
		return letter;
	}
}
