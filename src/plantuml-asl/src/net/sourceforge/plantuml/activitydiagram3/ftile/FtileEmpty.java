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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class FtileEmpty extends AbstractFtile {

	private final double width;
	private final double height;
	private final Swimlane swimlaneIn;
	private final Swimlane swimlaneOut;

	@Override
	public Collection<Ftile> getMyChildren() {
		return Collections.emptyList();
	}

	public FtileEmpty(ISkinParam skinParam, double width, double height) {
		this(skinParam, width, height, null, null);
	}

	public FtileEmpty(ISkinParam skinParam, double width, double height, Swimlane swimlaneIn, Swimlane swimlaneOut) {
		super(skinParam);
		this.width = width;
		this.height = height;
		this.swimlaneIn = swimlaneIn;
		this.swimlaneOut = swimlaneOut;

	}

	public FtileEmpty(ISkinParam skinParam) {
		this(skinParam, 0, 0, null, null);
	}

	public FtileEmpty(ISkinParam skinParam, Swimlane swimlane) {
		this(skinParam, 0, 0, swimlane, swimlane);
	}

	@Override
	public String toString() {
		return "FtileEmpty";
	}

	public void drawU(UGraphic ug) {
	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		return calculateDimensionEmpty();
	}

	final protected FtileGeometry calculateDimensionEmpty() {
		return new FtileGeometry(width, height, width / 2, 0, height);
	}

	public Swimlane getSwimlaneIn() {
		return swimlaneIn;
	}

	public Swimlane getSwimlaneOut() {
		return swimlaneOut;
	}

	public Set<Swimlane> getSwimlanes() {
		final Set<Swimlane> result = new HashSet<Swimlane>();
		if (swimlaneIn != null) {
			result.add(swimlaneIn);
		}
		if (swimlaneOut != null) {
			result.add(swimlaneOut);
		}
		return Collections.unmodifiableSet(result);
	}

}
