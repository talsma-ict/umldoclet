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
package net.sourceforge.plantuml.style;

import net.sourceforge.plantuml.ThemeStyle;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public abstract class ValueAbstract implements Value {

	public String asString() {
		throw new UnsupportedOperationException("Class=" + getClass());
	}

	public HColor asColor(ThemeStyle themeStyle, HColorSet set) {
		throw new UnsupportedOperationException("Class=" + getClass());
	}

	public int asInt() {
		throw new UnsupportedOperationException("Class=" + getClass());
	}

	public double asDouble() {
		throw new UnsupportedOperationException("Class=" + getClass());
	}

	public boolean asBoolean() {
		throw new UnsupportedOperationException("Class=" + getClass());
	}

	public int asFontStyle() {
		throw new UnsupportedOperationException("Class=" + getClass());
	}

	public HorizontalAlignment asHorizontalAlignment() {
		throw new UnsupportedOperationException("Class=" + getClass());
	}

	public int getPriority() {
		throw new UnsupportedOperationException("Class=" + getClass());
	}

}
