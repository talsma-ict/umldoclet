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

import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.shape.UDrawable;

public abstract class CommonTile implements Tile, UDrawable {

	private final StringBounder stringBounder;
	private TimeHook y = new TimeHook(-1);

	public CommonTile(StringBounder stringBounder) {
		this.stringBounder = stringBounder;
	}

	final public void callbackY(TimeHook y) {
		if (YGauge.USE_ME) {
		} else {
			this.y = y;
			callbackY_internal(y);
		}
	}

	protected void callbackY_internal(TimeHook y) {
		if (YGauge.USE_ME) {
			System.err.println("callbackY_internal::y=" + y + " gauge=" + getYGauge() + " " + getClass());
		}
	}

	protected final StringBounder getStringBounder() {
		return stringBounder;
	}

	final public double getMiddleX() {
		final double max = getMaxX().getCurrentValue();
		final double min = getMinX().getCurrentValue();
		return (min + max) / 2;
	}

	public final TimeHook getTimeHook() {
		if (YGauge.USE_ME) {
			throw new IllegalStateException();
		}
		return y;
	}

}
