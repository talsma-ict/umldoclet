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
package net.sourceforge.plantuml.posimo;

import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.Positionable;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;

public class MargedBlock {

	private final Block block;
	private final IEntityImageBlock imageBlock;
	private final double marginDecorator;
	private final XDimension2D imageDimension;

	static private int uid = 1;

	public MargedBlock(StringBounder stringBounder, IEntityImageBlock imageBlock, double marginDecorator,
			Cluster parent) {
		this.imageBlock = imageBlock;
		this.marginDecorator = marginDecorator;
		this.imageDimension = imageBlock.getDimension(stringBounder);
		this.block = new Block(uid++, imageDimension.getWidth() + 2 * marginDecorator,
				imageDimension.getHeight() + 2 * marginDecorator, parent);
	}

	public Block getBlock() {
		return block;
	}

	public double getMarginDecorator() {
		return marginDecorator;
	}

	public IEntityImageBlock getImageBlock() {
		return imageBlock;
	}

	public Positionable getImagePosition() {
		return new Positionable() {

			public XDimension2D getSize() {
				return imageDimension;
			}

			public XPoint2D getPosition() {
				final XPoint2D pos = block.getPosition();
				return new XPoint2D(pos.getX() + marginDecorator, pos.getY() + marginDecorator);
			}

			public void moveSvek(double deltaX, double deltaY) {
				throw new UnsupportedOperationException();
			}
		};
	}

}
