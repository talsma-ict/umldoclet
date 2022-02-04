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

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.Delay;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.ParticipantEnglober;
import net.sourceforge.plantuml.sequencediagram.ParticipantType;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class LivingSpace {

	private final Participant p;
	private final ISkinParam skinParam;
	private final ComponentType headType;
	private final ComponentType tailType;
	private final MutingLine mutingLine;
	private final Rose rose = new Rose();
	private final LiveBoxes liveboxes;

	// private final Rose skin;
	// private final boolean useContinueLineBecauseOfDelay;
	// private final LivingSpaceImpl previous;
	// private LivingSpace next;

	private final Real posB;
	private Real posC;
	private Real posD;

	private boolean create = false;
	private double createY = 0;

	private final ParticipantEnglober englober;

	public int getLevelAt(Tile tile, EventsHistoryMode mode) {
		// assert mode == EventsHistoryMode.IGNORE_FUTURE_DEACTIVATE;
		return liveboxes.getLevelAt(tile.getEvent(), mode);
	}

	public void addStepForLivebox(Event event, double y) {
		liveboxes.addStep(event, y);
	}

	@Override
	public String toString() {
		return p.getCode() + " B=" + posB.getCurrentValue() + "/C=" + currentValue(posC) + "/D=" + currentValue(posD);
	}

	private static String currentValue(Real pos) {
		if (pos == null) {
			return null;
		}
		return "" + pos.getCurrentValue();
	}

	public LivingSpace(Participant p, ParticipantEnglober englober, Rose skin, ISkinParam skinParam, Real position,
			List<Event> events) {
		this.p = p;
		// this.skin = skin;
		this.skinParam = skinParam;
		this.englober = englober;
		this.posB = position;
		if (p.getType() == ParticipantType.PARTICIPANT) {
			headType = ComponentType.PARTICIPANT_HEAD;
			tailType = ComponentType.PARTICIPANT_TAIL;
		} else if (p.getType() == ParticipantType.ACTOR) {
			headType = ComponentType.ACTOR_HEAD;
			tailType = ComponentType.ACTOR_TAIL;
		} else if (p.getType() == ParticipantType.BOUNDARY) {
			headType = ComponentType.BOUNDARY_HEAD;
			tailType = ComponentType.BOUNDARY_TAIL;
		} else if (p.getType() == ParticipantType.CONTROL) {
			headType = ComponentType.CONTROL_HEAD;
			tailType = ComponentType.CONTROL_TAIL;
		} else if (p.getType() == ParticipantType.ENTITY) {
			headType = ComponentType.ENTITY_HEAD;
			tailType = ComponentType.ENTITY_TAIL;
		} else if (p.getType() == ParticipantType.QUEUE) {
			headType = ComponentType.QUEUE_HEAD;
			tailType = ComponentType.QUEUE_TAIL;
		} else if (p.getType() == ParticipantType.DATABASE) {
			headType = ComponentType.DATABASE_HEAD;
			tailType = ComponentType.DATABASE_TAIL;
		} else if (p.getType() == ParticipantType.COLLECTIONS) {
			headType = ComponentType.COLLECTIONS_HEAD;
			tailType = ComponentType.COLLECTIONS_TAIL;
		} else {
			throw new IllegalArgumentException();
		}
		// this.stairs2.addStep2(0, p.getInitialLife());
		// this.stairs2.addStep2(0, 0);
		// this.useContinueLineBecauseOfDelay = useContinueLineBecauseOfDelay(events);
		this.mutingLine = new MutingLine(skin, skinParam, events, p);
		this.liveboxes = new LiveBoxes(p, events, skin, skinParam);
	}

	private boolean useContinueLineBecauseOfDelay(List<Event> events) {
		final String strategy = skinParam.getValue("lifelineStrategy");
		if ("nosolid".equalsIgnoreCase(strategy)) {
			return false;
		}
		for (Event ev : events) {
			if (ev instanceof Delay) {
				return true;
			}
		}
		return false;
	}

	public void drawLineAndLiveboxes(UGraphic ug, double height, Context2D context) {
		mutingLine.drawLine(ug, context, createY, height);
		liveboxes.drawBoxes(ug, context, createY, height);
	}

	// public void addDelayTile(DelayTile tile) {
	// System.err.println("addDelayTile " + this + " " + tile);
	// }

	public void drawHead(UGraphic ug, Context2D context, VerticalAlignment verticalAlignment,
			HorizontalAlignment horizontalAlignment) {
		if (create && verticalAlignment == VerticalAlignment.BOTTOM) {
			return;
		}
		final Component comp = rose.createComponent(p.getUsedStyles(), headType, null,
				p.getSkinParamBackcolored(skinParam), p.getDisplay(skinParam.forceSequenceParticipantUnderlined()));
		final Dimension2D dim = comp.getPreferredDimension(ug.getStringBounder());
		if (horizontalAlignment == HorizontalAlignment.RIGHT) {
			ug = ug.apply(UTranslate.dx(-dim.getWidth()));
		}
		if (verticalAlignment == VerticalAlignment.CENTER) {
			ug = ug.apply(UTranslate.dy(-dim.getHeight() / 2));
		}
		final Area area = new Area(dim);
		final Url url = getParticipant().getUrl();
		if (url != null) {
			ug.startUrl(url);
		}
		comp.drawU(ug, area, context);
		if (url != null) {
			ug.closeUrl();
		}
	}

	public Dimension2D getHeadPreferredDimension(StringBounder stringBounder) {
		final Component comp = rose.createComponent(p.getUsedStyles(), headType, null, skinParam,
				p.getDisplay(skinParam.forceSequenceParticipantUnderlined()));
		final Dimension2D dim = comp.getPreferredDimension(stringBounder);
		return dim;
	}

	private double getPreferredWidth(StringBounder stringBounder) {
		return getHeadPreferredDimension(stringBounder).getWidth();
	}

	public Real getPosC(StringBounder stringBounder) {
		if (posC == null) {
			this.posC = posB.addFixed(this.getPreferredWidth(stringBounder) / 2);
		}
		return posC;
	}

	public Real getPosC2(StringBounder stringBounder) {
		final double delta = liveboxes.getMaxPosition(stringBounder);
		return getPosC(stringBounder).addFixed(delta);
	}

	public Real getPosD(StringBounder stringBounder) {
		if (posD == null) {
			this.posD = posB.addFixed(this.getPreferredWidth(stringBounder));
		}
		// System.err.println("LivingSpace::getPosD "+posD.getCurrentValue());
		return posD;
	}

	public Real getPosB(StringBounder stringBounder) {
		return posB;
	}

	public Participant getParticipant() {
		return p;
	}

	public void goCreate(double y) {
		this.createY = y;
		this.create = true;
	}

	public void goCreate() {
		this.create = true;
	}

	public void delayOn(double y, double height) {
		mutingLine.delayOn(y, height);
		liveboxes.delayOn(y, height);
	}

	public ParticipantEnglober getEnglober() {
		return englober;
	}

	private double marginBefore;
	private double marginAfter;

	public void ensureMarginBefore(double margin) {
		if (margin < 0)
			throw new IllegalArgumentException();
		this.marginBefore = Math.max(marginBefore, margin);
	}

	public void ensureMarginAfter(double margin) {
		if (margin < 0)
			throw new IllegalArgumentException();
		this.marginAfter = Math.max(marginAfter, margin);
	}

	public Real getPosA(StringBounder stringBounder) {
		return getPosB(stringBounder).addFixed(-marginBefore);
	}

	public Real getPosE(StringBounder stringBounder) {
		return getPosD(stringBounder).addFixed(marginAfter);
	}

}
