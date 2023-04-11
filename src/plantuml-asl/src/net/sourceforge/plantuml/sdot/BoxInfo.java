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
package net.sourceforge.plantuml.sdot;

import h.ST_Agnode_s;
import h.ST_Agnodeinfo_t;
import h.ST_Agraphinfo_t;
import h.ST_boxf;
import h.ST_textlabel_t;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;

public class BoxInfo {

	private final XPoint2D upperRight;
	private final XPoint2D lowerLeft;

	private BoxInfo(XPoint2D upperRight, XPoint2D lowerLeft) {
		this.upperRight = upperRight;
		this.lowerLeft = lowerLeft;
	}

	public static BoxInfo fromTextlabel(ST_textlabel_t label) {
		final double x = label.pos.x;
		final double y = label.pos.y;
		final double width = label.dimen.x;
		final double height = label.dimen.y;

		final XPoint2D upperRight = new XPoint2D(x + width / 2, y - height / 2);
		final XPoint2D lowerLeft = new XPoint2D(x - width / 2, y + height / 2);
		return new BoxInfo(upperRight, lowerLeft);

	}

	public static BoxInfo fromNode(ST_Agnode_s node) {
		final ST_Agnodeinfo_t data = (ST_Agnodeinfo_t) node.data;
		final double width = data.width * 72;
		final double height = data.height * 72;
		final double x = data.coord.x;
		final double y = data.coord.y;

		final XPoint2D upperRight = new XPoint2D(x + width / 2, y - height / 2);
		final XPoint2D lowerLeft = new XPoint2D(x - width / 2, y + height / 2);
		return new BoxInfo(upperRight, lowerLeft);
	}

	public XDimension2D getDimension() {
		final double width = upperRight.getX() - lowerLeft.getX();
		final double height = lowerLeft.getY() - upperRight.getY();
		return new XDimension2D(width, height);
	}

	public static BoxInfo fromGraphInfo(ST_Agraphinfo_t data) {
		final ST_boxf bb = (ST_boxf) data.bb;
		final double llx = bb.LL.x;
		final double lly = bb.LL.y;
		final double urx = bb.UR.x;
		final double ury = bb.UR.y;

		final XPoint2D upperRight = new XPoint2D(urx, ury);
		final XPoint2D lowerLeft = new XPoint2D(llx, lly);
		return new BoxInfo(upperRight, lowerLeft);
	}

	public final XPoint2D getUpperRight() {
		return upperRight;
	}

	public final XPoint2D getLowerLeft() {
		return lowerLeft;
	}

}
