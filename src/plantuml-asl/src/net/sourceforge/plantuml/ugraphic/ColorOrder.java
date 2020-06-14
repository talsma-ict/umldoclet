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

import java.awt.Color;

public enum ColorOrder {
	RGB, RBG, GRB, GBR, BRG, BGR;

	public Color getColor(Color color) {
		if (this == RGB) {
			return new Color(color.getRed(), color.getGreen(), color.getBlue());
		}
		if (this == RBG) {
			return new Color(color.getRed(), color.getBlue(), color.getGreen());
		}
		if (this == GRB) {
			return new Color(color.getGreen(), color.getRed(), color.getBlue());
		}
		if (this == GBR) {
			return new Color(color.getGreen(), color.getBlue(), color.getRed());
		}
		if (this == BRG) {
			return new Color(color.getBlue(), color.getRed(), color.getGreen());
		}
		if (this == BGR) {
			return new Color(color.getBlue(), color.getGreen(), color.getRed());
		}
		throw new IllegalStateException();
	}

	public Color getReverse(Color color) {
		color = this.getColor(color);
		return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
	}

	public static ColorOrder fromString(String order) {
		try {
			return ColorOrder.valueOf(order.toUpperCase());
		} catch (Exception e) {
			return null;
		}
	}

}
