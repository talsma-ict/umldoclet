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
package net.sourceforge.plantuml.ugraphic;

import java.awt.geom.PathIterator;
import java.util.EnumSet;

import net.sourceforge.plantuml.ugraphic.arc.ExtendedPathIterator;

public enum USegmentType {

	SEG_MOVETO(PathIterator.SEG_MOVETO), SEG_LINETO(PathIterator.SEG_LINETO), SEG_QUADTO(PathIterator.SEG_QUADTO), SEG_CUBICTO(
			PathIterator.SEG_CUBICTO), SEG_CLOSE(PathIterator.SEG_CLOSE), SEG_ARCTO(ExtendedPathIterator.SEG_ARCTO);

	private final int code;

	private USegmentType(int code) {
		this.code = code;
	}

	public static USegmentType getByCode(int code) {
		for (USegmentType p : EnumSet.allOf(USegmentType.class)) {
			if (p.code == code) {
				return p;
			}
		}
		throw new IllegalArgumentException();
	}
}
