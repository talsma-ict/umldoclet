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
package net.sourceforge.plantuml.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;

class EntityImageActivityCircle extends AbstractEntityImage {

	private final int diameterExternal;
	private final int diameterInternal;

	public EntityImageActivityCircle(IEntity entity, int diameterExternal, int diameterInternal) {
		super(entity);
		this.diameterExternal = diameterExternal;
		this.diameterInternal = diameterInternal;
	}

	@Override
	public Dimension2D getDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(diameterExternal, diameterExternal);
	}

	@Override
	public void draw(ColorMapper colorMapper, Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		final int delta = diameterExternal - diameterInternal + 1;
		g2d.drawOval(0, 0, diameterExternal, diameterExternal);
		g2d.fillOval(delta / 2, delta / 2, diameterInternal, diameterInternal);
	}
}
