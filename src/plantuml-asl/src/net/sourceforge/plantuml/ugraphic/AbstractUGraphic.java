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
package net.sourceforge.plantuml.ugraphic;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.plantuml.graphic.SpecialText;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public abstract class AbstractUGraphic<O> extends AbstractCommonUGraphic {

	private final O graphic;

	// It would be nice to do something like this but not sure how:
	//     Map<Class<SHAPE>, UDriver<SHAPE, O>>
	// See https://stackoverflow.com/questions/416540/java-map-with-values-limited-by-keys-type-parameter
	private final Map<Class<? extends UShape>, UDriver<?, O>> drivers = new HashMap<>();

	public AbstractUGraphic(HColor defaultBackground, ColorMapper colorMapper, StringBounder stringBounder, O graphic) {
		super(Objects.requireNonNull(defaultBackground), colorMapper, stringBounder);
		this.graphic = graphic;
	}

	protected AbstractUGraphic(AbstractUGraphic<O> other) {
		super(other);
		this.graphic = other.graphic;
		// this.drivers.putAll(other.drivers);
	}

	protected final O getGraphicObject() {
		return graphic;
	}

	protected boolean manageHiddenAutomatically() {
		return true;
	}

	final protected <SHAPE extends UShape> void registerDriver(Class<SHAPE> cl, UDriver<SHAPE, O> driver) {
		this.drivers.put(cl, driver);
	}

	private static final UDriver<?,?> NOOP_DRIVER = new UDriver<UShape, Object>() {
		@Override
		public void draw(UShape shape, double x, double y, ColorMapper mapper, UParam param, Object object) {
		}
	};
	
	@SuppressWarnings("unchecked")
	final protected <SHAPE extends UShape> void ignoreShape(Class<SHAPE> cl) {
		registerDriver(cl, (UDriver<SHAPE, O>) NOOP_DRIVER);
	}
	
	public final <SHAPE extends UShape> void draw(SHAPE shape) {
		if (shape instanceof SpecialText) {
			((SpecialText) shape).getTitle().drawU(this);
			return;
		}
		if (shape instanceof UEmpty) {
			return;
		}
		if (shape instanceof UComment) {
			drawComment((UComment) shape);
			return;
		}

		@SuppressWarnings("unchecked")
		final UDriver<SHAPE, O> driver = (UDriver<SHAPE, O>) drivers.get(shape.getClass());

		if (driver == null) {
			throw new UnsupportedOperationException(shape.getClass().toString() + " " + this.getClass());
		}
		if (getParam().isHidden() && manageHiddenAutomatically()) {
			return;
		}
		beforeDraw();
		driver.draw(shape, getTranslateX(), getTranslateY(), getColorMapper(), getParam(), graphic);
		afterDraw();
	}

	protected void drawComment(UComment shape) {
	}

	protected void beforeDraw() {
	}

	protected void afterDraw() {
	}

}
