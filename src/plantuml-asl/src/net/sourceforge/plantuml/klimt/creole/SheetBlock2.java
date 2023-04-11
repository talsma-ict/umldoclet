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
package net.sourceforge.plantuml.klimt.creole;

import java.util.List;
import java.util.Objects;

import net.atmp.InnerStrategy;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.creole.atom.Atom;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.drawing.UGraphicStencil;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XRectangle2D;
import net.sourceforge.plantuml.klimt.shape.AbstractTextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.WithPorts;

final public class SheetBlock2 extends AbstractTextBlock implements TextBlock, Atom, WithPorts {

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

	private HorizontalAlignment getHorizontalAlignment() {
		return block.getHorizontalAlignment();
	}

	@Override
	public String toString() {
		return block.toString();
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return block.calculateDimension(stringBounder);
	}

	public void drawU(UGraphic ug) {
		if (stencil != null)
			ug = UGraphicStencil.create(ug, stencil, defaultStroke);

		if (getHorizontalAlignment() == HorizontalAlignment.CENTER && block.getMinimumWidth() > 0) {
			final double width = calculateDimension(ug.getStringBounder()).getWidth();
			final double dx = (block.getMinimumWidth() - width) / 2;
			ug = ug.apply(UTranslate.dx(dx));
		}
		block.drawU(ug);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return 0;
	}

	@Override
	public XRectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		return block.getInnerPosition(member, stringBounder, strategy);
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		return new Ports();
	}

	@Override
	public List<Neutron> getNeutrons() {
		throw new UnsupportedOperationException();
	}

}
