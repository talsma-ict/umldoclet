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

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class DirectionalTextBlock extends AbstractTextBlock implements TextBlock {
	private final TextBlock right;
	private final TextBlock left;
	private final TextBlock up;
	private final TextBlock down;
	private final GuideLine guideline;

	public DirectionalTextBlock(GuideLine guideline, TextBlock right, TextBlock left, TextBlock up, TextBlock down) {
		this.right = right;
		this.left = left;
		this.up = up;
		this.down = down;
		this.guideline = guideline;
	}

	public void drawU(UGraphic ug) {
		Direction dir = guideline.getArrowDirection();
		switch (dir) {
		case RIGHT:
			right.drawU(ug);
			break;
		case LEFT:
			left.drawU(ug);
			break;
		case UP:
			up.drawU(ug);
			break;
		case DOWN:
			down.drawU(ug);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return right.calculateDimension(stringBounder);
	}

}
