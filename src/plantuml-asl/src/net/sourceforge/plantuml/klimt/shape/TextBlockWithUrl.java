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
package net.sourceforge.plantuml.klimt.shape;

import net.atmp.InnerStrategy;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.MagneticBorder;
import net.sourceforge.plantuml.klimt.geom.MinMax;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XRectangle2D;
import net.sourceforge.plantuml.url.Url;

public class TextBlockWithUrl implements TextBlock {
    // ::remove file when __HAXE__

	private final TextBlock block;
	private final Url url;

	public static TextBlock withUrl(TextBlock block, Url url) {
		if (url == null)
			return block;

		return new TextBlockWithUrl(block, url);

	}

	private TextBlockWithUrl(TextBlock block, Url url) {
		this.block = block;
		this.url = url;
	}

	public void drawU(UGraphic ug) {
		ug.startUrl(url);
		block.drawU(ug);
		ug.closeUrl();
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return block.calculateDimension(stringBounder);
	}

	public MinMax getMinMax(StringBounder stringBounder) {
		return block.getMinMax(stringBounder);
	}

	public XRectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		return block.getInnerPosition(member, stringBounder, strategy);
	}

	@Override
	public MagneticBorder getMagneticBorder() {
		return block.getMagneticBorder();
	}

	@Override
	public HColor getBackcolor() {
		return block.getBackcolor();
	}

}
