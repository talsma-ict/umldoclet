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
package net.sourceforge.plantuml.svek;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockEmpty;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.utils.MathUtils;

public class HeaderLayout {

	final private TextBlock name;
	final private TextBlock stereo;
	final private TextBlock generic;
	final private TextBlock circledCharacter;

	public HeaderLayout(TextBlock circledCharacter, TextBlock stereo, TextBlock name, TextBlock generic) {
		this.circledCharacter = protectAgaintNull(circledCharacter);
		this.stereo = protectAgaintNull(stereo);
		this.name = protectAgaintNull(name);
		this.generic = protectAgaintNull(generic);
	}

	private static TextBlock protectAgaintNull(TextBlock block) {
		if (block == null) {
			return new TextBlockEmpty();
		}
		return block;
	}

	public Dimension2D getDimension(StringBounder stringBounder) {
		final Dimension2D nameDim = name.calculateDimension(stringBounder);
		final Dimension2D genericDim = generic.calculateDimension(stringBounder);
		final Dimension2D stereoDim = stereo.calculateDimension(stringBounder);
		final Dimension2D circleDim = circledCharacter.calculateDimension(stringBounder);

		final double width = circleDim.getWidth() + Math.max(stereoDim.getWidth(), nameDim.getWidth())
				+ genericDim.getWidth();
		final double height = MathUtils.max(circleDim.getHeight(), stereoDim.getHeight() + nameDim.getHeight() + 10,
				genericDim.getHeight());
		return new Dimension2DDouble(width, height);
	}

	public void drawU(UGraphic ug, double width, double height) {

		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D nameDim = name.calculateDimension(stringBounder);
		final Dimension2D genericDim = generic.calculateDimension(stringBounder);
		final Dimension2D stereoDim = stereo.calculateDimension(stringBounder);
		final Dimension2D circleDim = circledCharacter.calculateDimension(stringBounder);

		final double widthStereoAndName = Math.max(stereoDim.getWidth(), nameDim.getWidth());
		final double suppWith = Math.max(0, width - circleDim.getWidth() - widthStereoAndName - genericDim.getWidth());
		assert suppWith >= 0;

		final double h2 = Math.min(circleDim.getWidth() / 4, suppWith * 0.1);
		final double h1 = (suppWith - h2) / 2;
		assert h1 >= 0;
		assert h2 >= 0;

		final double xCircle = h1;
		final double yCircle = (height - circleDim.getHeight()) / 2;
		circledCharacter.drawU(ug.apply(new UTranslate(xCircle, yCircle)));

		final double diffHeight = height - stereoDim.getHeight() - nameDim.getHeight();
		final double xStereo = circleDim.getWidth() + (widthStereoAndName - stereoDim.getWidth()) / 2 + h1 + h2;
		final double yStereo = diffHeight / 2;
		stereo.drawU(ug.apply(new UTranslate(xStereo, yStereo)));

		final double xName = circleDim.getWidth() + (widthStereoAndName - nameDim.getWidth()) / 2 + h1 + h2;
		final double yName = diffHeight / 2 + stereoDim.getHeight();
		name.drawU(ug.apply(new UTranslate(xName, yName)));

		if (genericDim.getWidth() > 0) {
			final double delta = 4;
			final double xGeneric = width - genericDim.getWidth() + delta;
			final double yGeneric = -delta;
			generic.drawU(ug.apply(new UTranslate(xGeneric, yGeneric)));
		}
	}

}
