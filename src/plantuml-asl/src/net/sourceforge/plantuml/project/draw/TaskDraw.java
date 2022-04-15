/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.project.LabelStrategy;
import net.sourceforge.plantuml.project.core.Task;
import net.sourceforge.plantuml.project.core.TaskAttribute;
import net.sourceforge.plantuml.project.lang.CenterBorderColor;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public interface TaskDraw extends UDrawable {

	public TaskDraw getTrueRow();

	public void setColorsAndCompletion(CenterBorderColor colors, int completion, Url url, Display note);

	public Real getY(StringBounder stringBounder);

	public double getY(StringBounder stringBounder, Direction direction);

	public void drawTitle(UGraphic ug, LabelStrategy labelStrategy, double colTitles, double colBars);

	public double getTitleWidth(StringBounder stringBounder);

	public double getFullHeightTask(StringBounder stringBounder);

	public double getHeightMax(StringBounder stringBounder);

	public Task getTask();

	public FingerPrint getFingerPrint(StringBounder stringBounder);

	public FingerPrint getFingerPrintNote(StringBounder stringBounder);

	public double getX1(TaskAttribute taskAttribute);

	public double getX2(TaskAttribute taskAttribute);

}
