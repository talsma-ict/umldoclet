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
package net.sourceforge.plantuml.cute;

import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class PositionnedImpl implements Positionned {

	private final CuteShape cuteShape;
	private final HColor color;
	private final UTranslate position;
	private final RotationZoom rotationZoom;

	@Override
	public String toString() {
		return "Positionned " + position + " " + cuteShape;
	}

	public PositionnedImpl(CuteShape cuteShape, VarArgs args) {
		this.cuteShape = cuteShape;
		this.color = args.getAsColor("color");
		this.position = args.getPosition();
		this.rotationZoom = RotationZoom.fromVarArgs(args);
	}

	private PositionnedImpl(CuteShape cuteShape, HColor color, UTranslate position, RotationZoom rotationZoom) {
		this.cuteShape = cuteShape;
		this.color = color;
		this.position = position;
		this.rotationZoom = rotationZoom;
	}

	public PositionnedImpl(Group group, RotationZoom rotation) {
		this.cuteShape = group;
		this.color = HColorUtils.BLACK;
		this.position = new UTranslate();
		this.rotationZoom = rotation;
	}

	public PositionnedImpl(Group group, UTranslate translation) {
		this.cuteShape = group;
		this.color = HColorUtils.BLACK;
		this.position = translation;
		this.rotationZoom = RotationZoom.none();
	}

	private UGraphic applyColor(UGraphic ug) {
		return ug.apply(color.bg()).apply(color);

	}

	public void drawU(UGraphic ug) {
		ug = applyColor(ug);
		ug = ug.apply(position);
		final UDrawable tmp = rotationZoom.isNone() ? cuteShape : cuteShape.rotateZoom(rotationZoom);
		// System.err.println("rotationZoom=" + rotationZoom + " tmp=" + tmp);
		tmp.drawU(ug);
	}

	public Positionned rotateZoom(RotationZoom other) {
		return new PositionnedImpl(cuteShape, color, other.getUTranslate(position), rotationZoom.compose(other));
	}

	public Positionned translate(UTranslate other) {
		return new PositionnedImpl(cuteShape, color, position.compose(other), rotationZoom);
	}

}
