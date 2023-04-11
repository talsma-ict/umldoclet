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
 * Creator:  Hisashi Miyashita
 */

package net.sourceforge.plantuml.svek.image;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.EntityPosition;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.Rankdir;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Bibliotekon;
import net.sourceforge.plantuml.svek.Cluster;
import net.sourceforge.plantuml.svek.ShapeType;

public abstract class AbstractEntityImageBorder extends AbstractEntityImage {
	public final EntityPosition entityPosition;
	protected final Cluster parent;
	protected final Bibliotekon bibliotekon;
	protected final Rankdir rankdir;

	protected abstract StyleSignatureBasic getSignature();

	final protected Style getStyle() {
		final Entity leaf = (Entity) getEntity();
		final Stereotype stereotype = leaf.getStereotype();
		return getSignature().withTOBECHANGED(stereotype).getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	AbstractEntityImageBorder(Entity leaf, ISkinParam skinParam, Cluster parent, Bibliotekon bibliotekon,
			FontParam fontParam) {
		super(leaf, skinParam);

		this.parent = parent;
		this.bibliotekon = bibliotekon;
		this.entityPosition = leaf.getEntityPosition();
		this.rankdir = skinParam.getRankdir();

		if (entityPosition == EntityPosition.NORMAL)
			throw new IllegalArgumentException();
	}

	protected final TextBlock getDesc() {
		final Entity leaf = (Entity) getEntity();
		final FontConfiguration fc = FontConfiguration.create(getSkinParam(), getStyle());
		return leaf.getDisplay().create(fc, HorizontalAlignment.CENTER, getSkinParam());
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return entityPosition.getDimension(rankdir);
	}

	public ShapeType getShapeType() {
		return entityPosition.getShapeType();
	}

}
