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
package net.sourceforge.plantuml.sequencediagram.teoz;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.sequencediagram.Delay;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class MutingLine {

	private final Rose skin;
	private final ISkinParam skinParam;
	private final boolean useContinueLineBecauseOfDelay;
	private final Map<Double, Double> delays = new TreeMap<Double, Double>();
	private final StyleBuilder styleBuilder;
	private final Participant participant;

	public MutingLine(Rose skin, ISkinParam skinParam, List<Event> events, Participant participant) {
		this.participant = participant;
		this.skin = skin;
		this.skinParam = skinParam;
		this.useContinueLineBecauseOfDelay = useContinueLineBecauseOfDelay(events);
		this.styleBuilder = skinParam.getCurrentStyleBuilder();
	}

	private boolean useContinueLineBecauseOfDelay(List<Event> events) {
		final String strategy = skinParam.getValue("lifelineStrategy");
		if ("nosolid".equalsIgnoreCase(strategy))
			return false;

		for (Event ev : events)
			if (ev instanceof Delay)
				return true;

		return false;
	}

	public void drawLine(UGraphic ug, Context2D context, double createY, double endY) {
		final ComponentType defaultLineType = useContinueLineBecauseOfDelay ? ComponentType.CONTINUE_LINE
				: ComponentType.PARTICIPANT_LINE;
		if (delays.size() > 0) {
			double y = createY;
			for (Map.Entry<Double, Double> ent : delays.entrySet()) {
				if (ent.getKey() >= createY) {
					drawInternal(ug, context, y, ent.getKey(), defaultLineType);
					drawInternal(ug, context, ent.getKey(), ent.getKey() + ent.getValue(), ComponentType.DELAY_LINE);
					y = ent.getKey() + ent.getValue();
				}
			}
			drawInternal(ug, context, y, endY, defaultLineType);
		} else {
			drawInternal(ug, context, createY, endY, defaultLineType);
		}
	}

	private void drawInternal(UGraphic ug, Context2D context, double y1, double y2,
			final ComponentType defaultLineType) {
		if (y2 == y1)
			return;

		if (y2 < y1)
			throw new IllegalArgumentException();

		final Style style = defaultLineType.getStyleSignature().getMergedStyle(styleBuilder);
		final Component comp = skin.createComponent(new Style[] { style }, defaultLineType, null, skinParam,
				participant.getDisplay(skinParam.forceSequenceParticipantUnderlined()));
		final XDimension2D dim = comp.getPreferredDimension(ug.getStringBounder());
		final Area area = Area.create(dim.getWidth(), y2 - y1);
		comp.drawU(ug.apply(UTranslate.dy(y1)), area, context);
	}

	public void delayOn(double y, double height) {
		delays.put(y, height);
	}

}
