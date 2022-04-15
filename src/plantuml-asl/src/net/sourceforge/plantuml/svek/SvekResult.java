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
package net.sourceforge.plantuml.svek;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.dot.DotData;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UHidden;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public final class SvekResult extends AbstractTextBlock implements IEntityImage {

	private final Rose rose = new Rose();

	private final DotData dotData;
	private final DotStringFactory dotStringFactory;

	public SvekResult(DotData dotData, DotStringFactory dotStringFactory) {
		this.dotData = dotData;
		this.dotStringFactory = dotStringFactory;
	}

	public void drawU(UGraphic ug) {

		for (Cluster cluster : dotStringFactory.getBibliotekon().allCluster())
			cluster.drawU(ug, dotData.getUmlDiagramType(), dotData.getSkinParam());

		HColor color = rose.getHtmlColor(dotData.getSkinParam(), null, getArrowColorParam());
		if (UseStyle.useBetaStyle()) {
			final Style style = getDefaultStyleDefinition(null)
					.getMergedStyle(dotData.getSkinParam().getCurrentStyleBuilder());
			color = style.value(PName.LineColor).asColor(dotData.getSkinParam().getThemeStyle(),
					dotData.getSkinParam().getIHtmlColorSet());
		}

		color = HColorUtils.noGradient(color);
		UStroke stroke = null;

		for (SvekNode node : dotStringFactory.getBibliotekon().allNodes()) {
			final double minX = node.getMinX();
			final double minY = node.getMinY();
			final UGraphic ug2 = node.isHidden() ? ug.apply(UHidden.HIDDEN) : ug;
			final IEntityImage image = node.getImage();
			image.drawU(ug2.apply(new UTranslate(minX, minY)));
			if (image instanceof Untranslated)
				((Untranslated) image).drawUntranslated(ug.apply(color), minX, minY);

		}

		final Set<String> ids = new HashSet<>();

		for (SvekLine line : dotStringFactory.getBibliotekon().allLines()) {
			final UGraphic ug2 = line.isHidden() ? ug.apply(UHidden.HIDDEN) : ug;

			if (UseStyle.useBetaStyle()) {
				final StyleBuilder currentStyleBuilder = line.getCurrentStyleBuilder();
				final Style style = getDefaultStyleDefinition(line.getStereotype()).getMergedStyle(currentStyleBuilder);
				color = style.value(PName.LineColor).asColor(dotData.getSkinParam().getThemeStyle(),
						dotData.getSkinParam().getIHtmlColorSet());
				stroke = style.getStroke();
				color = HColorUtils.noGradient(color);
			}

			line.drawU(ug2, stroke, color, ids);
		}

	}

	private ColorParam getArrowColorParam() {
		if (dotData.getUmlDiagramType() == UmlDiagramType.CLASS)
			return ColorParam.arrow;
		else if (dotData.getUmlDiagramType() == UmlDiagramType.OBJECT)
			return ColorParam.arrow;
		else if (dotData.getUmlDiagramType() == UmlDiagramType.DESCRIPTION)
			return ColorParam.arrow;
		else if (dotData.getUmlDiagramType() == UmlDiagramType.ACTIVITY)
			return ColorParam.arrow;
		else if (dotData.getUmlDiagramType() == UmlDiagramType.STATE)
			return ColorParam.arrow;

		throw new IllegalStateException();
	}

	private StyleSignature getDefaultStyleDefinition(Stereotype stereotype) {
		StyleSignature result = StyleSignatureBasic.of(SName.root, SName.element, dotData.getUmlDiagramType().getStyleName(),
				SName.arrow);
		if (stereotype != null)
			result = result.withTOBECHANGED(stereotype);

		return result;
	}

	// Duplicate SvekResult / GeneralImageBuilder
	public HColor getBackcolor() {
		if (UseStyle.useBetaStyle()) {
			final Style style = StyleSignatureBasic.of(SName.root, SName.document)
					.getMergedStyle(dotData.getSkinParam().getCurrentStyleBuilder());
			return style.value(PName.BackGroundColor).asColor(dotData.getSkinParam().getThemeStyle(),
					dotData.getSkinParam().getIHtmlColorSet());
		}
		return dotData.getSkinParam().getBackgroundColor();
	}

	private MinMax minMax;

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		if (minMax == null) {
			minMax = TextBlockUtils.getMinMax(this, stringBounder, false);
			dotStringFactory.moveSvek(6 - minMax.getMinX(), 6 - minMax.getMinY());
		}
		return Dimension2DDouble.delta(minMax.getDimension(), 0, 12);
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public Margins getShield(StringBounder stringBounder) {
		return Margins.NONE;
	}

	public boolean isHidden() {
		return false;
	}

	public double getOverscanX(StringBounder stringBounder) {
		return 0;
	}

}
