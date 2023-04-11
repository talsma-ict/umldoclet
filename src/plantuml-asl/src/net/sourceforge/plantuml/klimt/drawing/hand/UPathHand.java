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
package net.sourceforge.plantuml.klimt.drawing.hand;

import java.util.Random;

import net.sourceforge.plantuml.klimt.UPath;
import net.sourceforge.plantuml.klimt.geom.USegment;
import net.sourceforge.plantuml.klimt.geom.USegmentType;
import net.sourceforge.plantuml.klimt.geom.XCubicCurve2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;

public class UPathHand {

	private final UPath path;
	private final double defaultVariation = 4.0;

	public UPathHand(UPath source, Random rnd) {

		final UPath result = UPath.none();

		XPoint2D last = new XPoint2D(0, 0);

		for (USegment segment : source) {
			final USegmentType type = segment.getSegmentType();
			if (type == USegmentType.SEG_MOVETO) {
				final double x = segment.getCoord()[0];
				final double y = segment.getCoord()[1];
				result.moveTo(x, y);
				last = new XPoint2D(x, y);
			} else if (type == USegmentType.SEG_CUBICTO) {
				final double x2 = segment.getCoord()[4];
				final double y2 = segment.getCoord()[5];
				final HandJiggle jiggle = HandJiggle.create(last, 2.0, rnd);

				final XCubicCurve2D tmp = new XCubicCurve2D(last.getX(), last.getY(), segment.getCoord()[0],
						segment.getCoord()[1], segment.getCoord()[2], segment.getCoord()[3], x2, y2);
				jiggle.curveTo(tmp);
				jiggle.appendTo(result);
				last = new XPoint2D(x2, y2);
			} else if (type == USegmentType.SEG_LINETO) {
				final double x = segment.getCoord()[0];
				final double y = segment.getCoord()[1];
				final HandJiggle jiggle = new HandJiggle(last.getX(), last.getY(), defaultVariation, rnd);
				jiggle.lineTo(x, y);
				for (USegment seg2 : jiggle.toUPath())
					if (seg2.getSegmentType() == USegmentType.SEG_LINETO)
						result.lineTo(seg2.getCoord()[0], seg2.getCoord()[1]);

				last = new XPoint2D(x, y);
			} else if (type == USegmentType.SEG_ARCTO) {
				final double x = segment.getCoord()[5];
				final double y = segment.getCoord()[6];
				result.lineTo(x, y);
				last = new XPoint2D(x, y);
			} else {
				this.path = source;
				return;
			}
		}
		this.path = result;
		this.path.setDeltaShadow(source.getDeltaShadow());
	}

	public UPath getHanddrawn() {
		return this.path;
	}

}
