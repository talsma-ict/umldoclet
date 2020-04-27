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
package net.sourceforge.plantuml.creole.atom;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.openiconic.OpenIcon;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class AtomOpenIcon extends AbstractAtom implements Atom {

	private final OpenIcon openIcon;
	private final double factor;
	private final Url url;
	private final HColor color;

	public AtomOpenIcon(HColor newColor, double scale, OpenIcon openIcon, FontConfiguration fontConfiguration,
			Url url) {
		this.url = url;
		this.openIcon = openIcon;
		this.factor = scale * fontConfiguration.getSize2D() / 12.0;
		this.color = newColor == null ? fontConfiguration.getColor() : newColor;
	}

	private TextBlock asTextBlock() {
		return TextBlockUtils.withMargin(openIcon.asTextBlock(color, factor), 1, 0);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return asTextBlock().calculateDimension(stringBounder);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return -3 * factor;
	}

	public void drawU(UGraphic ug) {
		if (url != null) {
			ug.startUrl(url);
		}
		asTextBlock().drawU(ug);
		if (url != null) {
			ug.closeAction();
		}
	}

}
