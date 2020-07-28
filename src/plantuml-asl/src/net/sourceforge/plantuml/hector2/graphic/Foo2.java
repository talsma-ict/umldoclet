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
package net.sourceforge.plantuml.hector2.graphic;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.cucadiagram.CucaDiagram;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.hector2.MinMax;
import net.sourceforge.plantuml.hector2.layering.Layer;
import net.sourceforge.plantuml.hector2.mpos.Distribution;
import net.sourceforge.plantuml.svek.GeneralImageBuilder;
import net.sourceforge.plantuml.svek.IEntityImage;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Foo2 extends AbstractTextBlock implements TextBlock {

	private final Distribution distribution;
	private final CucaDiagram diagram;

	public Foo2(Distribution distribution, CucaDiagram diagram) {
		this.distribution = distribution;
		this.diagram = diagram;
	}

	public Dimension2D getMaxCellDimension(StringBounder stringBounder) {
		Dimension2D result = new Dimension2DDouble(0, 0);
		for (Layer layer : distribution.getLayers()) {
			final Dimension2D dim = Foo1.getMaxCellDimension(stringBounder, layer, diagram);
			result = Dimension2DDouble.max(result, dim);
		}
		return result;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D cell = getMaxCellDimension(stringBounder);
		final MinMax longitudes = distribution.getMinMaxLongitudes();
		final double width = (longitudes.getDiff() + 2) * cell.getWidth() / 2;
		final double height = cell.getHeight() * distribution.getNbLayers();
		return new Dimension2DDouble(width, height);
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D cell = getMaxCellDimension(stringBounder);
		for (Layer layer : distribution.getLayers()) {
			drawLayer(ug, layer, cell.getWidth(), cell.getHeight());
			ug = ug.apply(UTranslate.dy(cell.getHeight()));
		}
	}

	private void drawLayer(UGraphic ug, Layer layer, double w, double h) {
		for (IEntity ent : layer.entities()) {
			final IEntityImage image = computeImage((ILeaf) ent);
			final int longitude = layer.getLongitude(ent);
			final Dimension2D dimImage = image.calculateDimension(ug.getStringBounder());
			final double diffx = w - dimImage.getWidth();
			final double diffy = h - dimImage.getHeight();
			image.drawU(ug.apply(new UTranslate(w * longitude / 2 + diffx / 2, diffy / 2)));
		}
	}

	private IEntityImage computeImage(final ILeaf leaf) {
		final IEntityImage image = GeneralImageBuilder.createEntityImageBlock(leaf, diagram.getSkinParam(),
				false, diagram, null, null, null, diagram.getLinks());
		return image;
	}

}
