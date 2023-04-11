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
package net.sourceforge.plantuml.dedication;

import java.awt.image.BufferedImage;
import java.util.Objects;

import net.atmp.PixelImage;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.PlainDiagram;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.klimt.AffineTransformType;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.UImage;

public class PSystemDedication extends PlainDiagram {

	private final BufferedImage img;

	public PSystemDedication(UmlSource source, BufferedImage img) {
		super(source);
		this.img = Objects.requireNonNull(img);
	}

	@Override
	protected UDrawable getRootDrawable(FileFormatOption fileFormatOption) {
		// return ug -> ug.draw(new UImage(new PixelImage(img,
		// AffineTransformType.TYPE_BILINEAR)));
		return new UDrawable() {
			public void drawU(UGraphic ug) {
				ug.draw(new UImage(new PixelImage(img, AffineTransformType.TYPE_BILINEAR)));
			}
		};
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Dedication)");
	}

}
