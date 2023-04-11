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
package net.sourceforge.plantuml.klimt.geom;

public enum USegmentType {

	SEG_MOVETO, //
	SEG_LINETO, //
	SEG_QUADTO, //
	SEG_CUBICTO, //
	SEG_CLOSE, //
	SEG_ARCTO;//

	final public static int SEG_ARCTO_VALUE = 4321;

	public int getNbPoints() {
		switch (this) {
		case SEG_MOVETO:
			return 1;
		case SEG_LINETO:
			return 1;
		case SEG_CUBICTO:
			return 3;
		case SEG_CLOSE:
			return 0;
		}
		throw new UnsupportedOperationException();
	}

}
