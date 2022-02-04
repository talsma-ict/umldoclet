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

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.real.RealUtils;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.LifeEvent;
import net.sourceforge.plantuml.sequencediagram.LifeEventType;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class LifeEventTile extends AbstractTile {

	private final LifeEvent lifeEvent;
	private final TileArguments tileArguments;
	private final LivingSpace livingSpace;
	private final Rose skin;
	private final ISkinParam skinParam;

	@Override
	final protected void callbackY_internal(double y) {
		// System.err.println("LifeEventTile::updateStairs " + lifeEvent + " " +
		// livingSpace.getParticipant() + " y=" + y);
		livingSpace.addStepForLivebox(getEvent(), y);
	}

	public Event getEvent() {
		return lifeEvent;
	}

	@Override
	public double getContactPointRelative() {
		return 0;
	}

	public LifeEventTile(LifeEvent lifeEvent, TileArguments tileArguments, LivingSpace livingSpace, Rose skin,
			ISkinParam skinParam) {
		super(tileArguments.getStringBounder());
		this.lifeEvent = lifeEvent;
		this.tileArguments = tileArguments;
		this.livingSpace = livingSpace;
		this.skin = skin;
		this.skinParam = skinParam;
	}

	public void drawU(UGraphic ug) {
		if (isDestroyWithoutMessage()) {
			final Component cross = skin.createComponent(null, ComponentType.DESTROY, null, skinParam, null);
			final Dimension2D dimCross = cross.getPreferredDimension(ug.getStringBounder());
			final double x = livingSpace.getPosC(ug.getStringBounder()).getCurrentValue();
			cross.drawU(ug.apply(UTranslate.dx(x - dimCross.getWidth() / 2)), null, (Context2D) ug);
		}
	}

	public boolean isDestroyWithoutMessage() {
		return lifeEvent.getMessage() == null && lifeEvent.getType() == LifeEventType.DESTROY;
	}

	public double getPreferredHeight() {
//		if (lifeEvent.isActivate()) {
//			return 20;
//		}
		if (isDestroyWithoutMessage()) {
			final Component cross = skin.createComponent(null, ComponentType.DESTROY, null, skinParam, null);
			final Dimension2D dimCross = cross.getPreferredDimension(getStringBounder());
			return dimCross.getHeight();
		}
		return 0;
	}

	public void addConstraints() {
	}

	public Real getMinX() {
		// return tileArguments.getLivingSpace(lifeEvent.getParticipant()).getPosB();
		return livingSpace.getPosB(getStringBounder());
	}

	public Real getMaxX() {
		// final LivingSpace livingSpace2 =
		// tileArguments.getLivingSpace(lifeEvent.getParticipant());
		return RealUtils.max(livingSpace.getPosD(getStringBounder()), livingSpace.getPosC2(getStringBounder()));
	}

}
