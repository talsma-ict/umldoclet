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
package net.sourceforge.plantuml.creole;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicStencil;
import net.sourceforge.plantuml.ugraphic.UStroke;

public class SheetBlock2 extends AbstractTextBlock implements TextBlock, Atom {

	public List<Atom> splitInTwo(StringBounder stringBounder, double width) {
		throw new UnsupportedOperationException(getClass().toString());
	}

	private final SheetBlock1 block;
	private final UStroke defaultStroke;
	private final Stencil stencil;

	public SheetBlock2 enlargeMe(final double delta1, final double delta2) {
		final Stencil newStencil = new Stencil() {

			public double getStartingX(StringBounder stringBounder, double y) {
				return stencil.getStartingX(stringBounder, y) - delta1;
			}

			public double getEndingX(StringBounder stringBounder, double y) {
				return stencil.getEndingX(stringBounder, y) + delta2;
			}
		};
		return new SheetBlock2(block, newStencil, defaultStroke);
	}

	public SheetBlock2(SheetBlock1 block, Stencil stencil, UStroke defaultStroke) {
		this.block = block;
		this.stencil = Objects.requireNonNull(stencil);
		this.defaultStroke = defaultStroke;
	}

	@Override
	public String toString() {
		return block.toString();
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return block.calculateDimension(stringBounder);
	}

	public void drawU(UGraphic ug) {
		if (stencil != null) {
			ug = UGraphicStencil.create(ug, stencil, defaultStroke);
		}
		block.drawU(ug);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return 0;
	}

	@Override
	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		return block.getInnerPosition(member, stringBounder, strategy);
	}

}
