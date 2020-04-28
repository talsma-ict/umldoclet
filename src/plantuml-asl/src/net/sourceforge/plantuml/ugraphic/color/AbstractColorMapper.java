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
package net.sourceforge.plantuml.ugraphic.color;

import java.awt.Color;

import net.sourceforge.plantuml.svek.DotStringFactory;

public abstract class AbstractColorMapper implements ColorMapper {

	final public String toHtml(HColor hcolor) {
		if (hcolor == null) {
			return null;
		}
		final Color color = toColor(hcolor);
		return DotStringFactory.sharp000000(color.getRGB());
	}

	final public String toSvg(HColor hcolor) {
		if (hcolor == null) {
			return "none";
		}
		if (hcolor instanceof HColorBackground) {
			final HColor result = ((HColorBackground) hcolor).getBack();
//			Thread.dumpStack();
//			System.exit(0);
//			return toHtml(result);
		}
		final Color color = toColor(hcolor);
		final int alpha = color.getAlpha();
		if (alpha != 255) {
			return DotStringFactory.sharpAlpha(color.getRGB());
		}
		return toHtml(hcolor);
	}

}
