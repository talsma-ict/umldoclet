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
package net.sourceforge.plantuml.nwdiag.legacy;

import java.awt.geom.Dimension2D;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.nwdiag.VerticalLine;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.utils.MathUtils;

public class LinkedElement {

	private final TextBlock box;
	private final NetworkLegacy network;
	private final NServerLegacy square;
	private final Map<NetworkLegacy, TextBlock> conns;
	private final List<NetworkLegacy> networks;

	public LinkedElement(NServerLegacy square, TextBlock box, Map<NetworkLegacy, TextBlock> conns, List<NetworkLegacy> networks) {
		this.networks = networks;
		this.box = box;
		this.network = square.getMainNetwork();
		this.square = square;
		this.conns = conns;
	}

	public boolean isLinkedTo(NetworkLegacy some) {
		return conns.containsKey(some);
	}

	private final double marginAd = 10;
	private final double marginBox = 15;

	public MinMax getMinMax(StringBounder stringBounder, double width, double height) {
		final double xMiddle = width / 2;
		final double yMiddle = height / 2;
		final Dimension2D dimBox = box.calculateDimension(stringBounder);

		final double x1 = xMiddle - dimBox.getWidth() / 2;
		final double y1 = yMiddle - dimBox.getHeight() / 2;
		final double x2 = xMiddle + dimBox.getWidth() / 2;
		final double y2 = yMiddle + dimBox.getHeight() / 2;
		return MinMax.getEmpty(false).addPoint(x1 - 5, y1 - 5).addPoint(x2 + 5, y2 + 5);
	}

	public void drawMe(UGraphic ug, double width, double height) {
		final double xMiddle = width / 2;
		final double yMiddle = height / 2;
		drawCenter(ug, box, xMiddle, yMiddle);
	}

	public void drawLinks(UGraphic ug, double xstart, double width, double height) {

		ug = ug.apply(UTranslate.dx(xstart));

		final double ynet1 = network.getY();
		final double yMiddle = height / 2;
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimBox = box.calculateDimension(stringBounder);

		final double alpha = yMiddle - dimBox.getHeight() / 2;

		final HColor color = ColorParam.activityBorder.getDefaultValue();
		ug = ug.apply(color);

		final double xMiddle = width / 2;

		final TreeSet<Double> skip = new TreeSet<>();

		for (NetworkLegacy n : networks) {
			if (xstart + xMiddle > n.getXmin() && xstart + xMiddle < n.getXmax())
				skip.add(n.getY());
		}

		if (square.hasItsOwnColumn()) {
			if (square.getMainNetwork().isVisible()) {
				new VerticalLine(ynet1 + GridTextBlockDecorated.NETWORK_THIN, ynet1 + alpha, skip)
						.drawU(ug.apply(UTranslate.dx(xMiddle)));
			} else {
				new VerticalLine(ynet1, ynet1 + alpha, Collections.<Double>emptySet())
						.drawU(ug.apply(UTranslate.dx(xMiddle)));
			}
		}
		drawCenter(ug, link1(), xMiddle, ynet1 + alpha / 2);

		final double seven = 7.0;
		double x = xMiddle - (conns.size() - 2) * seven / 2;
		boolean first = true;
		for (Entry<NetworkLegacy, TextBlock> ent : conns.entrySet()) {
			if (ent.getKey() == network) {
				continue;
			}
			final double ynet2 = ent.getKey().getY();
			new VerticalLine(ynet1 + yMiddle + dimBox.getHeight() / 2, ynet2, skip).drawU(ug.apply(UTranslate.dx(x)));
			final double xtext;
			if (first && conns.size() > 2) {
				xtext = x - ent.getValue().calculateDimension(stringBounder).getWidth() / 2;
			} else {
				xtext = x;
			}
			drawCenter(ug, ent.getValue(), xtext, ynet2 - alpha / 2);
			x += seven;
			first = false;

		}

	}

	private TextBlock link1() {
		return conns.get(network);
	}

	private TextBlock link2() {
		final int i = networks.indexOf(network);
		if (i == networks.size() - 1) {
			return null;
		}
		return conns.get(networks.get(i + 1));
	}

	private void drawCenter(UGraphic ug, TextBlock block, double x, double y) {
		if (block == null) {
			return;
		}
		final Dimension2D dim = block.calculateDimension(ug.getStringBounder());
		block.drawU(ug.apply(new UTranslate(x - dim.getWidth() / 2, y - dim.getHeight() / 2)));

	}

	public Dimension2D naturalDimension(StringBounder stringBounder) {
		final Dimension2D dim1 = link1() == null ? new Dimension2DDouble(0, 0)
				: link1().calculateDimension(stringBounder);
		final Dimension2D dimBox = box.calculateDimension(stringBounder);
		final Dimension2D dim2 = link2() == null ? new Dimension2DDouble(0, 0)
				: link2().calculateDimension(stringBounder);
		final double width = MathUtils.max(dim1.getWidth() + 2 * marginAd, dimBox.getWidth() + 2 * marginBox,
				dim2.getWidth() + 2 * marginAd);
		final double height = dim1.getHeight() + 2 * marginAd + dimBox.getHeight() + 2 * marginBox + dim2.getHeight()
				+ 2 * marginAd;
		return new Dimension2DDouble(width, height);
	}

	public final NetworkLegacy getNetwork() {
		return network;
	}

	public final NServerLegacy getElement() {
		return square;
	}

}
