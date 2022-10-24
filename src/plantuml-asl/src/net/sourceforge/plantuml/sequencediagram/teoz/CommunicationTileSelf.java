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

import java.util.Iterator;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.Message;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.ArrowComponent;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class CommunicationTileSelf extends AbstractTile {

	private final LivingSpace livingSpace1;
	private final Message message;
	private final Rose skin;
	private final ISkinParam skinParam;
	private final LivingSpaces livingSpaces;
	private final YGauge yGauge;

	public Event getEvent() {
		return message;
	}

	@Override
	public double getContactPointRelative() {
		return getComponent(getStringBounder()).getYPoint(getStringBounder());
	}

	public CommunicationTileSelf(StringBounder stringBounder, LivingSpace livingSpace1, Message message, Rose skin,
			ISkinParam skinParam, LivingSpaces livingSpaces, YGauge currentY) {
		super(stringBounder, currentY);
		this.livingSpace1 = livingSpace1;
		this.livingSpaces = livingSpaces;
		this.message = message;
		this.skin = skin;
		this.skinParam = skinParam;
		this.yGauge = YGauge.create(currentY.getMax(), getPreferredHeight());
	}

	@Override
	public YGauge getYGauge() {
		return yGauge;
	}

	private ArrowComponent getComponent(StringBounder stringBounder) {
		ArrowConfiguration arrowConfiguration = message.getArrowConfiguration();
		arrowConfiguration = arrowConfiguration.self();
		final ArrowComponent comp = skin.createComponentArrow(message.getUsedStyles(), arrowConfiguration, skinParam,
				message.getLabelNumbered());
		return comp;
	}

	@Override
	final protected void callbackY_internal(TimeHook y) {
		super.callbackY_internal(y);
		final ArrowComponent comp = getComponent(getStringBounder());
		final XDimension2D dim = comp.getPreferredDimension(getStringBounder());
		final XPoint2D p1 = comp.getStartPoint(getStringBounder(), dim);
		final XPoint2D p2 = comp.getEndPoint(getStringBounder(), dim);

		if (message.isActivate())
			livingSpace1.addStepForLivebox(getEvent(), y.getValue() + p2.getY());
		else if (message.isDeactivate())
			livingSpace1.addStepForLivebox(getEvent(), y.getValue() + p1.getY());
		else if (message.isDestroy())
			livingSpace1.addStepForLivebox(getEvent(), y.getValue() + p2.getY());

	}

	public void drawU(UGraphic ug) {
		if (YGauge.USE_ME)
			ug = ug.apply(UTranslate.dy(getYGauge().getMin().getCurrentValue()));
		final StringBounder stringBounder = ug.getStringBounder();
		final Component comp = getComponent(stringBounder);
		final XDimension2D dim = comp.getPreferredDimension(stringBounder);
		double x1 = getPoint1(stringBounder).getCurrentValue();
		final int levelIgnore = livingSpace1.getLevelAt(this, EventsHistoryMode.IGNORE_FUTURE_ACTIVATE);
		final int levelConsidere = livingSpace1.getLevelAt(this, EventsHistoryMode.CONSIDERE_FUTURE_DEACTIVATE);
		Log.info("CommunicationTileSelf::drawU levelIgnore=" + levelIgnore + " levelConsidere=" + levelConsidere);
		x1 += CommunicationTile.LIVE_DELTA_SIZE * levelIgnore;
		if (levelIgnore < levelConsidere) {
			x1 += CommunicationTile.LIVE_DELTA_SIZE;
		}

		final Area area = Area.create(dim.getWidth(), dim.getHeight());
		// if (message.isActivate()) {
		// area.setDeltaX1(CommunicationTile.LIVE_DELTA_SIZE);
		// } else if (message.isDeactivate()) {
		// // area.setDeltaX1(CommunicationTile.LIVE_DELTA_SIZE);
		// // x1 += CommunicationTile.LIVE_DELTA_SIZE * levelConsidere;
		// }
		area.setDeltaX1((levelIgnore - levelConsidere) * CommunicationTile.LIVE_DELTA_SIZE);
		ug = ug.apply(UTranslate.dx(x1));
		comp.drawU(ug, area, (Context2D) ug);
	}

	public double getPreferredHeight() {
		final Component comp = getComponent(getStringBounder());
		final XDimension2D dim = comp.getPreferredDimension(getStringBounder());
		return dim.getHeight();
	}

	public void addConstraints() {
		if (isReverseDefine()) {
			final LivingSpace previous = getPrevious();
			if (previous != null) {
				livingSpace1.getPosC(getStringBounder())
						.ensureBiggerThan(previous.getPosC(getStringBounder()).addFixed(getCompWidth()));

			}
		} else {
			final LivingSpace next = getNext();
			if (next != null) {
				next.getPosC(getStringBounder()).ensureBiggerThan(getMaxX());
			}
		}
	}

	private boolean isReverseDefine() {
		return message.getArrowConfiguration().isReverseDefine();
	}

	private LivingSpace getPrevious() {
		LivingSpace previous = null;
		for (Iterator<LivingSpace> it = livingSpaces.values().iterator(); it.hasNext();) {
			final LivingSpace current = it.next();
			if (current == livingSpace1) {
				return previous;
			}
			previous = current;
		}
		return null;
	}

	private LivingSpace getNext() {
		for (Iterator<LivingSpace> it = livingSpaces.values().iterator(); it.hasNext();) {
			final LivingSpace current = it.next();
			if (current == livingSpace1 && it.hasNext()) {
				return it.next();
			}
		}
		return null;
	}

	private Real getPoint1(final StringBounder stringBounder) {
		return livingSpace1.getPosC(stringBounder);
	}

	public Real getMinX() {
		if (isReverseDefine()) {
			return getPoint1(getStringBounder());
		}
		return getPoint1(getStringBounder());
	}

	public Real getMaxX() {
		if (isReverseDefine()) {
			return livingSpace1.getPosC2(getStringBounder());
		}
		return livingSpace1.getPosC2(getStringBounder()).addFixed(getCompWidth());
	}

	private double getCompWidth() {
		final Component comp = getComponent(getStringBounder());
		return comp.getPreferredDimension(getStringBounder()).getWidth();
	}

}
