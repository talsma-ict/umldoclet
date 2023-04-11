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
package net.sourceforge.plantuml.sequencediagram.teoz;

import net.sourceforge.plantuml.klimt.Fashion;

public class Step {
    // ::remove folder when __HAXE__

	private final double value;
	private final boolean destroy;
	private final int indent;
	private final Fashion color;

	public Step(double value, boolean destroy, int indent, Fashion color) {
		if (indent < 0) {
			throw new IllegalArgumentException();
		}
		this.indent = indent;
		this.color = color;
		this.value = value;
		this.destroy = destroy;
	}

	public double getValue() {
		return value;
	}

	public boolean isDestroy() {
		return destroy;
	}

	public int getIndent() {
		return indent;
	}

	public Fashion getColors() {
		return color;
	}

}
