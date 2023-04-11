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
package net.sourceforge.plantuml.svek;

import net.atmp.InnerStrategy;
import net.sourceforge.plantuml.dot.Neighborhood;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XRectangle2D;
import net.sourceforge.plantuml.klimt.shape.AbstractTextBlock;

public class EntityImageProtected extends AbstractTextBlock implements IEntityImage, Untranslated, WithPorts {

	private final IEntityImage orig;
	private final double border;
	private final Bibliotekon bibliotekon;
	private final Neighborhood neighborhood;

	public XRectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		final XRectangle2D result = orig.getInnerPosition(member, stringBounder, strategy);
		return new XRectangle2D(result.getMinX() + border, result.getMinY() + border, result.getWidth(),
				result.getHeight());
	}

	public EntityImageProtected(IEntityImage orig, double border, Neighborhood neighborhood, Bibliotekon bibliotekon) {
		this.orig = orig;
		this.border = border;
		this.bibliotekon = bibliotekon;
		this.neighborhood = neighborhood;
	}

	public boolean isHidden() {
		return orig.isHidden();
	}

	public HColor getBackcolor() {
		return orig.getBackcolor();
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return orig.calculateDimension(stringBounder).delta(2 * border);
	}

	public void drawU(UGraphic ug) {
		orig.drawU(ug.apply(new UTranslate(border, border)));
	}

	public void drawUntranslated(UGraphic ug, double minX, double minY) {
		final XDimension2D dim = orig.calculateDimension(ug.getStringBounder());
		neighborhood.drawU(ug, minX + border, minY + border, bibliotekon, dim);
	}

	public ShapeType getShapeType() {
		return orig.getShapeType();
	}

	public Margins getShield(StringBounder stringBounder) {
		return orig.getShield(stringBounder);
	}

	public double getOverscanX(StringBounder stringBounder) {
		return orig.getOverscanX(stringBounder);
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		return ((WithPorts) orig).getPorts(stringBounder);
	}

}
