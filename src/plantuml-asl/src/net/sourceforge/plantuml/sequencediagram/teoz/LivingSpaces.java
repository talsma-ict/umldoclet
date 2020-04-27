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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class LivingSpaces {

	private final Map<Participant, LivingSpace> all = new LinkedHashMap<Participant, LivingSpace>();

	public Collection<LivingSpace> values() {
		return all.values();
	}

	public void addConstraints(StringBounder stringBounder) {
		LivingSpace previous = null;
		for (LivingSpace current : all.values()) {
			if (previous != null) {
				final Real point1 = previous.getPosD(stringBounder);
				final Real point2 = current.getPosB();
				point2.ensureBiggerThan(point1.addFixed(10));
			}
			previous = current;
		}
	}

	public LivingSpace previous(LivingSpace element) {
		LivingSpace previous = null;
		for (LivingSpace current : all.values()) {
			if (current == element) {
				return previous;
			}
			previous = current;
		}
		return null;
	}

	public LivingSpace next(LivingSpace element) {
		for (Iterator<LivingSpace> it = all.values().iterator(); it.hasNext();) {
			final LivingSpace current = it.next();
			if (current == element && it.hasNext()) {
				return it.next();
			}
		}
		return null;

	}

	public Collection<Participant> participants() {
		return all.keySet();
	}

	public void put(Participant participant, LivingSpace livingSpace) {
		all.put(participant, livingSpace);
	}

	public LivingSpace get(Participant participant) {
		return all.get(participant);
	}

	public void drawHeads(final UGraphic ug, Context2D context, VerticalAlignment verticalAlignment) {
		final StringBounder stringBounder = ug.getStringBounder();
		final double headHeight = getHeadHeight(stringBounder);
		for (LivingSpace livingSpace : values()) {
			final double x = livingSpace.getPosB().getCurrentValue();
			double y = 0;
			if (verticalAlignment == VerticalAlignment.BOTTOM) {
				final Dimension2D dimHead = livingSpace.getHeadPreferredDimension(stringBounder);
				y = headHeight - dimHead.getHeight();
			}
			livingSpace.drawHead(ug.apply(new UTranslate(x, y)), context, verticalAlignment, HorizontalAlignment.LEFT);
		}
	}

	public double getHeadHeight(StringBounder stringBounder) {
		double headHeight = 0;
		for (LivingSpace livingSpace : values()) {
			final Dimension2D headDim = livingSpace.getHeadPreferredDimension(stringBounder);
			headHeight = Math.max(headHeight, headDim.getHeight());
		}
		return headHeight;
	}

	public void drawLifeLines(final UGraphic ug, double height, Context2D context) {
		int i = 0;
		for (LivingSpace livingSpace : values()) {
			// if (i++ == 0) {
			// System.err.println("TEMPORARY SKIPPING OTHERS");
			// continue;
			// }
			// System.err.println("drawing lines " + livingSpace);
			final double x = livingSpace.getPosC(ug.getStringBounder()).getCurrentValue();
			livingSpace.drawLineAndLiveBoxes(ug.apply(UTranslate.dx(x)), height, context);
		}
	}

	public void delayOn(double y, double height) {
		for (LivingSpace livingSpace : values()) {
			livingSpace.delayOn(y, height);
		}
	}

	public int size() {
		return all.size();
	}

}
