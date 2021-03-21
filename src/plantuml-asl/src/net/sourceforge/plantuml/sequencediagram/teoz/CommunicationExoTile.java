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
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.MessageExo;
import net.sourceforge.plantuml.sequencediagram.MessageExoType;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.ArrowComponent;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.ArrowDecoration;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.rose.ComponentRoseArrow;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class CommunicationExoTile extends AbstractTile {

	private final LivingSpace livingSpace;
	private final MessageExo message;
	private final Rose skin;
	private final ISkinParam skinParam;
	private final TileArguments tileArguments;

	public Event getEvent() {
		return message;
	}

	public CommunicationExoTile(LivingSpace livingSpace, MessageExo message, Rose skin, ISkinParam skinParam,
			TileArguments tileArguments) {
		super(tileArguments.getStringBounder());
		this.tileArguments = tileArguments;
		this.livingSpace = livingSpace;
		this.message = message;
		this.skin = skin;
		this.skinParam = skinParam;
	}

	@Override
	public double getContactPointRelative() {
		return getComponent(getStringBounder()).getYPoint(getStringBounder());
	}

	private ArrowComponent getComponent(StringBounder stringBounder) {
		ArrowConfiguration arrowConfiguration = message.getArrowConfiguration();
		if (message.getType().getDirection() == -1) {
			arrowConfiguration = arrowConfiguration.reverse();
		}
		final ArrowComponent comp = skin.createComponentArrow(message.getUsedStyles(), arrowConfiguration, skinParam,
				message.getLabelNumbered());
		return comp;
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Component comp = getComponent(stringBounder);
		final Dimension2D dim = comp.getPreferredDimension(stringBounder);
		double x1 = getPoint1Value(stringBounder);
		double x2 = getPoint2Value(stringBounder);
		final int level = livingSpace.getLevelAt(this, EventsHistoryMode.IGNORE_FUTURE_DEACTIVATE);
		if (level > 0) {
			if (message.getType().isRightBorder()) {
				x1 += CommunicationTile.LIVE_DELTA_SIZE * level;
			} else {
				x2 += CommunicationTile.LIVE_DELTA_SIZE * (level - 2);
			}
		}

		final ArrowConfiguration arrowConfiguration = message.getArrowConfiguration();
		final MessageExoType type = message.getType();
		if (arrowConfiguration.getDecoration1() == ArrowDecoration.CIRCLE && type == MessageExoType.FROM_LEFT) {
			x1 += ComponentRoseArrow.diamCircle / 2 + 2;
		}
		if (arrowConfiguration.getDecoration2() == ArrowDecoration.CIRCLE && type == MessageExoType.TO_LEFT) {
			x1 += ComponentRoseArrow.diamCircle / 2 + 2;
		}
		if (arrowConfiguration.getDecoration2() == ArrowDecoration.CIRCLE && type == MessageExoType.TO_RIGHT) {
			x2 -= ComponentRoseArrow.diamCircle / 2 + 2;
		}
		if (arrowConfiguration.getDecoration1() == ArrowDecoration.CIRCLE && type == MessageExoType.FROM_RIGHT) {
			x2 -= ComponentRoseArrow.diamCircle / 2 + 2;
		}

		final Area area = new Area(x2 - x1, dim.getHeight());
		ug = ug.apply(UTranslate.dx(x1));
		comp.drawU(ug, area, (Context2D) ug);
	}

	private boolean isShortArrow() {
		return message.isShortArrow();
	}

	public double getPreferredHeight() {
		final Component comp = getComponent(getStringBounder());
		final Dimension2D dim = comp.getPreferredDimension(getStringBounder());
		return dim.getHeight();
	}

	private double getPreferredWidth(StringBounder stringBounder) {
		final Component comp = getComponent(stringBounder);
		final Dimension2D dim = comp.getPreferredDimension(stringBounder);
		return dim.getWidth();
	}

	public void addConstraints() {
		final Component comp = getComponent(getStringBounder());
		final Dimension2D dim = comp.getPreferredDimension(getStringBounder());
		final double width = dim.getWidth();

		if (message.getType().isRightBorder()) {

		} else {
			livingSpace.getPosC(getStringBounder()).ensureBiggerThan(tileArguments.getOrigin().addFixed(width));
		}

		// final Real point1 = getPoint1(stringBounder);
		// if (message.getType().isRightBorder()) {
		// final Real point2 = point1.addFixed(width);
		// } else {
		// final Real point2 = getPoint2(stringBounder);
		// if (point1.getCurrentValue() < point2.getCurrentValue()) {
		// point2.ensureBiggerThan(point1.addFixed(width));
		// } else {
		// point1.ensureBiggerThan(point2.addFixed(width));
		// }
		// }
	}

	@Override
	public void callbackY_internal(double y) {
		final ArrowComponent comp = getComponent(getStringBounder());
		final Dimension2D dim = comp.getPreferredDimension(getStringBounder());
		final double arrowY = comp.getStartPoint(getStringBounder(), dim).getY();

		livingSpace.addStepForLivebox(getEvent(), y + arrowY);

	}

	private Real getPoint1(final StringBounder stringBounder) {
		if (message.getType().isRightBorder()) {
			return livingSpace.getPosC(stringBounder);
		}
		return tileArguments.getOrigin();
	}

	private double getPoint1Value(final StringBounder stringBounder) {
		if (message.getType().isRightBorder()) {
			return livingSpace.getPosC(stringBounder).getCurrentValue();
		}
		if (isShortArrow()) {
			return getPoint2Value(stringBounder) - getPreferredWidth(stringBounder);
		}
		return tileArguments.getBorder1();
	}

	private double getPoint2Value(final StringBounder stringBounder) {
		if (message.getType().isRightBorder()) {
			if (isShortArrow()) {
				return getPoint1Value(stringBounder) + getPreferredWidth(stringBounder);
			}
			return tileArguments.getBorder2();
		}
		return livingSpace.getPosC(stringBounder).getCurrentValue();
	}

	public Real getMinX() {
		return getPoint1(getStringBounder());
	}

	public Real getMaxX() {
		final Component comp = getComponent(getStringBounder());
		final Dimension2D dim = comp.getPreferredDimension(getStringBounder());
		final double width = dim.getWidth();
		return getPoint1(getStringBounder()).addFixed(width);
	}

}
