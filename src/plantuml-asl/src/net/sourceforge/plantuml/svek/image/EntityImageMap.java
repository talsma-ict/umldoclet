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

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.EntityPortion;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.abel.LineConfigurable;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.cucadiagram.TextBlockMap;
import net.sourceforge.plantuml.klimt.Shadowable;
import net.sourceforge.plantuml.klimt.UGroupType;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.creole.Stencil;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.drawing.UGraphicStencil;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.PlacementStrategyY1Y2;
import net.sourceforge.plantuml.klimt.geom.ULayoutGroup;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
import net.sourceforge.plantuml.klimt.shape.URectangle;
import net.sourceforge.plantuml.skin.CornerParam;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.svek.WithPorts;
import net.sourceforge.plantuml.text.Guillemet;
import net.sourceforge.plantuml.url.Url;

public class EntityImageMap extends AbstractEntityImage implements Stencil, WithPorts {

	final private TextBlock name;
	final private TextBlock stereo;
	final private TextBlock entries;
	final private Url url;
	final private double roundCorner;

	final private LineConfigurable lineConfig;

	public EntityImageMap(Entity entity, ISkinParam skinParam, PortionShower portionShower) {
		super(entity, skinParam);
		this.lineConfig = entity;
		final Stereotype stereotype = entity.getStereotype();
		this.roundCorner = skinParam.getRoundCorner(CornerParam.DEFAULT, null);

		final FontConfiguration fcHeader = getStyleHeader().getFontConfiguration(getSkinParam().getIHtmlColorSet());

		this.name = TextBlockUtils
				.withMargin(entity.getDisplay().create(fcHeader, HorizontalAlignment.CENTER, skinParam), 2, 2);

		if (stereotype == null || stereotype.getLabel(Guillemet.DOUBLE_COMPARATOR) == null
				|| portionShower.showPortion(EntityPortion.STEREOTYPE, entity) == false)
			this.stereo = null;
		else
			this.stereo = Display.create(stereotype.getLabels(skinParam.guillemet())).create(
					FontConfiguration.create(getSkinParam(), FontParam.OBJECT_STEREOTYPE, stereotype),
					HorizontalAlignment.CENTER, skinParam);

		final FontConfiguration fontConfiguration = getStyleHeader()
				.getFontConfiguration(getSkinParam().getIHtmlColorSet());
		this.entries = entity.getBodier().getBody(skinParam, false, false, entity.getStereotype(), getStyle(),
				fontConfiguration);

		this.url = entity.getUrl99();

	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		final XDimension2D dimTitle = getTitleDimension(stringBounder);
		return ((WithPorts) entries).getPorts(stringBounder).translateY(dimTitle.getHeight());
	}

	private int marginEmptyFieldsOrMethod = 13;

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		final XDimension2D dimTitle = getTitleDimension(stringBounder);
		final XDimension2D dimFields = entries.calculateDimension(stringBounder);
		double width = Math.max(dimFields.getWidth(), dimTitle.getWidth() + 2 * xMarginCircle);
		final double minClassWidth = getStyle().value(PName.MinimumWidth).asDouble();
		if (width < minClassWidth)
			width = minClassWidth;

		final double height = getMethodOrFieldHeight(dimFields) + dimTitle.getHeight();
		return new XDimension2D(width, height);
	}

	private Style getStyle() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.objectDiagram, SName.map)
				.withTOBECHANGED(getEntity().getStereotype()).getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	private Style getStyleHeader() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.objectDiagram, SName.map, SName.header)
				.withTOBECHANGED(getEntity().getStereotype()).getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	final public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D dimTotal = calculateDimension(stringBounder);
		final XDimension2D dimTitle = getTitleDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();
		final Shadowable rect = URectangle.build(widthTotal, heightTotal).rounded(roundCorner);

		HColor borderColor = lineConfig.getColors().getColor(ColorType.LINE);
		HColor backcolor = getEntity().getColors().getColor(ColorType.BACK);
		HColor headerBackcolor = getEntity().getColors().getColor(ColorType.HEADER);

		final Style style = getStyle();

		if (borderColor == null)
			borderColor = getStyle().value(PName.LineColor).asColor(getSkinParam().getIHtmlColorSet());

		if (headerBackcolor == null)
			headerBackcolor = backcolor == null
					? getStyleHeader().value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet())
					: backcolor;

		if (backcolor == null)
			backcolor = style.value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet());

		rect.setDeltaShadow(style.value(PName.Shadowing).asDouble());
		final UStroke stroke = style.getStroke();

		ug = ug.apply(borderColor).apply(backcolor.bg());

		if (url != null)
			ug.startUrl(url);

		final Map<UGroupType, String> typeIDent = new EnumMap<>(UGroupType.class);
		typeIDent.put(UGroupType.CLASS, "elem " + getEntity().getName() + " selected");
		typeIDent.put(UGroupType.ID, "elem_" + getEntity().getName());
		ug.startGroup(typeIDent);
		ug.apply(stroke).draw(rect);

		if (roundCorner == 0 && headerBackcolor != null && backcolor.equals(headerBackcolor) == false) {
			final Shadowable rect2 = URectangle.build(widthTotal, dimTitle.getHeight());
			final UGraphic ugHeader = ug.apply(headerBackcolor.bg());
			ugHeader.apply(stroke).draw(rect2);
		}

		final ULayoutGroup header = new ULayoutGroup(new PlacementStrategyY1Y2(ug.getStringBounder()));
		if (stereo != null)
			header.add(stereo);

		header.add(name);
		header.drawU(ug, dimTotal.getWidth(), dimTitle.getHeight());

		final UGraphic ug2 = UGraphicStencil.create(ug, this, stroke);
		((TextBlockMap) entries).setTotalWidth(dimTotal.getWidth());
		entries.drawU(ug2.apply(UTranslate.dy(dimTitle.getHeight())));

		if (url != null)
			ug.closeUrl();

		ug.closeGroup();
	}

	private double getMethodOrFieldHeight(final XDimension2D dim) {
		final double fieldsHeight = dim.getHeight();
		if (fieldsHeight == 0 && this.getEntity().getLeafType() != LeafType.MAP)
			return marginEmptyFieldsOrMethod;

		return fieldsHeight;
	}

	private int xMarginCircle = 5;

	private XDimension2D getTitleDimension(StringBounder stringBounder) {
		return getNameAndSteretypeDimension(stringBounder);
	}

	private XDimension2D getNameAndSteretypeDimension(StringBounder stringBounder) {
		final XDimension2D nameDim = name.calculateDimension(stringBounder);
		final XDimension2D stereoDim = stereo == null ? new XDimension2D(0, 0)
				: stereo.calculateDimension(stringBounder);
		final XDimension2D nameAndStereo = new XDimension2D(Math.max(nameDim.getWidth(), stereoDim.getWidth()),
				nameDim.getHeight() + stereoDim.getHeight());
		return nameAndStereo;
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE_HTML_FOR_PORTS;
	}

	public double getStartingX(StringBounder stringBounder, double y) {
		return 0;
	}

	public double getEndingX(StringBounder stringBounder, double y) {
		return calculateDimension(stringBounder).getWidth();
	}

}
