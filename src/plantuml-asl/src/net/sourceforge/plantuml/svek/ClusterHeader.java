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
 * Contribution :  Hisashi Miyashita
 * Contribution :  Serge Wenger
 */
package net.sourceforge.plantuml.svek;

import java.util.List;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.EntityImageLegend;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.DisplayPositioned;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.entity.EntityImpl;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;

public final class ClusterHeader {

	private int titleAndAttributeWidth = 0;
	private int titleAndAttributeHeight = 0;
	final private TextBlock title;
	final private TextBlock stereo;

	public ClusterHeader(EntityImpl g, ISkinParam skinParam, PortionShower portionShower, StringBounder stringBounder) {

		this.title = getTitleBlock(g, skinParam);
		this.stereo = getStereoBlock(g, skinParam, portionShower);
		final TextBlock stereoAndTitle = TextBlockUtils.mergeTB(stereo, title, HorizontalAlignment.CENTER);
		final XDimension2D dimLabel = stereoAndTitle.calculateDimension(stringBounder);
		if (dimLabel.getWidth() > 0) {
			final XDimension2D dimAttribute = ((EntityImpl) g).getStateHeader(skinParam)
					.calculateDimension(stringBounder);
			final double attributeHeight = dimAttribute.getHeight();
			final double attributeWidth = dimAttribute.getWidth();
			final double marginForFields = attributeHeight > 0 ? IEntityImage.MARGIN : 0;
			final USymbol uSymbol = g.getUSymbol();
			final int suppHeightBecauseOfShape = uSymbol == null ? 0 : uSymbol.suppHeightBecauseOfShape();
			final int suppWidthBecauseOfShape = uSymbol == null ? 0 : uSymbol.suppWidthBecauseOfShape();

			this.titleAndAttributeWidth = (int) Math.max(dimLabel.getWidth(), attributeWidth) + suppWidthBecauseOfShape;
			this.titleAndAttributeHeight = (int) (dimLabel.getHeight() + attributeHeight + marginForFields
					+ suppHeightBecauseOfShape);
		}

	}

	public final int getTitleAndAttributeWidth() {
		return titleAndAttributeWidth;
	}

	public final int getTitleAndAttributeHeight() {
		return titleAndAttributeHeight;
	}

	public final TextBlock getTitle() {
		return title;
	}

	public final TextBlock getStereo() {
		return stereo;
	}

	private TextBlock getTitleBlock(EntityImpl g, ISkinParam skinParam) {
		final Display label = g.getDisplay();
		if (label == null)
			return TextBlockUtils.empty(0, 0);

		final SName sname = skinParam.getUmlDiagramType().getStyleName();
		final StyleSignatureBasic signature;
		final USymbol uSymbol = g.getUSymbol();
		if (g.getGroupType() == GroupType.STATE)
			signature = StyleSignatureBasic.of(SName.root, SName.element, SName.stateDiagram, SName.state,
					SName.composite, SName.title);
		else if (uSymbol != null)
			signature = StyleSignatureBasic.of(SName.root, SName.element, sname, uSymbol.getSName(), SName.composite,
					SName.title);
		else
			signature = StyleSignatureBasic.of(SName.root, SName.element, sname, SName.composite, SName.title);

		final Style style = signature //
				.withTOBECHANGED(g.getStereotype()) //
				.with(g.getStereostyles()) //
				.getMergedStyle(skinParam.getCurrentStyleBuilder());

		final FontConfiguration fontConfiguration = style.getFontConfiguration(skinParam.getIHtmlColorSet(),
				g.getColors());

		final HorizontalAlignment alignment = HorizontalAlignment.CENTER;
		return label.create(fontConfiguration, alignment, skinParam);
	}

	private TextBlock getStereoBlock(EntityImpl g, ISkinParam skinParam, PortionShower portionShower) {
		final TextBlock stereo = getStereoBlockWithoutLegend(g, portionShower, skinParam);
		final DisplayPositioned legend = g.getLegend();
		if (legend == null || legend.isNull())
			return stereo;

		final TextBlock legendBlock = EntityImageLegend.create(legend.getDisplay(), skinParam);
		return DecorateEntityImage.add(legendBlock, stereo, legend.getHorizontalAlignment(),
				legend.getVerticalAlignment());
	}

	private TextBlock getStereoBlockWithoutLegend(EntityImpl g, PortionShower portionShower, ISkinParam skinParam) {
		final Stereotype stereotype = g.getStereotype();
		// final DisplayPositionned legend = g.getLegend();
		if (stereotype == null)
			return TextBlockUtils.empty(0, 0);

		final TextBlock tmp = stereotype.getSprite(skinParam);
		if (tmp != null)
			return tmp;

		final List<String> stereos = stereotype.getLabels(skinParam.guillemet());
		if (stereos == null)
			return TextBlockUtils.empty(0, 0);

		final boolean show = portionShower.showPortion(EntityPortion.STEREOTYPE, g);
		if (show == false)
			return TextBlockUtils.empty(0, 0);

		final Style style = Cluster
				.getDefaultStyleDefinition(skinParam.getUmlDiagramType().getStyleName(), g.getUSymbol())
				.forStereotypeItself(g.getStereotype()).getMergedStyle(skinParam.getCurrentStyleBuilder());

		final FontConfiguration fontConfiguration = style.getFontConfiguration(skinParam.getIHtmlColorSet());
		return Display.create(stereos).create(fontConfiguration, HorizontalAlignment.CENTER, skinParam);

	}

}
