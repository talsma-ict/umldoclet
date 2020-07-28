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
package net.sourceforge.plantuml.project.draw;

import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.project.core.AbstractTask;
import net.sourceforge.plantuml.project.time.Wink;
import net.sourceforge.plantuml.project.timescale.TimeScale;

public abstract class AbstractTaskDraw implements TaskDraw {

	protected final TimeScale timeScale;
	protected final double y;
	protected final String prettyDisplay;
	protected final Wink start;

	protected final double margin = 2;

	public AbstractTaskDraw(TimeScale timeScale, double y, String prettyDisplay, Wink start) {
		this.y = y;
		this.start = start;
		this.prettyDisplay = prettyDisplay;
		this.timeScale = timeScale;
	}

	abstract protected FontConfiguration getFontConfiguration();

	final protected double getShapeHeight() {
		return getHeight() - 2 * margin;
	}

	final public double getHeight() {
		return AbstractTask.HEIGHT;
	}

	final public double getY() {
		return y;
	}

}
