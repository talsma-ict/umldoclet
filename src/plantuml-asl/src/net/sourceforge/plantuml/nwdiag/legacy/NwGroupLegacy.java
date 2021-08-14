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
package net.sourceforge.plantuml.nwdiag.legacy;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.nwdiag.core.NwGroup;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class NwGroupLegacy extends NwGroup {

	private final NetworkLegacy network;

	@Override
	public String toString() {
		return getName() + " " + network + " " + names();
	}

	public NwGroupLegacy(String name, NetworkLegacy network) {
		super(name);
		this.network = network;
	}

	public int size() {
		return names().size();
	}

	public boolean matches(LinkedElement tested) {
		if (network != null && network != tested.getNetwork()) {
			return false;
		}
		return names().contains(tested.getElement().getName());
	}

	public void drawGroup(UGraphic ug, MinMax size, ISkinParam skinParam) {
		TextBlock block = null;
		Dimension2D blockDim = null;
		if (getDescription() != null) {
			block = Display.getWithNewlines(getDescription()).create(getGroupDescriptionFontConfiguration(),
					HorizontalAlignment.LEFT, skinParam);
			blockDim = block.calculateDimension(ug.getStringBounder());
			final double dy = size.getMinY() - blockDim.getHeight();
			size = size.addPoint(size.getMinX(), dy);
		}
		HColor color = getColor();
		if (color == null) {
			color = colors.getColorOrWhite(skinParam.getThemeStyle(), "#AAA");
		}
		size.draw(ug, color);

		if (block != null) {
			block.drawU(ug.apply(new UTranslate(size.getMinX() + 5, size.getMinY())));
		}
	}

	public final NetworkLegacy getNetwork() {
		return network;
	}

}
