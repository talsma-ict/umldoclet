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
package net.sourceforge.plantuml.sequencediagram.teoz;

import java.awt.geom.Dimension2D;
import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.SimpleContext2D;
import net.sourceforge.plantuml.ugraphic.UClip;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class PlayingSpaceWithParticipants extends AbstractTextBlock implements TextBlock {

	private final PlayingSpace playingSpace;
	private Dimension2D cacheDimension;
	private double ymin;
	private double ymax;

	public PlayingSpaceWithParticipants(PlayingSpace playingSpace) {
		this.playingSpace = Objects.requireNonNull(playingSpace);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		if (cacheDimension == null) {
			final double width = playingSpace.getMaxX(stringBounder).getCurrentValue()
					- playingSpace.getMinX(stringBounder).getCurrentValue();

			final int factor = playingSpace.isShowFootbox() ? 2 : 1;
			final double height = playingSpace.getPreferredHeight(stringBounder)
					+ factor * playingSpace.getLivingSpaces().getHeadHeight(stringBounder);

			cacheDimension = new Dimension2DDouble(width, height);
		}
		return cacheDimension;
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();

		final Context2D context = new SimpleContext2D(false);
		final double height = playingSpace.getPreferredHeight(stringBounder);
		final LivingSpaces livingSpaces = playingSpace.getLivingSpaces();

		final double headHeight = livingSpaces.getHeadHeight(stringBounder);

		if (ymax == 0) {
			playingSpace.drawBackground(ug.apply(UTranslate.dy(headHeight)));
		} else {
			final UClip clip = new UClip(-1000, ymin, Double.MAX_VALUE, ymax - ymin + 1);
			playingSpace.drawBackground(ug.apply(UTranslate.dy(headHeight)).apply(clip));
		}

		livingSpaces.drawLifeLines(ug.apply(UTranslate.dy(headHeight)), height, context);

		livingSpaces.drawHeads(ug, context, VerticalAlignment.BOTTOM);
		if (playingSpace.isShowFootbox()) {
			livingSpaces.drawHeads(ug.apply(UTranslate.dy(height + headHeight)), context, VerticalAlignment.TOP);
		}
		if (ymax == 0) {
			playingSpace.drawForeground(ug.apply(UTranslate.dy(headHeight)));
		} else {
			final UClip clip = new UClip(-1000, ymin, Double.MAX_VALUE, ymax - ymin + 1);
			// playingSpace.drawForeground(new
			// UGraphicNewpages(ug.apply(UTranslate.dy(headHeight)), ymin, ymax));
			playingSpace.drawForeground(ug.apply(UTranslate.dy(headHeight)).apply(clip));
		}
		// drawNewPages(ug.apply(UTranslate.dy(headHeight)));
	}

	public Real getMinX(StringBounder stringBounder) {
		return playingSpace.getMinX(stringBounder);
	}

	public int getNbPages() {
		return playingSpace.getNbPages();
	}

	public void setIndex(int index) {
		final List<Double> yNewPages = playingSpace.yNewPages();
		this.ymin = yNewPages.get(index);
		this.ymax = yNewPages.get(index + 1);
	}

	private List<Double> yNewPages() {
		return playingSpace.yNewPages();
	}

	private void drawNewPages(UGraphic ug) {
		ug = ug.apply(HColorUtils.BLUE);
		for (Double change : yNewPages()) {
			if (change == 0 || change == Double.MAX_VALUE) {
				continue;
			}
			ug.apply(UTranslate.dy(change)).draw(ULine.hline(100));
		}
	}

}
