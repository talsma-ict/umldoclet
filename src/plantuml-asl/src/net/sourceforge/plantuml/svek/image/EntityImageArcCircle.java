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
package net.sourceforge.plantuml.svek.image;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.text.Guillemet;

public class EntityImageArcCircle extends AbstractEntityImage {

	// private static final int SIZE = 16;

	private final TextBlock name;
	private final TextBlock stereo;

	public EntityImageArcCircle(Entity entity, ISkinParam skinParam) {
		super(entity, skinParam);

		final Stereotype stereotype = entity.getStereotype();

		this.name = entity.getDisplay().create(
				FontConfiguration.create(getSkinParam(), FontParam.COMPONENT, stereotype), HorizontalAlignment.CENTER,
				skinParam);

		if (stereotype == null || stereotype.getLabel(Guillemet.DOUBLE_COMPARATOR) == null) {
			this.stereo = null;
		} else {
			this.stereo = Display.getWithNewlines(stereotype.getLabel(getSkinParam().guillemet())).create(
					FontConfiguration.create(getSkinParam(), FontParam.COMPONENT_STEREOTYPE, stereotype),
					HorizontalAlignment.CENTER, skinParam);
		}

	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		final XDimension2D dimName = name.calculateDimension(stringBounder);
		final XDimension2D dimStereo = getStereoDimension(stringBounder);
		// final Dimension2D circle = new Dimension2DDouble(SIZE, SIZE);
		return dimStereo.mergeTB(dimName);
	}

	private XDimension2D getStereoDimension(StringBounder stringBounder) {
		if (stereo == null) {
			return new XDimension2D(0, 0);
		}
		return stereo.calculateDimension(stringBounder);
	}

	final public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D dimStereo = getStereoDimension(stringBounder);
		final XDimension2D dimTotal = calculateDimension(stringBounder);
		final XDimension2D dimName = name.calculateDimension(stringBounder);

		final double nameX = (dimTotal.getWidth() - dimName.getWidth()) / 2;
		final double nameY = dimStereo.getHeight();
		name.drawU(ug.apply(new UTranslate(nameX, nameY)));

		if (stereo != null) {
			final double stereoX = (dimTotal.getWidth() - dimStereo.getWidth()) / 2;
			stereo.drawU(ug.apply(UTranslate.dx(stereoX)));
		}
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

}
