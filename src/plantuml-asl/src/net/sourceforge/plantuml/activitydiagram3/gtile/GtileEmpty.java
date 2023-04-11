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
package net.sourceforge.plantuml.activitydiagram3.gtile;

import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.UEmpty;
import net.sourceforge.plantuml.style.ISkinParam;

public class GtileEmpty extends AbstractGtile {

	private final double width;
	private final double height;

	public GtileEmpty(StringBounder stringBounder, ISkinParam skinParam, double width, double height) {
		this(stringBounder, skinParam, width, height, null);
	}

	public GtileEmpty(StringBounder stringBounder, ISkinParam skinParam) {
		this(stringBounder, skinParam, 0, 0, null);
	}

	public GtileEmpty(StringBounder stringBounder, ISkinParam skinParam, Swimlane swimlane) {
		this(stringBounder, skinParam, 0, 0, swimlane);
	}

	public GtileEmpty(StringBounder stringBounder, ISkinParam skinParam, double width, double height,
			Swimlane swimlane) {
		super(stringBounder, skinParam, swimlane);
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString() {
		return "GtileEmpty";
	}

	@Override
	protected void drawUInternal(UGraphic ug) {
		if (width > 0 && height > 0)
			ug.draw(new UEmpty(width, height));

	}

	@Override
	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return new XDimension2D(width, height);
	}

//	@Override
//	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
//		return calculateDimensionEmpty();
//	}
//
//	final protected FtileGeometry calculateDimensionEmpty() {
//		return new FtileGeometry(width, height, width / 2, 0, height);
//	}
//
//	public Swimlane getSwimlaneIn() {
//		return swimlane;
//	}
//
//	public Swimlane getSwimlaneOut() {
//		return swimlane;
//	}
//
//	public Set<Swimlane> getSwimlanes() {
//		final Set<Swimlane> result = new HashSet<>();
//		if (swimlane != null) {
//			result.add(swimlane);
//		}
//		return Collections.unmodifiableSet(result);
//	}

}
