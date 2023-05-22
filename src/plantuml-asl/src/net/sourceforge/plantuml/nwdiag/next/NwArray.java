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
package net.sourceforge.plantuml.nwdiag.next;

public class NwArray {
    // ::remove folder when __HAXE__

	private final NServerDraw data[][];

	public NwArray(int lines, int cols) {
		this.data = new NServerDraw[lines][cols];
	}

	@Override
	public String toString() {
		return "lines=" + getNbLines() + " cols=" + getNbCols();
	}

	public int getNbLines() {
		return data.length;
	}

	public int getNbCols() {
		return data[0].length;
	}

	public NServerDraw get(int i, int j) {
		return data[i][j];
	}

	public NServerDraw[] getLine(int i) {
		return data[i];
	}

	public void set(int i, int j, NServerDraw value) {
		data[i][j] = value;
	}

}
