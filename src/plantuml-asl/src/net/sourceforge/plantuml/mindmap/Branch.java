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
package net.sourceforge.plantuml.mindmap;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.color.HColor;

class Branch implements UDrawable {
	private Idea root;
	private Idea last;
	private Finger finger;

	void initRoot(StyleBuilder styleBuilder, HColor backColor, Display label, IdeaShape shape, String stereotype) {
		root = new Idea(styleBuilder, backColor, label, shape, stereotype);
		last = root;
	}

	void initFinger(ISkinParam skinParam, Direction direction) {
		finger = FingerImpl.build(root, skinParam, direction);
	}

	Idea getParentOfLast(int nb) {
		Idea result = last;
		for (int i = 0; i < nb; i++) {
			result = result.getParent();
		}
		return result;
	}

	CommandExecutionResult add(StyleBuilder styleBuilder, HColor backColor, int level, Display label, IdeaShape shape,
			String stereotype) {
		if (last == null)
			return CommandExecutionResult.error("Check your indentation ?");

		if (level == last.getLevel() + 1) {
			final Idea newIdea = last.createIdea(styleBuilder, backColor, level, label, shape, stereotype);
			last = newIdea;
			return CommandExecutionResult.ok();
		}
		if (level <= last.getLevel()) {
			final int diff = last.getLevel() - level + 1;
			final Idea newIdea = getParentOfLast(diff).createIdea(styleBuilder, backColor, level, label, shape,
					stereotype);
			last = newIdea;
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("error42L");
	}

	public boolean hasFinger() {
		return finger != null;
	}

	public void drawU(UGraphic ug) {
		if (finger != null)
			finger.drawU(ug);
	}

	public double getHalfThickness(StringBounder stringBounder) {
		if (finger == null)
			return 0;
		return finger.getFullThickness(stringBounder) / 2;
	}

	public double getFullElongation(StringBounder stringBounder) {
		if (finger == null)
			return 0;
		return finger.getFullElongation(stringBounder);
	}

	public boolean hasChildren() {
		return root.hasChildren();
	}

	public boolean hasRoot() {
		return root != null;
	}

	public void doNotDrawFirstPhalanx() {
		finger.doNotDrawFirstPhalanx();
	}

	public double getX12(StringBounder stringBounder) {
		if (finger == null)
			return 0;
		return finger.getFullElongation(stringBounder) + ((FingerImpl) finger).getX12();
	}

}
