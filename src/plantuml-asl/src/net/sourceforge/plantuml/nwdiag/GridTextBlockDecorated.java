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
package net.sourceforge.plantuml.nwdiag;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public class GridTextBlockDecorated extends GridTextBlockSimple {

	public static final HColorSet colors = HColorSet.instance();

	public static final int NETWORK_THIN = 5;

	private final Collection<DiagGroup> groups;
	private final List<Network> networks;

	public GridTextBlockDecorated(int lines, int cols, Collection<DiagGroup> groups, List<Network> networks) {
		super(lines, cols);
		this.groups = groups;
		this.networks = networks;
	}

	@Override
	protected void drawGrid(UGraphic ug) {
		for (DiagGroup group : groups) {
			drawGroups(ug, group);
		}
		final Map<Network, Double> pos = drawNetworkTube(ug);
		drawLinks(ug, pos);
	}

	private void drawLinks(UGraphic ug, Map<Network, Double> pos) {
		final StringBounder stringBounder = ug.getStringBounder();
		for (int i = 0; i < data.length; i++) {
			final double lineHeight = lineHeight(stringBounder, i);
			double x = 0;
			for (int j = 0; j < data[i].length; j++) {
				final double colWidth = colWidth(stringBounder, j);
				if (data[i][j] != null) {
					data[i][j].drawLinks(ug.apply(UTranslate.dx(x)), colWidth, lineHeight, pos);
				}
				x += colWidth;
			}
		}

	}

	private void drawGroups(UGraphic ug, DiagGroup group) {
		final StringBounder stringBounder = ug.getStringBounder();

		MinMax size = null;
		double y = 0;
		for (int i = 0; i < data.length; i++) {
			final double lineHeight = lineHeight(stringBounder, i);
			double x = 0;
			for (int j = 0; j < data[i].length; j++) {
				final double colWidth = colWidth(stringBounder, j);
				final LinkedElement element = data[i][j];
				if (element != null && group.matches(element)) {
					final MinMax minMax = element.getMinMax(stringBounder, colWidth, lineHeight)
							.translate(new UTranslate(x, y));
					size = size == null ? minMax : size.addMinMax(minMax);
				}
				x += colWidth;
			}
			y += lineHeight;
		}
		if (size != null) {
			HColor color = group.getColor();
			if (color == null) {
				color = colors.getColorIfValid("#AAA");
			}
			size.draw(ug, color);
		}

	}

	private boolean isThereALink(int j, Network network) {
		for (int i = 0; i < data.length; i++) {
			final LinkedElement element = data[i][j];
			if (element != null && element.isLinkedTo(network)) {
				return true;
			}
		}
		return false;
	}

	private Map<Network, Double> drawNetworkTube(final UGraphic ug) {
		final Map<Network, Double> pos = new HashMap<Network, Double>();
		final StringBounder stringBounder = ug.getStringBounder();
		double y = 0;
		for (int i = 0; i < data.length; i++) {
			final Network network = getNetwork(i);
			double x = 0;
			double xmin = -1;
			double xmax = 0;
			for (int j = 0; j < data[i].length; j++) {
				final boolean hline = isThereALink(j, network);
				if (hline && xmin < 0) {
					xmin = x;
				}
				x += colWidth(stringBounder, j);
				if (hline) {
					xmax = x;
				}
			}
			final URectangle rect = new URectangle(xmax - xmin, NETWORK_THIN);
			rect.setDeltaShadow(1.0);
			UGraphic ug2 = ug.apply(new UTranslate(xmin, y));
			if (network != null && network.getColor() != null) {
				ug2 = ug2.apply(network.getColor().bg());
			}
			if (network != null) {
				pos.put(network, y);
			}
			ug2.draw(rect);
			y += lineHeight(stringBounder, i);
		}
		return pos;
	}

	private Network getNetwork(int i) {
		return networks.get(i);
	}
}
