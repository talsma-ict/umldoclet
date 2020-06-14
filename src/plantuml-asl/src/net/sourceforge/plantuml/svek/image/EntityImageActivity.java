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
package net.sourceforge.plantuml.svek.image;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Bibliotekon;
import net.sourceforge.plantuml.svek.Shape;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class EntityImageActivity extends AbstractEntityImage {

	public static final int CORNER = 25;
	final private TextBlock desc;
	final private static int MARGIN = 10;
	final private Url url;
	private final Bibliotekon bibliotekon;

	public EntityImageActivity(ILeaf entity, ISkinParam skinParam, Bibliotekon bibliotekon) {
		super(entity, skinParam);
		this.bibliotekon = bibliotekon;
		final Stereotype stereotype = entity.getStereotype();

		this.desc = entity.getDisplay().create(new FontConfiguration(getSkinParam(), FontParam.ACTIVITY, stereotype),
				HorizontalAlignment.CENTER, skinParam);
		this.url = entity.getUrl99();
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim = desc.calculateDimension(stringBounder);
		return Dimension2DDouble.delta(dim, MARGIN * 2);
	}

	final public void drawU(UGraphic ug) {
		if (url != null) {
			ug.startUrl(url);
		}
		if (getShapeType() == ShapeType.ROUND_RECTANGLE) {
			ug = drawNormal(ug);
		} else if (getShapeType() == ShapeType.OCTAGON) {
			ug = drawOctagon(ug);
		} else {
			throw new UnsupportedOperationException();
		}
		if (url != null) {
			ug.closeAction();
		}
	}

	private UGraphic drawOctagon(UGraphic ug) {
		final Shape shape = bibliotekon.getShape(getEntity());
		final Shadowable octagon = shape.getOctagon();
		if (getSkinParam().shadowing(getEntity().getStereotype())) {
			octagon.setDeltaShadow(4);
		}
		ug = applyColors(ug);
		ug.apply(new UStroke(1.5)).draw(octagon);
		desc.drawU(ug.apply(new UTranslate(MARGIN, MARGIN)));
		return ug;

	}

	private UGraphic drawNormal(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();
		final Shadowable rect = new URectangle(widthTotal, heightTotal, CORNER, CORNER);
		if (getSkinParam().shadowing(getEntity().getStereotype())) {
			rect.setDeltaShadow(4);
		}

		ug = applyColors(ug);
		ug.apply(new UStroke(1.5)).draw(rect);

		desc.drawU(ug.apply(new UTranslate(MARGIN, MARGIN)));
		return ug;
	}

	private UGraphic applyColors(UGraphic ug) {
		ug = ug.apply(new UChangeColor(SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.activityBorder)));
		HtmlColor backcolor = getEntity().getColors(getSkinParam()).getColor(ColorType.BACK);
		if (backcolor == null) {
			backcolor = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.activityBackground);
		}
		ug = ug.apply(new UChangeBackColor(backcolor));
		return ug;
	}

	public ShapeType getShapeType() {
		final Stereotype stereotype = getStereo();
		if (getSkinParam().useOctagonForActivity(stereotype)) {
			return ShapeType.OCTAGON;
		}
		return ShapeType.ROUND_RECTANGLE;
	}

}
