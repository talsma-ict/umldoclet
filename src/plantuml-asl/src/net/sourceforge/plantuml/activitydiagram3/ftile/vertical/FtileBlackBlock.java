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
package net.sourceforge.plantuml.activitydiagram3.ftile.vertical;

import java.awt.geom.Dimension2D;
import java.util.Collections;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class FtileBlackBlock extends AbstractFtile {

	private final double labelMargin = 5;

	private double width;
	private double height;
	private TextBlock label = TextBlockUtils.empty(0, 0);
	private final HColor colorBar;
	private final Swimlane swimlane;

	public FtileBlackBlock(ISkinParam skinParam, HColor colorBar, Swimlane swimlane) {
		super(skinParam);
		this.colorBar = colorBar;
		this.swimlane = swimlane;
	}

	public void setBlackBlockDimension(double width, double height) {
		this.height = height;
		this.width = width;
	}

	public void setLabel(TextBlock label) {
		if (label == null) {
			throw new IllegalArgumentException();
		}
		this.label = label;
	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		double supp = label.calculateDimension(stringBounder).getWidth();
		if (supp > 0) {
			supp += labelMargin;
		}
		return new FtileGeometry(width + supp, height, width / 2, 0, height);
	}

	public void drawU(UGraphic ug) {
		final URectangle rect = new URectangle(width, height).rounded(5).ignoreForCompressionOnX();
		if (skinParam().shadowing(null)) {
			rect.setDeltaShadow(3);
		}
		ug.apply(colorBar).apply(colorBar.bg()).draw(rect);
		final Dimension2D dimLabel = label.calculateDimension(ug.getStringBounder());
		label.drawU(ug.apply(new UTranslate(width + labelMargin, -dimLabel.getHeight() / 2)));
	}

	public Set<Swimlane> getSwimlanes() {
		return Collections.singleton(swimlane);
	}

	public Swimlane getSwimlaneIn() {
		return swimlane;
	}

	public Swimlane getSwimlaneOut() {
		return swimlane;
	}

}
