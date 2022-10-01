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
 * Creator:  Hisashi Miyashita
 */

package net.sourceforge.plantuml.svek.image;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.cucadiagram.EntityPosition;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Rankdir;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Bibliotekon;
import net.sourceforge.plantuml.svek.Cluster;
import net.sourceforge.plantuml.svek.ShapeType;

public abstract class AbstractEntityImageBorder extends AbstractEntityImage {
	public final EntityPosition entityPosition;
	protected final Cluster parent;
	protected final Bibliotekon bibliotekon;
	protected final Rankdir rankdir;

	protected final TextBlock desc;

	AbstractEntityImageBorder(ILeaf leaf, ISkinParam skinParam, Cluster parent, Bibliotekon bibliotekon,
			FontParam fontParam) {
		super(leaf, skinParam);

		this.parent = parent;
		this.bibliotekon = bibliotekon;
		this.entityPosition = leaf.getEntityPosition();
		this.rankdir = skinParam.getRankdir();

		if (entityPosition == EntityPosition.NORMAL) {
			throw new IllegalArgumentException();
		}

		final Stereotype stereotype = leaf.getStereotype();
		final FontConfiguration fc = FontConfiguration.create(skinParam, fontParam, stereotype);
		this.desc = leaf.getDisplay().create(fc, HorizontalAlignment.CENTER, skinParam);
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return entityPosition.getDimension(rankdir);
	}

	public ShapeType getShapeType() {
		return entityPosition.getShapeType();
	}

}
