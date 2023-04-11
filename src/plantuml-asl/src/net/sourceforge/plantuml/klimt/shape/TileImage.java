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

import java.awt.image.BufferedImage;
import java.util.Objects;

import net.atmp.PixelImage;
import net.sourceforge.plantuml.klimt.AffineTransformType;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.ImgValign;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;

public class TileImage extends AbstractTextBlock implements TextBlock {
    // ::remove file when __HAXE__

	private final BufferedImage image;
	private final int vspace;

	public TileImage(BufferedImage image, ImgValign valign, int vspace) {
		this.image = Objects.requireNonNull(image);
		this.vspace = vspace;
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return new XDimension2D(image.getWidth(), image.getHeight() + 2 * vspace);
	}

	public void drawU(UGraphic ug) {
		ug.apply(UTranslate.dy(vspace)).draw(new UImage(new PixelImage(image, AffineTransformType.TYPE_BILINEAR)));
	}

}
