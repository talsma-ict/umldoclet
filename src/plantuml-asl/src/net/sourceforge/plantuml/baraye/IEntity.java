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
 * Contribution:  Miguel Esteves
 */
package net.sourceforge.plantuml.baraye;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.Hideable;
import net.sourceforge.plantuml.LineConfigurable;
import net.sourceforge.plantuml.Removeable;
import net.sourceforge.plantuml.SpecificBackcolorable;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.cucadiagram.Bodier;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.CucaNote;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityPosition;
import net.sourceforge.plantuml.cucadiagram.Ident;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Stereostyles;
import net.sourceforge.plantuml.cucadiagram.Stereotag;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.utils.LineLocation;

public interface IEntity extends SpecificBackcolorable, Hideable, Removeable, LineConfigurable {

	public Code getCode();

	public String getCodeGetName();

	public Ident getIdent();

	public USymbol getUSymbol();

	public void setUSymbol(USymbol symbol);

	public LeafType getLeafType();

	public Display getDisplay();

	public IGroup getParentContainer();

	public void setDisplay(Display display);

	public String getUid();

	public Url getUrl99();

	public Stereotype getStereotype();

	public void setStereotype(Stereotype stereotype);

	public Bodier getBodier();

	public void addUrl(Url url);

	public boolean isGroup();

	public boolean hasUrl();

	public int getRawLayout();

	public void putTip(String member, Display display);

	public Map<String, Display> getTips();

	public void addStereotag(Stereotag tag);

	public Set<Stereotag> stereotags();

	public boolean isAloneAndUnlinked();

	public String getCodeLine();

	public void setCodeLine(LineLocation codeLine);

	public void setStereostyle(String stereo);

	public Stereostyles getStereostyles();

	public void addNote(Display note, Position position, Colors colors);

	public EntityPosition getEntityPosition();

	public List<CucaNote> getNotes(Position position);

}
