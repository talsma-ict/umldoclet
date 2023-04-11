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
package net.sourceforge.plantuml.svek;

import java.util.Objects;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.shape.AbstractTextBlock;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.ISkinParam;

public abstract class AbstractEntityImage extends AbstractTextBlock implements IEntityImage {

	private final Entity entity;
	private final ISkinParam skinParam;

	public AbstractEntityImage(Entity entity, ISkinParam skinParam) {
		this.entity = Objects.requireNonNull(entity);
		this.skinParam = Objects.requireNonNull(skinParam);
	}

	@Override
	public boolean isHidden() {
		return entity.isHidden();
	}

	protected final Entity getEntity() {
		return entity;
	}

	protected final ISkinParam getSkinParam() {
		return skinParam;
	}

	@Override
	public final HColor getBackcolor() {
		return skinParam.getBackgroundColor();
	}

	protected final Stereotype getStereo() {
		return entity.getStereotype();
	}

	@Override
	public Margins getShield(StringBounder stringBounder) {
		return Margins.NONE;
	}

	@Override
	public double getOverscanX(StringBounder stringBounder) {
		return 0;
	}

}
