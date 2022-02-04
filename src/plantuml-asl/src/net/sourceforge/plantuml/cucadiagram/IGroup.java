/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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
package net.sourceforge.plantuml.cucadiagram;

import java.util.Collection;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.svek.IEntityImage;
import net.sourceforge.plantuml.svek.PackageStyle;
import net.sourceforge.plantuml.svek.SingleStrategy;

public interface IGroup extends IEntity {

	public boolean containsLeafRecurse(ILeaf entity);

	public Collection<ILeaf> getLeafsDirect();

	public Collection<IGroup> getChildren();

	public void moveEntitiesTo(IGroup dest);

	public int size();

	public GroupType getGroupType();

	public Code getNamespace();

	public PackageStyle getPackageStyle();

	public void overrideImage(IEntityImage img, LeafType state);

	public SingleStrategy getSingleStrategy();

	public FontConfiguration getFontConfigurationForTitle(ISkinParam skinParam);

	public char getConcurrentSeparator();

	public void setConcurrentSeparator(char separator);

	public void setLegend(DisplayPositioned legend);

	public DisplayPositioned getLegend();

}
