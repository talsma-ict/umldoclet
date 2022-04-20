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
package net.sourceforge.plantuml.activitydiagram3.ftile.vertical;

import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.graphic.StringBounder;

public class FtileDecoratePointOut extends FtileDecorate {

	final private double dx;
	final private double dy;

	public FtileDecoratePointOut(final Ftile ftile, final double dx, double dy) {
		super(ftile);
		this.dx = dx;
		if (dx != 0) {
			throw new IllegalArgumentException();
		}
		this.dy = dy;
	}

	@Override
	public FtileGeometry calculateDimension(StringBounder stringBounder) {
		final FtileGeometry geo = super.calculateDimension(stringBounder);
		return new FtileGeometry(geo.getWidth(), geo.getHeight(), geo.getLeft(), geo.getInY(), geo.getOutY() + dy);
	}

}
