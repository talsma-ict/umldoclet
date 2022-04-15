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
package net.sourceforge.plantuml.activitydiagram3.ftile;

public class FtileGeometryMerger {

	private final FtileGeometry result;

	public FtileGeometryMerger(FtileGeometry geo1, FtileGeometry geo2) {
		final double left = Math.max(geo1.getLeft(), geo2.getLeft());
		final double dx1 = left - geo1.getLeft();
		final double dx2 = left - geo2.getLeft();
		final double width = Math.max(geo1.getWidth() + dx1, geo2.getWidth() + dx2);
		final double height = geo1.getHeight() + geo2.getHeight();

		if (geo2.hasPointOut()) {
			result = new FtileGeometry(width, height, left, geo1.getInY(), geo2.getOutY() + geo1.getHeight());
		} else {
			result = new FtileGeometry(width, height, left, geo1.getInY());
		}
	}

	public final FtileGeometry getResult() {
		return result;
	}
}
