/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.Guillemet;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.CircledCharacter;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockGeneric;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.HeaderLayout;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class EntityImageClassHeader extends AbstractEntityImage {

	final private HeaderLayout headerLayout;

	public EntityImageClassHeader(ILeaf entity, ISkinParam skinParam, PortionShower portionShower) {
		super(entity, skinParam);

		final boolean italic = entity.getLeafType() == LeafType.ABSTRACT_CLASS
				|| entity.getLeafType() == LeafType.INTERFACE;

		final Stereotype stereotype = entity.getStereotype();
		final boolean displayGenericWithOldFashion = skinParam.displayGenericWithOldFashion();
		final String generic = displayGenericWithOldFashion ? null : entity.getGeneric();

		final Style style = StyleSignatureBasic
				.of(SName.root, SName.element, SName.classDiagram, SName.class_, SName.header) //
				.withTOBECHANGED(stereotype) //
				.with(entity.getStereostyles()) //
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		FontConfiguration fontConfigurationName = FontConfiguration.create(skinParam, style, entity.getColors());

		if (italic)
			fontConfigurationName = fontConfigurationName.italic();

		Display display = entity.getDisplay();
		if (displayGenericWithOldFashion && entity.getGeneric() != null)
			display = display.addGeneric(entity.getGeneric());

		TextBlock name = display.createWithNiceCreoleMode(fontConfigurationName, HorizontalAlignment.CENTER, skinParam);
		final VisibilityModifier modifier = entity.getVisibilityModifier();
		if (modifier == null) {
			name = TextBlockUtils.withMargin(name, 3, 3, 0, 0);
		} else {
			final Rose rose = new Rose();
			final HColor back = rose.getHtmlColor(skinParam, modifier.getBackground());
			final HColor fore = rose.getHtmlColor(skinParam, modifier.getForeground());

			final TextBlock uBlock = modifier.getUBlock(skinParam.classAttributeIconSize(), fore, back, false);
			name = TextBlockUtils.mergeLR(uBlock, name, VerticalAlignment.CENTER);
			name = TextBlockUtils.withMargin(name, 3, 3, 0, 0);
		}

		final TextBlock stereo;
		if (stereotype == null || stereotype.getLabel(Guillemet.DOUBLE_COMPARATOR) == null
				|| portionShower.showPortion(EntityPortion.STEREOTYPE, entity) == false)
			stereo = null;
		else
			stereo = TextBlockUtils.withMargin(Display.create(stereotype.getLabels(skinParam.guillemet())).create(
					FontConfiguration.create(getSkinParam(), FontParam.CLASS_STEREOTYPE, stereotype),
					HorizontalAlignment.CENTER, skinParam), 1, 0);

		TextBlock genericBlock;
		if (generic == null) {
			genericBlock = null;
		} else {
			genericBlock = Display.getWithNewlines(generic).create(
					FontConfiguration.create(getSkinParam(), FontParam.CLASS_STEREOTYPE, stereotype),
					HorizontalAlignment.CENTER, skinParam);
			genericBlock = TextBlockUtils.withMargin(genericBlock, 1, 1);
			final HColor classBackground = SkinParamUtils.getColor(getSkinParam(), stereotype, ColorParam.background);

			final HColor classBorder = SkinParamUtils.getFontColor(getSkinParam(), FontParam.CLASS_STEREOTYPE,
					stereotype);
			genericBlock = new TextBlockGeneric(genericBlock, classBackground, classBorder);
			genericBlock = TextBlockUtils.withMargin(genericBlock, 1, 1);
		}

		final TextBlock circledCharacter;
		if (portionShower.showPortion(EntityPortion.CIRCLED_CHARACTER, (ILeaf) getEntity()))
			circledCharacter = TextBlockUtils.withMargin(getCircledCharacter(entity, skinParam), 4, 0, 5, 5);
		else
			circledCharacter = null;

		this.headerLayout = new HeaderLayout(circledCharacter, stereo, name, genericBlock);
	}

	private TextBlock getCircledCharacter(ILeaf entity, ISkinParam skinParam) {
		final Stereotype stereotype = entity.getStereotype();
		if (stereotype != null && stereotype.getSprite(skinParam) != null)
			return stereotype.getSprite(skinParam);

		final UFont font = SkinParamUtils.getFont(getSkinParam(), FontParam.CIRCLED_CHARACTER, null);

		final LeafType leafType = entity.getLeafType();

		final Style style = spotStyleSignature(leafType).getMergedStyle(skinParam.getCurrentStyleBuilder());
		HColor spotBorder = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(),
				skinParam.getIHtmlColorSet());
		final HColor spotBackColor = style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(),
				skinParam.getIHtmlColorSet());
		final HColor classBorder = SkinParamUtils.getColor(getSkinParam(), stereotype, ColorParam.classBorder);
		final HColor fontColor = style.value(PName.FontColor).asColor(skinParam.getThemeStyle(),
				skinParam.getIHtmlColorSet());

		if (stereotype != null && stereotype.getCharacter() != 0)
			return new CircledCharacter(stereotype.getCharacter(), getSkinParam().getCircledCharacterRadius(), font,
					stereotype.getHtmlColor(), classBorder, fontColor);

		if (spotBorder == null)
			spotBorder = classBorder;

		char circledChar = 0;
		if (stereotype != null)
			circledChar = getSkinParam().getCircledCharacter(stereotype);

		if (circledChar == 0)
			circledChar = getCircledChar(leafType);

		return new CircledCharacter(circledChar, getSkinParam().getCircledCharacterRadius(), font, spotBackColor,
				spotBorder, fontColor);
	}

	private StyleSignatureBasic spotStyleSignature(LeafType leafType) {
		switch (leafType) {
		case ANNOTATION:
			return StyleSignatureBasic.of(SName.root, SName.element, SName.spot, SName.spotAnnotation);
		case ABSTRACT_CLASS:
			return StyleSignatureBasic.of(SName.root, SName.element, SName.spot, SName.spotAbstractClass);
		case CLASS:
			return StyleSignatureBasic.of(SName.root, SName.element, SName.spot, SName.spotClass);
		case INTERFACE:
			return StyleSignatureBasic.of(SName.root, SName.element, SName.spot, SName.spotInterface);
		case ENUM:
			return StyleSignatureBasic.of(SName.root, SName.element, SName.spot, SName.spotEnum);
		case ENTITY:
			return StyleSignatureBasic.of(SName.root, SName.element, SName.spot, SName.spotEntity);
		case PROTOCOL:
			return StyleSignatureBasic.of(SName.root, SName.element, SName.spot, SName.spotProtocol);
		case STRUCT:
			return StyleSignatureBasic.of(SName.root, SName.element, SName.spot, SName.spotStruct);
		}
		throw new IllegalStateException();
	}

	private char getCircledChar(LeafType leafType) {
		switch (leafType) {
		case ANNOTATION:
			return '@';
		case ABSTRACT_CLASS:
			return 'A';
		case CLASS:
			return 'C';
		case INTERFACE:
			return 'I';
		case ENUM:
			return 'E';
		case ENTITY:
			return 'E';
		case PROTOCOL:
			return 'P';
		case STRUCT:
			return 'S';
		}
		assert false;
		return '?';
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return headerLayout.getDimension(stringBounder);
	}

	final public void drawU(UGraphic ug) {
		throw new UnsupportedOperationException();
	}

	public void drawU(UGraphic ug, double width, double height) {
		headerLayout.drawU(ug, width, height);
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

}
