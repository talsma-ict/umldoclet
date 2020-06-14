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
package net.sourceforge.plantuml.ugraphic.sprite;

import java.awt.geom.Dimension2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FileUtils;
import net.sourceforge.plantuml.SvgString;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImageSvg;

public class SpriteSvg implements Sprite {

	private final UImageSvg img;

	public SpriteSvg(String svg) {
		this.img = new UImageSvg(new SvgString(svg, 1));
	}

	public SpriteSvg(File svgFile) throws IOException {
		this(FileUtils.readSvg(svgFile));
	}

	public SpriteSvg(InputStream is) throws IOException {
		this(FileUtils.readSvg(is));
	}

	public TextBlock asTextBlock(final HtmlColor color, final double scale) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				ug.draw(img);
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return new Dimension2DDouble(img.getWidth() * scale, img.getHeight() * scale);
			}
		};
	}

}
