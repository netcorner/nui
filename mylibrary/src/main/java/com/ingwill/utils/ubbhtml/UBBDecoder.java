/**
 * 
 */
package com.ingwill.utils.ubbhtml;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * UBB解码类
 * @author liudong
 */
public class UBBDecoder {

	public static final int MODE_IGNORE = 0;
	public static final int MODE_CLOSE = 1;

	private static final int SEARCH_LEN = 200;

	/**
	 * 进行UBB标签的转换
	 * @param s 需要转换的包含UBB标签的文本
	 * @param th 用户自定义的UBB标签的处理器的实例
	 * @param mode 容错模式，可以是忽略模式(MODE_IGNORE)或关闭模式(MODE_CLOSE)
	 * @return 转换后的包含HTML标签的文本
	 */
	public static final String decode(String s, UBBTagHandler th, int mode) {
		return decode(s, th, mode, false);
	}

	public static final String decode(String s) {
		return UBBDecoder.decode(s, new SimpleTagHandler(),
				UBBDecoder.MODE_CLOSE);
	}
	/**
	 * 进行UBB标签的转换
	 * @param s 需要转换的包含UBB标签的文本
	 * @param th 用户自定义的UBB标签的处理器的实例
	 * @param mode 容错模式，可以是忽略模式(MODE_IGNORE)或关闭模式(MODE_CLOSE)
	 * @param convBr 是否把'\n'字符也转换为'<br>'
	 * @return 转换后的包含HTML标签的文本
	 */
	public static final String decode(String s, UBBTagHandler th, int mode,
									  boolean convBr) {
		StringBuffer buf = new StringBuffer(); // 当前文本
		char[] cc = s.toCharArray(); // 把输入转换为字符数组以提高处理性能
		int len = cc.length, pos = 0;
		UBBNode root = new UBBNode(null, "", null, "", false); // 根节点
		UBBNode node = root; // 当前节点
		Stack<UBBNode> stk = new Stack<UBBNode>(); // 使用堆栈处理节点的嵌套
		stk.push(node);
		while (pos < len) { // 只要未到文件末尾就循环
			char cur = cc[pos]; // 当前字符
			if (convBr && cur == '\n') { // 如果当前字符是换行
				buf.append("\n<br />");
				pos++;
				continue;
			}
			if (convBr && cur == '\t') { // 如果当前字符是制表符
				buf.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				pos++;
				continue;
			}
			if (convBr && cur == ' ') { // 如果当前字符是空字符
				buf.append("&nbsp;");
				pos++;
				continue;
			}
			if (cur != '[') { // 只要当前字符不是'['就扩展到当前文本
				buf.append(cur);
				pos++;
				continue;
			}
			// 如果当前字符是'['，那么查找下一个']'
			int ii = indexOf(cc, ']', pos + 1, SEARCH_LEN);
			if (ii == -1) { // 未找到，把当前'['作为一般字符处理，扩展到当前文本
				buf.append(cur);
				pos++;
				continue;
			}
			if (cc[pos + 1] == '/') { // 标签以'/'起始，可能是结束标签
				if (cc[pos + 2] == ']') { // 修正littlebat发现的bug，处理"[/]"的情况
					buf.append("[/]");
					pos += 3;
					continue;
				}
				// 得到结束标签的文本
				String tmp = new String(cc, pos + 2, ii - pos - 2).trim()
						.toLowerCase();
				int cnt = 1;
				boolean find = false;
				// 在节点树上向上查找和本结束标签对应的标签
				for (UBBNode nd = node; nd != null; nd = nd.parent, cnt++) {
					if (nd.tag.equals(tmp)) {
						find = true;
						break;
					}
				}
				if (find) { // 如果在节点树上找到了和本结束标签对应的标签
					// 先把当前文本扩展到当前节点
					addTextChild(node, buf);
					// 从堆栈中弹出节点，直到找到的标签成为当前节点
					while (cnt-- > 0) {
						// 对于CLOSE容错模式，把当前节点和找到的标签节点之间的标签全部关闭
						if (mode == MODE_CLOSE) {
							node.closed = true;
						}
						node = (UBBNode) stk.pop();
					}
					// 关闭当前标签节点，当前节点上移一层
					node.closed = true;
					node = node.parent;
					pos = ii + 1;
					continue;
				} else { // 未找到对应起始标签，作为普通文本处理
					buf.append("[/");
					pos += 2;
					continue;
				}
			} else if (cc[ii - 1] == '/') { // 标签以'/'结尾，可能是空标签
				String tmp = new String(cc, pos + 1, ii - pos - 2).trim();
				// 由UBBTagHandler决定是否是一个合法空标签
				String[] ss = th.parseTag(tmp, true);
				if (ss != null && ss.length == 3) { // 处理空标签
					// 先把当前文本扩展到当前节点
					addTextChild(node, buf);
					UBBNode nd = new UBBNode(node, ss[0].toLowerCase(), ss[1]
							.split(","), new String(cc, pos, ii + 1 - pos),
							true);
					node.addChild(nd);
					pos = ii + 1;
					continue;
				}
			}
			// 可能是普通起始标签
			// 得到标签文本
			String tmp = new String(cc, pos + 1, ii - pos - 1).trim();
			// 由UBBTagHandler决定是否是合法标签
			String[] ss = th.parseTag(tmp, false);
			if (ss != null && ss.length == 2) { // 是合法标签
				// 先把当前文本扩展到当前节点
				addTextChild(node, buf);
				// 创建新的节点，扩展到当前节点，然后当前节点下移一层
				UBBNode nd = new UBBNode(node, ss[0].toLowerCase(), ss[1]
						.split(","), new String(cc, pos, ii + 1 - pos), false);
				node.addChild(nd);
				pos = ii + 1;
				stk.push(nd);
				node = nd;
			} else { // 不是标签，当作普通文本处理
				buf.append('[');
				pos++;
			}
		}
		// 把当前文本中剩余的内容扩展到当前节点
		addTextChild(node, buf);
		//System.out.println("=========================\n" + root.toString(0));
		// 使用节点树构造输出文本
		String show = decodeNode(th, root);
		show = show.replaceAll("\\[br\\]", "\n<br />");
		return show.replaceAll("\\[em:(.+?)\\]","<img src='/image/emote/em$1.gif' style=\"border:0\">");
	}

	/**
	 * 把文本生成一个纯文本节点并扩展到给定的节点
	 * @param node
	 * @param buf
	 */
	private static void addTextChild(UBBNode node, StringBuffer buf) {
		if (buf.length() > 0) {
			node.addChild(new UBBNode(node, buf.toString()));
			buf.setLength(0);
		}
	}
	/**
	 * 从标签节点树来递归的构造输出文本
	 * @param th
	 * @param node
	 * @return
	 */
	private static String decodeNode(UBBTagHandler th, UBBNode node) {
		StringBuffer buf = new StringBuffer(); // 输出文本
		if (UBBNode.TEXT == node.tag) {
			// 处理纯文本节点
			buf.append(node.img);
		} else if (!node.closed) {
			// 处理未正常关闭的节点，节点本身当作纯文本处理，对其子节点进行递归处理
			buf.append(node.img);
			List<UBBNode> lst = node.children;
			if (lst != null && lst.size() > 0) {
				for (int i = 0, n = lst.size(); i < n; i++) {
					buf.append(decodeNode(th, (UBBNode) lst.get(i)));
				}
			}
		} else {
			// 处理正常节点，使用UBBTagHandler来组合输出，并递归处理其子节点
			List<UBBNode> lst = node.children;
			StringBuffer tmp = new StringBuffer();
			if (lst != null && lst.size() > 0) {
				for (int i = 0, n = lst.size(); i < n; i++) {
					tmp.append(decodeNode(th, (UBBNode) lst.get(i)));
				}
			}
			buf.append(th.compose(node.tag, node.attr, tmp.toString(),
					node.isEmpty));
		}
		return buf.toString();
	}
	private static final int indexOf(char[] cc, char c, int idx, int len) {
		int end = idx + len;
		if (end > cc.length)
			end = cc.length;
		for (int i = idx; i < end; i++) {
			if (cc[i] == c) {
				return i;
			}
		}
		return -1;
	}

}

class UBBNode {
	static final String TEXT = "<text>";
	
	String tag = null;
	String[] attr = null;
	String img = null;
	UBBNode parent = null;
	List<UBBNode> children = null;
	boolean closed = false;
	boolean isEmpty = false;

	UBBNode(UBBNode parent, String tag, String[] attr, String img,
			boolean isEmpty) {
		this.parent = parent;
		this.tag = tag.toLowerCase();
		this.attr = attr;
		this.img = img;
		this.isEmpty = isEmpty;
		this.closed = isEmpty;
		this.children = isEmpty ? null : new ArrayList<UBBNode>();
	}

	UBBNode(UBBNode parent, String text) {
		String[] text1 = { text };
		this.parent = parent;
		this.tag = TEXT;
		this.attr = text1;
		this.img = text;
		this.closed = true;
		this.isEmpty = true;
	}

	final void addChild(UBBNode child) {
		children.add(child);
	}

	/**
	 * 
	 */
	public final String toString() {
		return "[tag=\"" + tag + "\",attr=\"" + attr + "\",closed=" + closed
				+ ",children="
				+ (children != null ? "" + children.size() : "null") + "]";
	}

	/**
	 *
	 * @param i
	 * @return
	 */
	final String toString(int i) {
		StringBuffer buf = new StringBuffer();
		for (int j = i; --j >= 0;) {
			buf.append(' ');
		}
		buf.append(toString() + "\n");
		if (children != null && children.size() > 0) {
			for (int j = 0, n = children.size(); j < n; j++) {
				buf.append(((UBBNode) children.get(j)).toString(i + 2));
			}
		}
		return buf.toString();
	}

}

class SimpleTagHandler implements UBBTagHandler {
	//    [b]文字加粗体效果[/b]
	//    [i]文字加倾斜效果[/i]
	//    [u]文字加下划线效果[/u]
	//    [size=4]改变文字大小[/size]
	//    [color=red]改变文字颜色[/color]
	//    [quote]这个标签是用来做为引用所设置的，如果你有什么内容是引用自别的地方，请加上这个标签！[/quote]  
	//    [url]http://www.cnjm.net[/url]
	//    [url=http://www.cnjm.net]JAVA手机网[/url]
	//    [email=webmaster@cnjm.net]写信给我[/email] 
	//    [email]webmaster@cnjm.net[/email]
	//    [img]http://www.cnjm.net/myimages/mainlogo.gif[/img]
	public SimpleTagHandler() {	}

	public String[] parseTag(String s, boolean isEmpty) {
		if (isEmpty) { // 本处理器不支持空标签
			return null;
		}
		// 如果标签中有'='号就把标签分为UBB标记和属性两部分，否则属性为空
		String tag = s, attr = "";
		int idx = s.indexOf('=');
		if (idx >= 0) {
			tag = s.substring(0, idx);
			attr = s.substring(idx + 1);
		} else {
			idx = s.indexOf(':');
			if (idx >= 0) {
				tag = s.substring(0, idx);
				attr = s.substring(idx + 1);
			}
		}
		String tmp = tag.toLowerCase(); // 大小写不敏感
		// 只有下面的标记是本处理器支持的
		if ("b".equals(tmp) || "i".equals(tmp) || "u".equals(tmp)
				|| "size".equals(tmp) || "color".equals(tmp)
				|| "quote".equals(tmp) || "url".equals(tmp)
				|| "email".equals(tmp) || "img".equals(tmp) || "*".equals(tmp)
				|| "list".equals(tmp) || "ic_fly_black".equals(tmp)
				|| "move".equals(tmp) || "align".equals(tmp)
				|| "flash".equals(tmp) || "wmv".equals(tmp) || "rm".equals(tmp)
				|| "code".equals(tmp) || "glow".equals(tmp)
				|| "sign".equals(tmp) || "em".equals(tmp)) {
			return new String[] { tag, attr };

		}
		// 不是一个合法的UBB标签，作为普通文本处理
		return null;
	}

	public String compose(String tag, String[] attr, String data,
						  boolean isEmpty) {
		// 针对不同标记进行组合工作
		String tmp = tag;
		String style = null;
		if ("b".equals(tmp) || "i".equals(tmp) || "u".equals(tmp)) {
			return "<" + tag + ">" + data + "</" + tag + ">";
		} else if ("size".equals(tmp) || "color".equals(tmp)) {
			return "<font " + tag + "='" + attr[0] + "'>" + data + "</font>";
		} else if ("quote".equals(tmp)) {
			return "<div class=\"clearfix\">&nbsp;</div><div class='quote'><div class='fheader_fillet'><div class='fheader_fillet1'><div class='fheader_fillet2'></div></div></div><div class='quotemain'><div class='quotemain1'><div class='quotemain2'>"
					+ data
					+ "</div></div></div><div class='ffooter_fillet'><div class='ffooter_fillet1'><div class='ffooter_fillet2'></div></div></div></div>";
		} else if ("url".equals(tmp)) {
			String link = attr[0] != "" ? attr[0] : data;
			String domain;
			link.trim();
			String code = link.substring(0, 1);
			if (code.equals("#") == true) {
				return "<a href='" + link + "'>" + data + "</a>";
			} else {
				int index = link.indexOf('/');
				if (index < 0) {
					link = "http://" + link;
				} else {
					String head = link.substring(0, index);
					if ((head.equals("http:") == false)
							&& (head.equals("ftp:") == false)
							&& (head.equals("gopher:") == false)
							&& (head.equals("news:") == false)
							&& (head.equals("telnet:") == false)
							&& (head.equals("mms:") == false)
							&& (head.equals("rtsp:") == false))
						link = "http://" + link;
				}
				try {
					domain = link.substring((link.indexOf('.') + 1));
					domain = domain.substring(0, (domain.indexOf('.')));
				} catch (Exception ex) {
					domain = "";
				}
				if (domain.equals("moabc"))
					return "<a href='" + link + "' target=_blank>" + data+ "</a>";
				else
					return "<a href='/wap/out?url="+ link + "' target=_blank>" + data + "</a>";
			}
		} else if ("email".equals(tmp)) {
			String email = attr[0] != "" ? attr[0] : data;
			return "<a href='mailto:" + email + "'>" + data + "</a>";
		} else if ("img".equals(tmp)) {
			return "<img src='" + data + "' border=0>";
		} else if ("*".equals(tmp)) {
			return "<li>" + data + "</li>";
		} else if ("list".equals(tmp)) {
			return "<ul>" + data + "</ul>";
		} else if ("ic_fly_black".equals(tmp)) {
			return "<marquee width=90% behavior=alternate scrollamount=3>"
					+ data + "</marquee>";
		} else if ("move".equals(tmp)) {
			return "<marquee scrollamount=3>" + data + "</marquee>";
		} else if ("align".equals(tmp)) {
			style = attr[0] != "" ? attr[0] : "left";
			return "<div align='" + style + "'>" + data + "</div>";
		} else if ("sign".equals(tmp)) {
			return "<a name='" + attr[0] + "'>" + data + "</a>";
		} else if ("flash".equals(tmp)) {
			String height;
			String width;
			try {
				height = attr[1];
				width = attr[0];
			} catch (Exception ex) {
				height = String.valueOf(360);
				width = String.valueOf(480);
			}
			return "<OBJECT CLASSID=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" WIDTH="
					+ width
					+ " HEIGHT="
					+ height
					+ "><PARAM NAME=MOVIE VALUE='"
					+ data
					+ "'><PARAM NAME=PLAY VALUE=TRUE><PARAM NAME=LOOP VALUE=TRUE><PARAM NAME=QUALITY VALUE=HIGH><EMBED SRC='"
					+ data
					+ "' WIDTH="
					+ width
					+ " HEIGHT="
					+ height
					+ " PLAY=TRUE LOOP=TRUE QUALITY=HIGH></EMBED></OBJECT><br />[<a target=_blank href='"
					+ data + "'>全屏播放</a>]";
		} else if ("wmv".equals(tmp)) {
			return "<EMBED src='" + data
					+ "' HEIGHT=\"256\" WIDTH=\"314\" AutoStart=1></EMBED>";
		} else if ("rm".equals(tmp)) {
			return "<object classid=clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA height=241 id=Player width=316 VIEWASTEXT><param name=\"_ExtentX\" value=\"12726\"><param name=\"_ExtentY\" value=\"8520\"><param name=\"AUTOSTART\" value=\"0\"><param name=\"SHUFFLE\" value=\"0\"><param name=\"PREFETCH\" value=\"0\"><param name=\"NOLABELS\" value=\"0\"><param name=\"CONTROLS\" value=\"ImageWindow\"><param name=\"CONSOLE\" value=\"_master\"><param name=\"LOOP\" value=\"0\"><param name=\"NUMLOOP\" value=\"0\"><param name=\"CENTER\" value=\"0\"><param name=\"MAINTAINASPECT\" value=\""
					+ data
					+ "\"><param name=\"BACKGROUNDCOLOR\" value=\"#000000\"></object><br /><object classid=clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA height=32 id=Player width=316 VIEWASTEXT><param name=\"_ExtentX\" value=\"18256\"><param name=\"_ExtentY\" value=\"794\"><param name=\"AUTOSTART\" value=\"1\"><param name=\"SHUFFLE\" value=\"0\"><param name=\"PREFETCH\" value=\"0\"><param name=\"NOLABELS\" value=\"0\"><param name=\"CONTROLS\" value=\"controlpanel\"><param name=\"CONSOLE\" value=\"_master\"><param name=\"LOOP\" value=\"0\"><param name=\"NUMLOOP\" value=\"0\"><param name=\"CENTER\" value=\"0\"><param name=\"MAINTAINASPECT\" value=\"0\"><param name=\"BACKGROUNDCOLOR\" value=\"#000000\"><param name=\"SRC\" value=\""
					+ data + "\"></object>";
		} else if ("em".equals(tmp)) {
			style = attr[0] != "" ? attr[0] : "1";
			return "<img src='../ico/em" + attr[0]
					+ ".gif' style=\"border:0\" >";
		} else if ("glow".equals(tmp)) {
			String glowwidth, glowcolor, glowstrength;
			try {
				glowwidth = attr[0];
			} catch (Exception ex) {
				glowwidth = String.valueOf(360);
			}
			try {
				glowcolor = attr[1];
			} catch (Exception ex) {
				glowcolor = String.valueOf(360);
			}
			try {
				glowstrength = attr[2];
			} catch (Exception ex) {
				glowstrength = String.valueOf(360);
			}
			return "<span style='WIDTH:" + glowwidth + ";filter:glow(color="
					+ glowcolor + ", strength=" + glowstrength + ")'>" + data
					+ "</span>";
		} else if ("code".equals(tmp)) {
			Random random = new Random();
			int x = random.nextInt(9999);
			String s = String.valueOf(x);
			//System.out.println("data: \n" + data);
			//int index = link.indexOf('/');
			return "<div class=\"clearfix\">&nbsp;</div><div class=\"somecode\"><div class=\"somecode_main\"><div class=\"fheader_fillet\"><div class=\"fheader_fillet1\"><div class=\"fheader_fillet2\"></div></div></div><div class=\"body\"><div class=\"code_header\"><input type=\"button\" value=\"复制代码\" onclick=\"getcode('code-content"
					+ s
					+ "')\" class=\"btn\"/></div><div class=\"code_tools\"><a class=\"c_to\" id=\"c_to"
					+ s
					+ "\" onclick=\"showsomecode('"
					+ s
					+ "')\">&nbsp;</a></div><div class=\"code-content\" id=\"code-content"
					+ s
					+ "\">"
					+ data
					+ "</div><div id=\"code_footer"
					+ s
					+ "\"><div class=\"code_footer\"><input type=\"button\" value=\"复制代码\" onclick=\"getcode('code-content"
					+ s
					+ "')\" class=\"btn\"/></div><div class=\"code_tools\"><a class=\"c_to\" id=\"c_to\" onclick=\"showsomecode('"
					+ s
					+ "')\">&nbsp;</a></div></div></div><!-- body --><div class=\"ffooter_fillet\"><div class=\"ffooter_fillet1\"><div class=\"ffooter_fillet2\"></div></div></div></div><!-- somecode_main --><div class=\"clearfix\">&nbsp;</div></div>";
		}
		return data;
	}

	// 测试代码，可以运行这个类，并把包含UBB标签的文本作为参数传入来测试
	// 比如java util.SimpleTagHandler "[color=red]你[color=blue]好[/color]啊[/color]"
	public static void main(String[] args) throws Exception {
		String ubb = "[color=red]你[color=blue]好[/color]啊  &&& [color=red]你<font color='blue'>好</font>啊";
		System.out.println(">>>>" + ubb);
		// 下面采用了忽略模式来容错，你也可以用MODE_CLOSE试验一下关闭模式的容错效果
		System.out.println("=========================\n"
				+ UBBDecoder.decode(ubb, new SimpleTagHandler(),
						UBBDecoder.MODE_CLOSE));
	}

}
