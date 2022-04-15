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
package net.sourceforge.plantuml.sequencediagram.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlMode;
import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.classdiagram.command.CommandLinkClass;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.descdiagram.command.CommandLinkElement;
import net.sourceforge.plantuml.sequencediagram.LifeEventType;
import net.sourceforge.plantuml.sequencediagram.Message;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.skin.ArrowBody;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.ArrowDecoration;
import net.sourceforge.plantuml.skin.ArrowHead;
import net.sourceforge.plantuml.skin.ArrowPart;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandArrow extends SingleLineCommand2<SequenceDiagram> {

	static final String ANCHOR = "(\\{([%pLN_]+)\\}[%s]+)?";

	public CommandArrow() {
		super(getRegexConcat());
	}

	public static String getColorOrStylePattern() {
		return "(?:\\[(" + CommandLinkElement.LINE_STYLE + ")\\])?";
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandArrow.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("PARALLEL", "(&[%s]*)?"), //
				new RegexLeaf("ANCHOR", ANCHOR), //
				new RegexOr("PART1", //
						new RegexLeaf("PART1CODE", "([%pLN_.@]+)"), //
						new RegexLeaf("PART1LONG", "[%g]([^%g]+)[%g]"), //
						new RegexLeaf("PART1LONGCODE", "[%g]([^%g]+)[%g][%s]*as[%s]+([%pLN_.@]+)"), //
						new RegexLeaf("PART1CODELONG", "([%pLN_.@]+)[%s]+as[%s]*[%g]([^%g]+)[%g]")), //
				new RegexLeaf("PART1ANCHOR", ANCHOR), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("ARROW_DRESSING1",
						"([%s][ox]|(?:[%s][ox])?<<?_?|(?:[%s][ox])?//?|(?:[%s][ox])?\\\\\\\\?)?"), //
				new RegexOr(new RegexConcat( //
						new RegexLeaf("ARROW_BODYA1", "(-+)"), //
						new RegexLeaf("ARROW_STYLE1", getColorOrStylePattern()), //
						new RegexLeaf("ARROW_BODYB1", "(-*)")), //
						new RegexConcat( //
								new RegexLeaf("ARROW_BODYA2", "(-*)"), //
								new RegexLeaf("ARROW_STYLE2", getColorOrStylePattern()), //
								new RegexLeaf("ARROW_BODYB2", "(-+)"))), //
				new RegexLeaf("ARROW_DRESSING2",
						"(_?>>?(?:[ox][%s])?|//?(?:[ox][%s])?|\\\\\\\\?(?:[ox][%s])?|[ox][%s])?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOr("PART2", //
						new RegexLeaf("PART2CODE", "([%pLN_.@]+)"), //
						new RegexLeaf("PART2LONG", "[%g]([^%g]+)[%g]"), //
						new RegexLeaf("PART2LONGCODE", "[%g]([^%g]+)[%g][%s]*as[%s]+([%pLN_.@]+)"), //
						new RegexLeaf("PART2CODELONG", "([%pLN_.@]+)[%s]+as[%s]*[%g]([^%g]+)[%g]")), //
				new RegexLeaf("MULTICAST", "((?:\\s&\\s[%pLN_.@]+)*)"), //
				new RegexLeaf("PART2ANCHOR", ANCHOR), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("ACTIVATION", "(?:(\\+\\+|\\*\\*|!!|--|--\\+\\+|\\+\\+--)?)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("LIFECOLOR", "(?:(#\\w+)?)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREOTYPE", "(\\<\\<.*\\>\\>)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("MESSAGE", "(?::[%s]*(.*))?"), //
				RegexLeaf.end()).protectSize(2000);
	}

	private List<Participant> getMulticasts(SequenceDiagram system, RegexResult arg2) {
		final String multicast = arg2.get("MULTICAST", 0);
		if (multicast != null) {
			final List<Participant> result = new ArrayList<>();
			for (String s : multicast.split("&")) {
				s = s.trim();
				if (s.length() == 0)
					continue;

				final Participant participant = system.getOrCreateParticipant(s);
				if (participant != null)
					result.add(participant);

			}
			return Collections.unmodifiableList(result);
		}
		return Collections.emptyList();
	}

	private Participant getOrCreateParticipant(SequenceDiagram system, RegexResult arg2, String n) {
		final String code;
		final Display display;
		if (arg2.get(n + "CODE", 0) != null) {
			code = arg2.get(n + "CODE", 0);
			display = Display.getWithNewlines(code);
		} else if (arg2.get(n + "LONG", 0) != null) {
			code = arg2.get(n + "LONG", 0);
			display = Display.getWithNewlines(code);
		} else if (arg2.get(n + "LONGCODE", 0) != null) {
			display = Display.getWithNewlines(arg2.get(n + "LONGCODE", 0));
			code = arg2.get(n + "LONGCODE", 1);
		} else if (arg2.get(n + "CODELONG", 0) != null) {
			code = arg2.get(n + "CODELONG", 0);
			display = Display.getWithNewlines(arg2.get(n + "CODELONG", 1));
			return system.getOrCreateParticipant(code, display);
		} else {
			throw new IllegalStateException();
		}
		return system.getOrCreateParticipant(code, display);
	}

	private boolean contains(String string, String... totest) {
		for (String t : totest)
			if (string.contains(t))
				return true;

		return false;
	}

	private String getDressing(RegexResult arg, String key) {
		String value = arg.get(key, 0);
		value = CommandLinkClass.notNull(value);
		value = value.replace("_", "");
		return StringUtils.goLowerCase(value);
	}

	@Override
	protected CommandExecutionResult executeArg(SequenceDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {

		Participant p1;
		Participant p2;

		final String dressing1 = getDressing(arg, "ARROW_DRESSING1");
		final String dressing2 = getDressing(arg, "ARROW_DRESSING2");

		final boolean circleAtStart;
		final boolean circleAtEnd;

		final boolean hasDressing2 = contains(dressing2, ">", "\\", "/", "x");
		final boolean hasDressing1 = contains(dressing1, "x", "<", "\\", "/");
		final boolean reverseDefine;
		if (hasDressing2) {
			p1 = getOrCreateParticipant(diagram, arg, "PART1");
			p2 = getOrCreateParticipant(diagram, arg, "PART2");
			circleAtStart = dressing1.contains("o");
			circleAtEnd = dressing2.contains("o");
			reverseDefine = false;
		} else if (hasDressing1) {
			p2 = getOrCreateParticipant(diagram, arg, "PART1");
			p1 = getOrCreateParticipant(diagram, arg, "PART2");
			circleAtStart = dressing2.contains("o");
			circleAtEnd = dressing1.contains("o");
			reverseDefine = true;
		} else {
			return CommandExecutionResult.error("Illegal sequence arrow");

		}

		final boolean sync = contains(dressing1, "<<", "\\\\", "//") || contains(dressing2, ">>", "\\\\", "//");

		final boolean dotted = getLength(arg) > 1;

		final Display labels;
		if (arg.get("MESSAGE", 0) == null) {
			labels = Display.create("");
		} else {
			// final String message = UrlBuilder.multilineTooltip(arg.get("MESSAGE", 0));
			final String message = arg.get("MESSAGE", 0);
			labels = Display.getWithNewlines(message);
		}

		ArrowConfiguration config = hasDressing1 && hasDressing2 ? ArrowConfiguration.withDirectionBoth()
				: ArrowConfiguration.withDirectionNormal();
		if (dotted)
			config = config.withBody(ArrowBody.DOTTED);

		if (sync)
			config = config.withHead(ArrowHead.ASYNC);

		if (dressing2.contains("\\") || dressing1.contains("/"))
			config = config.withPart(ArrowPart.TOP_PART);

		if (dressing2.contains("/") || dressing1.contains("\\"))
			config = config.withPart(ArrowPart.BOTTOM_PART);

		if (circleAtEnd)
			config = config.withDecoration2(ArrowDecoration.CIRCLE);

		if (circleAtStart)
			config = config.withDecoration1(ArrowDecoration.CIRCLE);

		if (reverseDefine) {
			if (dressing1.contains("x"))
				config = config.withHead2(ArrowHead.CROSSX);

			if (dressing2.contains("x"))
				config = config.withHead1(ArrowHead.CROSSX);

		} else {
			if (dressing1.contains("x"))
				config = config.withHead1(ArrowHead.CROSSX);

			if (dressing2.contains("x"))
				config = config.withHead2(ArrowHead.CROSSX);

		}
		if (reverseDefine)
			config = config.reverseDefine();

		config = applyStyle(diagram.getSkinParam().getThemeStyle(), arg.getLazzy("ARROW_STYLE", 0), config);

		final String activationSpec = arg.get("ACTIVATION", 0);

		if (activationSpec != null && activationSpec.charAt(0) == '*')
			diagram.activate(p2, LifeEventType.CREATE, null);

		final String messageNumber = diagram.getNextMessageNumber();
		final Message msg = new Message(diagram.getSkinParam().getCurrentStyleBuilder(), p1, p2,
				diagram.manageVariable(labels), config, messageNumber);
		msg.setMulticast(getMulticasts(diagram, arg));
		final String url = arg.get("URL", 0);
		if (url != null) {
			final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), UrlMode.STRICT);
			final Url urlLink = urlBuilder.getUrl(url);
			msg.setUrl(urlLink);
		}

		if (arg.get("STEREOTYPE", 0) != null) {
			final Stereotype stereotype = Stereotype.build(arg.get("STEREOTYPE", 0));
			msg.getStereotype(stereotype);
		}

		final boolean parallel = arg.get("PARALLEL", 0) != null;
		if (parallel)
			msg.goParallel();

		msg.setAnchor(arg.get("ANCHOR", 1));
		msg.setPart1Anchor(arg.get("PART1ANCHOR", 1));
		msg.setPart2Anchor(arg.get("PART2ANCHOR", 1));

		final String error = diagram.addMessage(msg);
		if (error != null)
			return CommandExecutionResult.error(error);

		final String s = arg.get("LIFECOLOR", 0);

		final HColor activationColor = s == null ? null
				: diagram.getSkinParam().getIHtmlColorSet().getColor(diagram.getSkinParam().getThemeStyle(), s);

		if (activationSpec != null)
			return manageActivations(activationSpec, diagram, p1, p2, activationColor);

		if (diagram.isAutoactivate() && (config.getHead() == ArrowHead.NORMAL || config.getHead() == ArrowHead.ASYNC))
			if (config.isDotted())
				diagram.activate(p1, LifeEventType.DEACTIVATE, null);
			else
				diagram.activate(p2, LifeEventType.ACTIVATE, activationColor);

		return CommandExecutionResult.ok();
	}

	private CommandExecutionResult manageActivations(String spec, SequenceDiagram diagram, Participant p1,
			Participant p2, HColor activationColor) {
		switch (spec.charAt(0)) {
		case '+':
			diagram.activate(p2, LifeEventType.ACTIVATE, activationColor);
			break;
		case '-':
			diagram.activate(p1, LifeEventType.DEACTIVATE, null);
			break;
		case '!':
			diagram.activate(p2, LifeEventType.DESTROY, null);
			break;
		}
		if (spec.length() == 4) {
			switch (spec.charAt(2)) {
			case '+':
				diagram.activate(p2, LifeEventType.ACTIVATE, activationColor);
				break;
			case '-':
				diagram.activate(p1, LifeEventType.DEACTIVATE, null);
				break;
			}
		}
		return CommandExecutionResult.ok();
	}

	private int getLength(RegexResult arg2) {
		String sa = arg2.getLazzy("ARROW_BODYA", 0);
		if (sa == null)
			sa = "";

		String sb = arg2.getLazzy("ARROW_BODYB", 0);
		if (sb == null)
			sb = "";

		return sa.length() + sb.length();
	}

	public static ArrowConfiguration applyStyle(ThemeStyle themeStyle, String arrowStyle, ArrowConfiguration config)
			throws NoSuchColorException {
		if (arrowStyle == null)
			return config;

		final StringTokenizer st = new StringTokenizer(arrowStyle, ",");
		while (st.hasMoreTokens()) {
			final String s = st.nextToken();
			if (s.equalsIgnoreCase("dashed")) {
				config = config.withBody(ArrowBody.DOTTED);
			} else if (s.equalsIgnoreCase("bold")) {
			} else if (s.equalsIgnoreCase("dotted")) {
				config = config.withBody(ArrowBody.DOTTED);
			} else if (s.equalsIgnoreCase("hidden")) {
				config = config.withBody(ArrowBody.HIDDEN);
			} else {
				config = config.withColor(HColorSet.instance().getColor(themeStyle, s));
			}
		}
		return config;
	}

}
