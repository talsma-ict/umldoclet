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
package net.sourceforge.plantuml.mindmap;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Rankdir;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.style.NoStyleAvailableException;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class MindMap implements UDrawable {

	private final Branch regular = new Branch();
	private final Branch reverse = new Branch();

	private final ISkinParam skinParam;

	public MindMap(ISkinParam skinParam) {
		this.skinParam = skinParam;
	}

	private void computeFinger() {
		if (this.reverse.hasFinger() == false && this.regular.hasFinger() == false) {
			if (this.reverse.hasChildren())
				reverse.initFinger(skinParam, false);

			if (this.reverse.hasFinger() == false || this.regular.hasChildren())
				regular.initFinger(skinParam, true);

			if (this.reverse.hasFinger() && this.regular.hasFinger())
				this.reverse.doNotDrawFirstPhalanx();

		}
	}

	XDimension2D calculateDimension(StringBounder stringBounder) {
		this.computeFinger();
		final double y1 = this.regular.getHalfThickness(stringBounder);
		final double y2 = this.reverse.getHalfThickness(stringBounder);
		final double y = Math.max(y1, y2);

		final double width = this.reverse.getX12(stringBounder) + this.regular.getX12(stringBounder);
		final double height = y
				+ Math.max(this.reverse.getHalfThickness(stringBounder), this.regular.getHalfThickness(stringBounder));
		if (skinParam.getRankdir() == Rankdir.TOP_TO_BOTTOM)
			return new XDimension2D(height, width);
		else
			return new XDimension2D(width, height);

	}

	@Override
	public void drawU(UGraphic ug) {
		if (this.reverse.hasRoot() == false && this.regular.hasRoot() == false)
			return;

		this.computeFinger();

		final StringBounder stringBounder = ug.getStringBounder();
		final double y1 = this.regular.getHalfThickness(stringBounder);
		final double y2 = this.reverse.getHalfThickness(stringBounder);
		final double y = Math.max(y1, y2);

		final double x = this.reverse.getX12(stringBounder);
		if (skinParam.getRankdir() == Rankdir.TOP_TO_BOTTOM)
			ug = ug.apply(new UTranslate(y, x));
		else
			ug = ug.apply(new UTranslate(x, y));
		this.regular.drawU(ug);
		this.reverse.drawU(ug);
	}

	CommandExecutionResult addIdeaInternal(String stereotype, HColor backColor, int level, Display label,
			IdeaShape shape, boolean direction) {
		try {
			if (this.reverse.hasRoot() == false && this.regular.hasRoot() == false)
				level = 0;

			if (level == 0) {
				this.regular.initRoot(skinParam.getCurrentStyleBuilder(), backColor, label, shape, stereotype);
				this.reverse.initRoot(skinParam.getCurrentStyleBuilder(), backColor, label, shape, stereotype);
				return CommandExecutionResult.ok();
			}
			if (direction == false)
				return this.reverse.add(skinParam.getCurrentStyleBuilder(), backColor, level, label, shape, stereotype);

			return this.regular.add(skinParam.getCurrentStyleBuilder(), backColor, level, label, shape, stereotype);
		} catch (NoStyleAvailableException e) {
			// Logme.error(e);
			return CommandExecutionResult.error("General failure: no style available.");
		}
	}

	boolean isFull(int level) {
		return level == 0 && this.regular.hasRoot();
	}

}
