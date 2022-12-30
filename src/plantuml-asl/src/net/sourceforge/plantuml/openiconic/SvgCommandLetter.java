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
package net.sourceforge.plantuml.openiconic;

import net.sourceforge.plantuml.StringUtils;

public class SvgCommandLetter implements SvgCommand {

	final private char letter;

	public SvgCommandLetter(String letter) {
		if (letter.matches("[mlhvzsacqtMLHVZSACQT]") == false) 
			throw new IllegalArgumentException(letter);
		
		this.letter = letter.charAt(0);
	}

	@Override
	public String toString() {
		return " " + letter;
	}

	public String toSvg() {
		return "" + letter;
	}

	public int argumentNumber() {
		switch (StringUtils.goLowerCase(letter)) {
		case 'm':
		case 'l':
		case 't':
			return 2;
		case 'h':
		case 'v':
			return 1;
		case 'z':
			return 0;
		case 'c':
			return 6;
		case 'q':
		case 's':
			return 4;
		case 'a':
			return 7;
		}
		throw new UnsupportedOperationException("" + letter);
	}

	public boolean isUpperCase() {
		return Character.isUpperCase(letter);
	}

	public boolean is(char c) {
		return this.letter == c;
	}

	public char getLetter() {
		return letter;
	}

	public SvgCommandLetter implicit() {
		// https://stackoverflow.com/questions/29251389/svg-path-spec-moveto-and-implicit-lineto
		if (letter == 'm')
			return new SvgCommandLetter("l");

		if (letter == 'M')
			return new SvgCommandLetter("L");

		return this;
	}
}
