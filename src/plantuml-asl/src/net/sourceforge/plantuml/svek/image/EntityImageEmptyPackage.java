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
package net.sourceforge.plantuml.svek.image;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.AlignmentParam;
import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.Guillemet;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.EntityImageLegend;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.DisplayPositioned;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.entity.EntityImpl;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Cluster;
import net.sourceforge.plantuml.svek.ClusterDecoration;
import net.sourceforge.plantuml.svek.GeneralImageBuilder;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class EntityImageEmptyPackage extends AbstractEntityImage {

	private final TextBlock desc;
	private final static int MARGIN = 10;

	private final Stereotype stereotype;
	private final TextBlock stereoBlock;
	private final Url url;
	private final SName styleName;
	private final double shadowing;
	private final HColor borderColor;
	private final UStroke stroke;
	private final double roundCorner;
	private final HColor back;

	private Style getStyle() {
		return getDefaultStyleDefinition().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	private StyleSignature getDefaultStyleDefinition() {
		return StyleSignature.of(SName.root, SName.element, styleName, SName.package_).with(stereotype);
	}

	public EntityImageEmptyPackage(ILeaf entity, ISkinParam skinParam, PortionShower portionShower, SName styleName) {
		super(entity, skinParam);
		this.styleName = styleName;

		final Colors colors = entity.getColors();
		final HColor specificBackColor = colors.getColor(ColorType.BACK);
		this.stereotype = entity.getStereotype();
		final FontConfiguration titleFontConfiguration;
		final HorizontalAlignment titleHorizontalAlignment;
		this.url = entity.getUrl99();

		if (UseStyle.useBetaStyle()) {
			Style style = getStyle();
			style = style.eventuallyOverride(colors);
			this.borderColor = style.value(PName.LineColor).asColor(getSkinParam().getThemeStyle(),
					getSkinParam().getIHtmlColorSet());
			this.shadowing = style.value(PName.Shadowing).asDouble();
			this.stroke = style.getStroke(colors);
			this.roundCorner = style.value(PName.RoundCorner).asDouble();
			if (specificBackColor == null) {
				this.back = style.value(PName.BackGroundColor).asColor(getSkinParam().getThemeStyle(),
						getSkinParam().getIHtmlColorSet());
			} else {
				this.back = specificBackColor;
			}
			titleFontConfiguration = style.getFontConfiguration(getSkinParam().getThemeStyle(),
					getSkinParam().getIHtmlColorSet());
			titleHorizontalAlignment = style.getHorizontalAlignment();
		} else {
			this.borderColor = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.packageBorder);
			this.shadowing = getSkinParam().shadowing(getEntity().getStereotype()) ? 3 : 0;
			this.stroke = GeneralImageBuilder.getForcedStroke(getEntity().getStereotype(), getSkinParam());
			this.roundCorner = 0;
			this.back = Cluster.getBackColor(specificBackColor, skinParam, stereotype, styleName, USymbol.PACKAGE);
			titleFontConfiguration = new FontConfiguration(getSkinParam(), FontParam.PACKAGE, stereotype);
			titleHorizontalAlignment = HorizontalAlignment.CENTER;
		}

		this.desc = entity.getDisplay().create(titleFontConfiguration, titleHorizontalAlignment, skinParam);

		final DisplayPositioned legend = ((EntityImpl) entity).getLegend();
		if (legend != null) {
			final TextBlock legendBlock = EntityImageLegend.create(legend.getDisplay(), skinParam);
			stereoBlock = legendBlock;
		} else {
			if (stereotype == null || stereotype.getLabel(Guillemet.DOUBLE_COMPARATOR) == null
					|| portionShower.showPortion(EntityPortion.STEREOTYPE, entity) == false) {
				stereoBlock = TextBlockUtils.empty(0, 0);
			} else {
				stereoBlock = TextBlockUtils.withMargin(Display.create(stereotype.getLabels(skinParam.guillemet()))
						.create(new FontConfiguration(getSkinParam(), FontParam.PACKAGE_STEREOTYPE, stereotype),
								titleHorizontalAlignment, skinParam),
						1, 0);
			}
		}

	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dimDesc = desc.calculateDimension(stringBounder);
		Dimension2D dim = TextBlockUtils.mergeTB(desc, stereoBlock, HorizontalAlignment.LEFT)
				.calculateDimension(stringBounder);
		dim = Dimension2DDouble.atLeast(dim, 0, 2 * dimDesc.getHeight());
		return Dimension2DDouble.delta(dim, MARGIN * 2, MARGIN * 2);
	}

	final public void drawU(UGraphic ug) {
		if (url != null) {
			ug.startUrl(url);
		}

		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();

		final ClusterDecoration decoration = new ClusterDecoration(getSkinParam().packageStyle(), null, desc,
				stereoBlock, 0, 0, widthTotal, heightTotal, stroke);

		final HorizontalAlignment horizontalAlignment = getSkinParam()
				.getHorizontalAlignment(AlignmentParam.packageTitleAlignment, null, false, null);
		final HorizontalAlignment stereotypeAlignment = getSkinParam().getStereotypeAlignment();

		decoration.drawU(ug, back, borderColor, shadowing, roundCorner, horizontalAlignment, stereotypeAlignment);

		if (url != null) {
			ug.closeUrl();
		}

	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

}
