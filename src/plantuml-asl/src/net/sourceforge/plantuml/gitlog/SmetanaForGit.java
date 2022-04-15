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
package net.sourceforge.plantuml.gitlog;

import static gen.lib.cgraph.attr__c.agsafeset;
import static gen.lib.cgraph.edge__c.agedge;
import static gen.lib.cgraph.graph__c.agopen;
import static gen.lib.cgraph.node__c.agnode;
import static gen.lib.gvc.gvc__c.gvContext;
import static gen.lib.gvc.gvlayout__c.gvLayoutJobs;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import h.ST_Agedge_s;
import h.ST_Agedgeinfo_t;
import h.ST_Agnode_s;
import h.ST_Agnodeinfo_t;
import h.ST_Agraph_s;
import h.ST_GVC_s;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.jsondiagram.Mirror;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import smetana.core.CString;
import smetana.core.Macro;
import smetana.core.Z;

public class SmetanaForGit {

	private final UGraphic ug;
	private final ISkinParam skinParam;
	private int num;
	private ST_Agraph_s g;
	private StringBounder stringBounder;

	private final Map<GNode, ST_Agnode_s> nodes = new LinkedHashMap<GNode, ST_Agnode_s>();
	private final List<ST_Agedge_s> edges = new ArrayList<>();
	private Mirror yMirror;

	public SmetanaForGit(UGraphic ug, ISkinParam skinParam) {
		this.stringBounder = ug.getStringBounder();
		this.skinParam = skinParam;
		this.ug = getStyle().applyStrokeAndLineColor(ug, skinParam.getIHtmlColorSet(), skinParam.getThemeStyle());
	}

	private Style getStyle() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.gitDiagram)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
	}

	private HColor arrowColor() {
		return getStyle().value(PName.LineColor).asColor(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet());
	}

	public void drawMe(Collection<GNode> gnodes) {
		initGraph(gnodes);

		for (Entry<GNode, ST_Agnode_s> ent : nodes.entrySet()) {
			final ST_Agnode_s node = ent.getValue();
			final UTranslate pos = getPosition(node);

			final MagicBox magicBox = new MagicBox(skinParam, ent.getKey());
			magicBox.drawBorder(ug.apply(pos), getSize(node));
		}

		for (ST_Agedge_s edge : edges) {
			final ST_Agedgeinfo_t data = (ST_Agedgeinfo_t) Macro.AGDATA(edge);
			new GitCurve(data, yMirror).drawCurve(arrowColor(), ug);
		}
	}

	private Dimension2D getSize(ST_Agnode_s node) {
		final ST_Agnodeinfo_t data = (ST_Agnodeinfo_t) Macro.AGDATA(node);
		final double width = data.width * 72;
		final double height = data.height * 72;
		return new Dimension2DDouble(width, height);
	}

	private UTranslate getPosition(ST_Agnode_s node) {
		final ST_Agnodeinfo_t data = (ST_Agnodeinfo_t) Macro.AGDATA(node);
		final double width = data.width * 72;
		final double height = data.height * 72;
		final double x = data.coord.x;
		final double y = data.coord.y;
		return new UTranslate(x - width / 2, yMirror.inv(y + height / 2));
	}

	private void initGraph(Collection<GNode> gnodes) {
		if (g != null)
			return;

		Z.open();
		try {
			g = agopen(new CString("g"), Z.z().Agdirected, null);
			agsafeset(g, new CString("ranksep"), new CString("0.35"), new CString(""));
			for (GNode gnode : gnodes) {
				final Dimension2D dim = new MagicBox(skinParam, gnode).getBigDim(stringBounder);
				nodes.put(gnode, createNode(dim));
			}

			for (GNode gnode : nodes.keySet()) {
				for (GNode parent : gnode.getDowns()) {
					final ST_Agedge_s edge = createEdge(gnode, parent);
					if (edge != null)
						edges.add(edge);

				}
			}

			final ST_GVC_s gvc = gvContext();
			gvLayoutJobs(gvc, g);

			double max = 0;
			for (ST_Agnode_s node : nodes.values()) {
				final ST_Agnodeinfo_t data = (ST_Agnodeinfo_t) Macro.AGDATA(node);
				final double height = data.height * 72;
				final double y = data.coord.y;
				max = Math.max(max, y + height);
			}
			yMirror = new Mirror(max);

		} finally {
			Z.close();
		}

	}

	private ST_Agedge_s createEdge(GNode node0, GNode node1) {
		final ST_Agnode_s a0 = nodes.get(node0);
		final ST_Agnode_s a1 = nodes.get(node1);
		if (a0 == null || a1 == null) {
			return null;
		}

		final ST_Agedge_s edge = agedge(g, a0, a1, null, true);
		agsafeset(edge, new CString("arrowsize"), new CString(".7"), new CString(""));
		agsafeset(edge, new CString("arrowtail"), new CString("none"), new CString(""));
		agsafeset(edge, new CString("arrowhead"), new CString("normal"), new CString(""));
		return edge;
	}

	private ST_Agnode_s createNode(Dimension2D dim) {
		final String width = "" + (dim.getWidth() / 72);
		final String height = "" + (dim.getHeight() / 72);

		final ST_Agnode_s node = agnode(g, new CString("N" + num), true);
		node.NAME = "N " + num;
		num++;

		agsafeset(node, new CString("shape"), new CString("box"), new CString(""));
		agsafeset(node, new CString("height"), new CString("" + height), new CString(""));
		agsafeset(node, new CString("width"), new CString("" + width), new CString(""));

		return node;
	}

}
