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
package net.sourceforge.plantuml.nwdiag.next;

import java.util.List;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.nwdiag.core.Network;
import net.sourceforge.plantuml.nwdiag.core.NwGroup;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class GridTextBlockDecorated extends GridTextBlockSimple {

	public static final int NETWORK_THIN = 5;

	private final List<NwGroup> groups;
	private final List<Network> networks;

	public GridTextBlockDecorated(int lines, int cols, List<NwGroup> groups, List<Network> networks,
			ISkinParam skinparam) {
		super(lines, cols, skinparam);
		this.groups = groups;
		this.networks = networks;
	}

	@Override
	protected void drawGrid(UGraphic ug) {
		for (NwGroup group : groups) {
			drawGroups(ug, group, getSkinParam());
		}
		drawNetworkTube(ug);
		drawLinks(ug, getSkinParam().getCurrentStyleBuilder());
	}

	private void drawLinks(UGraphic ug, StyleBuilder styleBuilder) {

		final Style style = getStyleDefinitionNetwork(SName.arrow).getMergedStyle(styleBuilder);
		final HColor lineColor = style.value(PName.LineColor).asColor(getSkinParam().getThemeStyle(),
				getSkinParam().getIHtmlColorSet());

		ug = ug.apply(lineColor);

		final StringBounder stringBounder = ug.getStringBounder();
		for (int i = 0; i < data.getNbLines(); i++) {
			final double lineHeight = lineHeight(stringBounder, i);
			double x = 0;
			for (int j = 0; j < data.getNbCols(); j++) {
				final double colWidth = colWidth(stringBounder, j);
				if (data.get(i, j) != null) {
					data.get(i, j).drawLinks(ug, x, colWidth, lineHeight);
				}
				x += colWidth;
			}
		}

	}

	private void drawGroups(UGraphic ug, NwGroup group, ISkinParam skinParam) {
		final StringBounder stringBounder = ug.getStringBounder();

		MinMax size = null;
		double y = 0;
		for (int i = 0; i < data.getNbLines(); i++) {
			final double lineHeight = lineHeight(stringBounder, i);
			double x = 0;
			for (int j = 0; j < data.getNbCols(); j++) {
				final double colWidth = colWidth(stringBounder, j);
				final LinkedElement element = data.get(i, j);
				if (element != null && group.contains(element.getServer())) {
					final MinMax minMax = element.getMinMax(stringBounder, colWidth, lineHeight)
							.translate(new UTranslate(x, y));
					size = size == null ? minMax : size.addMinMax(minMax);
				}
				x += colWidth;
			}
			y += lineHeight;
		}
		if (size != null) {
			group.drawGroup(ug, size, skinParam);
		}

	}

	private boolean isThereALink(int j, Network network) {
		for (int i = 0; i < data.getNbLines(); i++) {
			final LinkedElement element = data.get(i, j);
			if (element != null && element.isLinkedTo(network)) {
				return true;
			}
		}
		return false;
	}

	private StyleSignatureBasic getStyleDefinitionNetwork(SName sname) {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.nwdiagDiagram, sname);
	}

	private void drawNetworkTube(UGraphic ug) {

		final StringBounder stringBounder = ug.getStringBounder();
		double y = 0;
		for (int i = 0; i < data.getNbLines(); i++) {
			final Network network = getNetwork(i);
			computeMixMax(data.getLine(i), stringBounder, network);

			final URectangle rect = new URectangle(network.getXmax() - network.getXmin(), NETWORK_THIN);

			UGraphic ug2 = ug.apply(new UTranslate(network.getXmin(), y));
			final StyleBuilder styleBuilder = getSkinParam().getCurrentStyleBuilder();
			final Style style = getStyleDefinitionNetwork(SName.network).getMergedStyle(styleBuilder);
			final double deltaShadow = style.value(PName.Shadowing).asDouble();
			ug2 = ug2.apply(style.value(PName.LineColor).asColor(getSkinParam().getThemeStyle(),
					getSkinParam().getIHtmlColorSet()));
			ug2 = ug2.apply(style.value(PName.BackGroundColor)
					.asColor(getSkinParam().getThemeStyle(), getSkinParam().getIHtmlColorSet()).bg());

			rect.setDeltaShadow(deltaShadow);
			if (network != null && network.getColor() != null) {
				ug2 = ug2.apply(network.getColor().bg());
			}
			if (network != null) {
				network.setY(y);
			}
			if (network.isVisible()) {
				ug2.draw(rect);
			}
			y += lineHeight(stringBounder, i);
		}
	}

	private void computeMixMax(LinkedElement line[], StringBounder stringBounder, Network network) {
		double x = 0;
		double xmin = network.isFullWidth() ? 0 : -1;
		double xmax = 0;
		for (int j = 0; j < line.length; j++) {
			final boolean hline = isThereALink(j, network);
			if (hline && xmin < 0) {
				xmin = x;
			}
			x += colWidth(stringBounder, j);
			if (hline || network.isFullWidth()) {
				xmax = x;
			}
		}
		network.setMinMax(xmin, xmax);

	}

	private Network getNetwork(int i) {
		return networks.get(i);
	}

}
