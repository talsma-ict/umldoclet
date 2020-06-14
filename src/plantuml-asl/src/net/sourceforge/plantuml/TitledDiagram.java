/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml;

import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.DisplayPositionned;
import net.sourceforge.plantuml.cucadiagram.DisplaySection;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.VerticalAlignment;

public abstract class TitledDiagram extends AbstractPSystem implements Diagram, Annotated {

	private DisplayPositionned title = DisplayPositionned.none(HorizontalAlignment.CENTER, VerticalAlignment.TOP);

	private DisplayPositionned caption = DisplayPositionned.none(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
	private DisplayPositionned legend = DisplayPositionned.none(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
	private final DisplaySection header = DisplaySection.none();
	private final DisplaySection footer = DisplaySection.none();
	private Display mainFrame;

	
	final public void setTitle(DisplayPositionned title) {
		if (title.isNull() || title.getDisplay().isWhite()) {
			return;
		}
		this.title = title;
	}

	@Override
	final public DisplayPositionned getTitle() {
		return title;
	}
	
	final public void setMainFrame(Display mainFrame) {
		this.mainFrame = mainFrame;
	}

	final public void setCaption(DisplayPositionned caption) {
		this.caption = caption;
	}

	final public DisplayPositionned getCaption() {
		return caption;
	}
	
	final public DisplaySection getHeader() {
		return header;
	}

	final public DisplaySection getFooter() {
		return footer;
	}
	
	final public DisplayPositionned getLegend() {
		return legend;
	}

	public void setLegend(DisplayPositionned legend) {
		this.legend = legend;
	}

	final public Display getMainFrame() {
		return mainFrame;
	}







}
