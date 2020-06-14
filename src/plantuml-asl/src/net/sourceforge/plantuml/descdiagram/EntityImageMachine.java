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
package net.sourceforge.plantuml.descdiagram;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.Guillemet;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.PlacementStrategyY1Y2;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULayoutGroup;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class EntityImageMachine extends AbstractEntityImage {
	final private TextBlock name;
	final private TextBlock stereo;
	final private Url url;

	public EntityImageMachine(ILeaf entity, ISkinParam skinParam) {
		super(entity, skinParam);
		final Stereotype stereotype = entity.getStereotype();
		this.name = TextBlockUtils.withMargin(
				entity.getDisplay().create(new FontConfiguration(getSkinParam(), FontParam.MACHINE, stereotype),
						HorizontalAlignment.CENTER, skinParam), 2, 2);
		if (stereotype == null || stereotype.getLabel(Guillemet.DOUBLE_COMPARATOR) == null) {
			this.stereo = null;
		} else {
			this.stereo = Display.create(stereotype.getLabels(skinParam.guillemet())).create(
					new FontConfiguration(getSkinParam(), FontParam.MACHINE_STEREOTYPE, stereotype),
					HorizontalAlignment.CENTER, skinParam);
		}
		this.url = entity.getUrl99();
	}

	private UStroke getStroke() {
		UStroke stroke = getSkinParam().getThickness(LineParam.machineBorder, getStereo());

		if (stroke == null) {
			stroke = new UStroke(1.5);
		}
		return stroke;
	}

	private Dimension2D getTitleDimension(StringBounder stringBounder) {
		return getNameAndSteretypeDimension(stringBounder);
	}

	private Dimension2D getNameAndSteretypeDimension(StringBounder stringBounder) {
		final Dimension2D nameDim = name.calculateDimension(stringBounder);
		final Dimension2D stereoDim = stereo == null ? new Dimension2DDouble(0, 0) : stereo
				.calculateDimension(stringBounder);
		final Dimension2D nameAndStereo = new Dimension2DDouble(Math.max(nameDim.getWidth(), stereoDim.getWidth()),
				nameDim.getHeight() + stereoDim.getHeight());
		return nameAndStereo;
	}

	public double getStartingX(StringBounder stringBounder, double y) {
		return 0;
	}

	public double getEndingX(StringBounder stringBounder, double y) {
		return calculateDimension(stringBounder).getWidth();
	}

	final public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);
		final Dimension2D dimTitle = getTitleDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();
		final URectangle rect = new URectangle(widthTotal, heightTotal, 0, 0);
		URectangle rect2 = new URectangle(4, heightTotal, 0, 0);

		ug = ug.apply(new UChangeColor(SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.machineBorder)));
		HtmlColor backcolor = getEntity().getColors(getSkinParam()).getColor(ColorType.BACK);
		if (backcolor == null) {
			backcolor = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.machineBackground);
		}
		ug = ug.apply(new UChangeBackColor(backcolor));
		if (url != null) {
			ug.startUrl(url);
		}

		final UStroke stroke = getStroke();
		ug.apply(stroke).draw(rect);
		ug.apply(new UTranslate(4, 0)).apply(stroke).draw(rect2);

		final ULayoutGroup header = new ULayoutGroup(new PlacementStrategyY1Y2(ug.getStringBounder()));
		header.add(name);
		header.drawU(ug.apply(new UTranslate(5, 0)), dimTotal.getWidth(), dimTitle.getHeight());

		if (url != null) {
			ug.closeAction();
		}
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dimTitle = getTitleDimension(stringBounder);
		final double width = dimTitle.getWidth();
		final double height = dimTitle.getHeight();
		return new Dimension2DDouble(width + 10, height);
	}

}
