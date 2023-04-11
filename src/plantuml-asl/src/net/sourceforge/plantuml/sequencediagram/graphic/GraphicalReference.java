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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.util.Objects;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.sequencediagram.InGroupable;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.url.Url;

class GraphicalReference extends GraphicalElement implements InGroupable {

	private final Component comp;
	private final LivingParticipantBox livingParticipantBox1;
	private final LivingParticipantBox livingParticipantBox2;
	private final Url url;

	public GraphicalReference(double startingY, Component comp, LivingParticipantBox livingParticipantBox1,
			LivingParticipantBox livingParticipantBox2, Url url) {
		super(startingY);
		this.url = url;
		this.comp = comp;
		this.livingParticipantBox1 = Objects.requireNonNull(livingParticipantBox1);
		this.livingParticipantBox2 = Objects.requireNonNull(livingParticipantBox2);
	}

	@Override
	protected void drawInternalU(UGraphic ug, double maxX, Context2D context) {

		final StringBounder stringBounder = ug.getStringBounder();
		final double posX = getMinX(stringBounder);

		ug = ug.apply(new UTranslate(posX, getStartingY()));
		final double preferredWidth = comp.getPreferredWidth(stringBounder);
		final double w = getMaxX(stringBounder) - getMinX(stringBounder);

		final double width = Math.max(preferredWidth, w);

		final XDimension2D dim = new XDimension2D(width, comp.getPreferredHeight(stringBounder));
		if (url != null) {
			ug.startUrl(url);
		}
		comp.drawU(ug, new Area(dim), context);
		if (url != null) {
			ug.closeUrl();
		}
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return comp.getPreferredHeight(stringBounder);
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return comp.getPreferredWidth(stringBounder);
	}

	@Override
	public double getStartingX(StringBounder stringBounder) {
		return getMinX(stringBounder);
	}

	public double getMaxX(StringBounder stringBounder) {
		return Math.max(livingParticipantBox1.getMaxX(stringBounder), livingParticipantBox2.getMaxX(stringBounder));
	}

	public double getMinX(StringBounder stringBounder) {
		return Math.min(livingParticipantBox1.getMinX(stringBounder), livingParticipantBox2.getMinX(stringBounder));
	}

	public String toString(StringBounder stringBounder) {
		return toString();
	}

}
