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
package net.sourceforge.plantuml.svek;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.creole.CreoleMode;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.URectangle;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.svek.extremity.Extremity;
import net.sourceforge.plantuml.utils.Direction;

public class Kal implements UDrawable {

	private final TextBlock textBlock;
	private final Direction position;
	private XDimension2D dim;
	private UTranslate translate;
	private final SvekLine svekLine;
	private final Entity entity;
	private final Link link;

	public Kal(SvekLine svekLine, String text, FontConfiguration font, ISkinParam skinParam, Entity entity, Link link,
			StringBounder stringBounder) {
		this.svekLine = svekLine;
		this.entity = entity;
		this.link = link;
		this.textBlock = Display.getWithNewlines(text).create7(font, HorizontalAlignment.LEFT, skinParam,
				CreoleMode.SIMPLE_LINE);
		this.dim = this.textBlock.calculateDimension(stringBounder).delta(4, 2);

		if (link.getLength() == 1 && link.getEntity1() == entity) {
			this.position = Direction.RIGHT;
			entity.ensureMargins(new Margins(0, dim.getWidth(), 0, 0));

		} else if (link.getLength() == 1 && link.getEntity2() == entity) {
			this.position = Direction.LEFT;
			entity.ensureMargins(new Margins(dim.getWidth(), 0, 0, 0));

		} else if (link.getEntity1() == entity) {
			this.position = Direction.DOWN;
			entity.ensureMargins(new Margins(0, 0, dim.getHeight(), 0));

		} else if (link.getEntity2() == entity) {
			this.position = Direction.UP;
			entity.ensureMargins(new Margins(0, 0, 0, dim.getHeight()));

		} else {
			throw new IllegalStateException();
		}

		entity.addKal(this);

	}

	public XDimension2D getDimension() {
		return dim;
	}

	@Override
	public void drawU(UGraphic ug) {
		final URectangle rect = URectangle.build(dim);
		ug = ug.apply(getTranslate());
		ug.apply(HColors.WHITE.bg()).apply(HColors.BLACK).apply(UStroke.withThickness(0.5)).draw(rect);
		textBlock.drawU(ug.apply(new UTranslate(2, 1)));
	}

	private UTranslate getTranslate() {
		return getTextDelta().compose(translate);

	}

	public double getX1() {
		return getTranslate().getDx() - 5;
	}

	public double getX2() {
		return getX1() + dim.getWidth() + 10;
	}

	private UTranslate getTextDelta() {
		switch (position) {
		case RIGHT:
			return UTranslate.dy(-dim.getHeight() / 2);
		case LEFT:
			return new UTranslate(-dim.getWidth() + 0.5, -dim.getHeight() / 2);
		case DOWN:
			return UTranslate.dx(-dim.getWidth() / 2);
		case UP:
			return new UTranslate(-dim.getWidth() / 2, -dim.getHeight() + 0.5);
		default:
			throw new IllegalStateException();
		}

	}

	public final Direction getPosition() {
		return position;
	}

	public void setTranslate(UTranslate translate, UDrawable decoration) {
		this.translate = translate;
		if (decoration instanceof Extremity) {
			final Extremity extremity = (Extremity) decoration;
			final UTranslate deltaForKal = extremity.getDeltaForKal();
			// this.translate = this.translate.compose(deltaForKal);
		}
	}

	public double overlapx(Kal other) {
		if (this.position != other.position)
			throw new IllegalArgumentException();
		if (other.getX1() >= this.getX1() && other.getX1() <= this.getX2())
			return this.getX2() - other.getX1();
		if (other.getX2() >= this.getX1() && other.getX2() <= this.getX2())
			return this.getX1() - other.getX2();

		if (this.getX1() >= other.getX1() && this.getX1() <= other.getX2())
			return other.getX2() - this.getX1();
		if (this.getX2() >= other.getX1() && this.getX2() <= other.getX2())
			return other.getX1() - this.getX2();

		return 0;
	}

	public void moveX(double dx) {
		if (dx == 0)
			return;
		this.translate = this.translate.compose(UTranslate.dx(dx));
		if (link.getEntity1() == entity)
			svekLine.moveStartPoint(dx, 0);

	}

}
