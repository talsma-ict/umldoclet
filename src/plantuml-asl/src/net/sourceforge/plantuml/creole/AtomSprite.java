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
package net.sourceforge.plantuml.creole;

import java.awt.geom.Dimension2D;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.sprite.Sprite;

public class AtomSprite extends AbstractAtom implements Atom {

	private final Sprite sprite;
	private final double scale;
	private final Url url;
	private final HtmlColor color;

	@Override
	public List<Atom> splitInTwo(StringBounder stringBounder, double width) {
		return Arrays.asList((Atom) this);
	}

	public AtomSprite(HtmlColor newColor, double scale, FontConfiguration fontConfiguration, Sprite sprite, Url url) {
		this.scale = scale;
		this.sprite = sprite;
		this.url = url;
		this.color = newColor == null ? fontConfiguration.getColor() : newColor;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return sprite.asTextBlock(color, scale).calculateDimension(stringBounder);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return 0;
	}

	public void drawU(UGraphic ug) {
		if (url != null) {
			ug.startUrl(url);
		}
		sprite.asTextBlock(color, scale).drawU(ug);
		if (url != null) {
			ug.closeAction();
		}
	}

}
