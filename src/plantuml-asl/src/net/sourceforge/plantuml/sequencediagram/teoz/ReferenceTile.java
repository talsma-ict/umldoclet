/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.Reference;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Context2D;

public class ReferenceTile extends AbstractTile implements Tile {

	private final Reference reference;
	private final TileArguments tileArguments;
	private Real first;
	private Real last;
	private final YGauge yGauge;

	public Event getEvent() {
		return reference;
	}

	public ReferenceTile(Reference reference, TileArguments tileArguments, YGauge currentY) {
		super(tileArguments.getStringBounder(), currentY);
		this.reference = reference;
		this.tileArguments = tileArguments;
		this.yGauge = YGauge.create(currentY.getMax(), getPreferredHeight());
	}

	@Override
	public YGauge getYGauge() {
		return yGauge;
	}

	private void init(StringBounder stringBounder) {
		if (first != null) {
			return;
		}
		for (Participant p : reference.getParticipant()) {
			final LivingSpace livingSpace = tileArguments.getLivingSpace(p);
			final Real pos = livingSpace.getPosC(stringBounder);
			if (first == null || pos.getCurrentValue() < first.getCurrentValue()) {
				this.first = livingSpace.getPosB(stringBounder);
			}
			if (last == null || pos.getCurrentValue() > last.getCurrentValue()) {
				this.last = livingSpace.getPosD(stringBounder);
			}
		}
		final Component comp = getComponent(stringBounder);
		final XDimension2D dim = comp.getPreferredDimension(stringBounder);
		if (reference.getParticipant().size() == 1) {
			this.last = this.last.addAtLeast(0);
		}
		this.last.ensureBiggerThan(this.first.addFixed(dim.getWidth()));

	}

	private Component getComponent(StringBounder stringBounder) {
		Display strings = Display.empty();
		strings = strings.add("ref");
		strings = strings.addAll(reference.getStrings());

		final Component comp = tileArguments.getSkin().createComponent(reference.getUsedStyles(),
				ComponentType.REFERENCE, null, tileArguments.getSkinParam(), strings);
		return comp;
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		init(stringBounder);
		final Component comp = getComponent(stringBounder);
		final XDimension2D dim = comp.getPreferredDimension(stringBounder);
		final Area area = Area.create(last.getCurrentValue() - first.getCurrentValue(), dim.getHeight());

		ug = ug.apply(UTranslate.dx(first.getCurrentValue()));
		comp.drawU(ug, area, (Context2D) ug);
	}

	public double getPreferredHeight() {
		final Component comp = getComponent(getStringBounder());
		final XDimension2D dim = comp.getPreferredDimension(getStringBounder());
		return dim.getHeight();
	}

	public void addConstraints() {
	}

	public Real getMinX() {
		init(getStringBounder());
		return this.first;
	}

	public Real getMaxX() {
		init(getStringBounder());
		return this.last;
	}

}
