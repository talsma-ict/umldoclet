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

import net.atmp.SpecialText;
import net.sourceforge.plantuml.klimt.Fashion;
import net.sourceforge.plantuml.klimt.Shadowable;
import net.sourceforge.plantuml.klimt.UPath;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;

public class BigFrame extends AbstractTextBlock {
    // ::remove file when __HAXE__
	
	private final TextBlock title;
	private final double width;
	private final double height;
	private final Fashion symbolContext;

	public BigFrame(final TextBlock title, final double width, final double height, final Fashion symbolContext) {
		this.title = title;
		this.width = width;
		this.height = height;
		this.symbolContext = symbolContext;
	}

	private double getYpos(XDimension2D dimTitle) {
		if (dimTitle.getWidth() == 0)
			return 12;

		return dimTitle.getHeight() + 3;
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D dim = calculateDimension(stringBounder);
		ug = symbolContext.apply(ug);
		final XDimension2D dimTitle = title.calculateDimension(stringBounder);
		final double widthFull = dim.getWidth();
		final Shadowable rectangle = URectangle.build(widthFull, dim.getHeight()).rounded(symbolContext.getRoundCorner())
				.ignoreForCompressionOnX().ignoreForCompressionOnY();
		rectangle.setDeltaShadow(symbolContext.getDeltaShadow());

		ug.draw(rectangle);

		final double textWidth;
		final int cornersize;
		if (dimTitle.getWidth() == 0) {
			textWidth = widthFull / 3;
			cornersize = 7;
		} else {
			textWidth = dimTitle.getWidth() + 10;
			cornersize = 10;
		}
		final double textHeight = getYpos(dimTitle);

		final UPath line = UPath.none();
		line.setIgnoreForCompressionOnX();
		line.moveTo(textWidth, 0);

		line.lineTo(textWidth, textHeight - cornersize);
		line.lineTo(textWidth - cornersize, textHeight);

		line.lineTo(0, textHeight);
		ug.apply(HColors.none().bg()).draw(line);
		final double widthTitle = title.calculateDimension(stringBounder).getWidth();

		// Temporary hack...
		if (widthFull - widthTitle < 25)
			title.drawU(ug.apply(new UTranslate(3, 1)));
		else
			ug.apply(new UTranslate(3, 1)).draw(new SpecialText(title));

	}

	@Override
	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return new XDimension2D(width, height);
	}

}
