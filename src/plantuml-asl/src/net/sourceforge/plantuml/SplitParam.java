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
package net.sourceforge.plantuml;

import java.awt.Color;

import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSimple;

public class SplitParam {

	private final HColor borderColor;
	private final HColor externalColor;
	private final int externalMargin;

	private static SplitParam empty() {
		return new SplitParam(null, null, 0);
	}

	public SplitParam(HColor borderColor, HColor externalColor, int externalMargin) {
		if (borderColor != null && externalMargin == 0) {
			externalMargin = 1;
		}
		this.borderColor = borderColor;
		this.externalColor = externalColor;
		this.externalMargin = externalMargin;
	}

	public boolean isSet() {
		return externalMargin > 0;
	}

	public int getExternalMargin() {
		return externalMargin;
	}

	public Color getBorderColor() {
		if (borderColor == null) {
			return null;
		}
		return ((HColorSimple) borderColor).getColor999();
	}

	public Color getExternalColor() {
		if (externalColor == null) {
			return null;
		}
		return ((HColorSimple) externalColor).getColor999();
	}

}
