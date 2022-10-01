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
package net.sourceforge.plantuml.nwdiag.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.plantuml.ComponentStyle;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.USymbols;
import net.sourceforge.plantuml.nwdiag.next.LinkedElement;
import net.sourceforge.plantuml.nwdiag.next.NBar;
import net.sourceforge.plantuml.skin.ActorStyle;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.PackageStyle;

public class NServer {

	private final Map<Network, String> connections = new LinkedHashMap<Network, String>();

	private USymbol shape = USymbols.RECTANGLE;
	private final String name;
	private String description;
	private final NBar bar;

	private boolean printFirstLink = true;

	public void doNotPrintFirstLink() {
		this.printFirstLink = false;
	}

	public final boolean printFirstLink() {
		return printFirstLink;
	}

	public Network getMainNetworkNext() {
		return connections.keySet().iterator().next();
	}

	public String getAdress(Network network) {
		return connections.get(network);
	}

	private TextBlock toTextBlock(String s, ISkinParam skinParam, SName sname) {
		if (s == null) 
			return null;
		
		if (s.length() == 0) 
			return TextBlockUtils.empty(0, 0);
		
		s = s.replace(", ", "\\n");
		return Display.getWithNewlines(s).create(getFontConfiguration(skinParam, sname), HorizontalAlignment.LEFT,
				skinParam);
	}

	private StyleSignatureBasic getStyleDefinition(SName sname) {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.nwdiagDiagram, sname);
	}

	private FontConfiguration getFontConfiguration(ISkinParam skinParam, SName sname) {
		final StyleBuilder styleBuilder = skinParam.getCurrentStyleBuilder();
		final Style style = getStyleDefinition(sname).getMergedStyle(styleBuilder);
		return style.getFontConfiguration(skinParam.getIHtmlColorSet());
	}

	public LinkedElement getLinkedElement(double topMargin, Map<Network, String> conns, List<Network> networks,
			ISkinParam skinParam) {
		final StyleBuilder styleBuilder = skinParam.getCurrentStyleBuilder();
		final SymbolContext symbolContext = getStyleDefinition(SName.server).getMergedStyle(styleBuilder)
				.getSymbolContext(skinParam.getIHtmlColorSet());

		final Map<Network, TextBlock> conns2 = new LinkedHashMap<Network, TextBlock>();
		for (Entry<Network, String> ent : conns.entrySet()) 
			conns2.put(ent.getKey(), toTextBlock(ent.getValue(), skinParam, SName.arrow));
		

		final TextBlock desc = toTextBlock(getDescription(), skinParam, SName.server);
		final TextBlock box = getShape().asSmall(TextBlockUtils.empty(0, 0), desc, TextBlockUtils.empty(0, 0),
				symbolContext, HorizontalAlignment.CENTER);
		return new LinkedElement(topMargin, this, box, conns2, networks);
	}

	public void connectTo(Network network) {
		connectTo(network, "");
	}

	public void connectTo(Network network, String address) {
		if (address == null)
			address = "";
		if (address.length() == 0 && connections.containsKey(network)) 
			return;
		
		connections.put(network, address);
		if (bar.getStart() == null)
			bar.addStage(network.getNstage());
		else if (this.getMainNetworkNext() != network)
			bar.addStage(network.getUp());
	}

	public void updateProperties(Map<String, String> props) {
		final String description = props.get("description");
		if (description != null) 
			this.setDescription(description);
		
		final String shape = props.get("shape");
		if (shape != null) 
			this.setShape(shape);
		
	}

	@Override
	public final String toString() {
		return name;
	}

	public static NServer create(String name) {
		return new NServer(name, new NBar());
	}

	public NServer(String name, NBar bar) {
		this.description = name;
		this.name = name;
		this.bar = bar;
	}

	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final String getName() {
		return name;
	}

	public final void setShape(String shapeName) {
		final USymbol shapeFromString = USymbols.fromString(shapeName, ActorStyle.STICKMAN, ComponentStyle.RECTANGLE,
				PackageStyle.RECTANGLE);
		if (shapeFromString != null) {
			this.shape = shapeFromString;
		}
	}

	public final USymbol getShape() {
		return shape;
	}

	public final NBar getBar() {
		return bar;
	}

}
