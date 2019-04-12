

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestString {
	/** ���ʹ�õĻ�����Ҫ���±���������ʽ�ˣ�����Ƶ�����������Ч�� */
	public static final String patternString1 = "[^\\s]*((<\\s*[aA]\\s+(href\\s*=[^>]+\\s*)>)(.*)</[aA]>).*";
	public static final String patternString2 = ".*(<\\s*[aA]\\s+(href\\s*=[^>]+\\s*)>(.*)</[aA]>).*";
	public static final String patternString3 = ".*href\\s*=\\s*(\"|'|)http://.*";
	public static Pattern pattern1 = Pattern.compile(patternString1,
			Pattern.DOTALL);
	public static Pattern pattern2 = Pattern.compile(patternString2,
			Pattern.DOTALL);
	public static Pattern pattern3 = Pattern.compile(patternString3,
			Pattern.DOTALL);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/** ���Ե����� */
		String ss = "���ǲ���<a href=http://www.google.cn>www.google.cn</a>����ǲ�����";
		/** ������ȡ������url,��set��ĳ�̶ֳ�ȥ�أ�ֻ�������ϣ����������Ǿ�Ҫ��Ҫ���Ǻܶ��� */
		Set<String> set = new HashSet<String>();
		/** ����url��������set�� */
		parseUrl(set, ss);
		/** ��Խ���������url������ */
		System.out.println(replaceHtml(set, ss));

	}

	/** ��ÿ��url����target���� */
	public static String replaceHtml(Set<String> set, String var) {
		String result = null;
		/** ��ò�Ҫ�Բ����޸� */
		result = var;
		Iterator<String> ite = set.iterator();
		while (ite.hasNext()) {
			String url = ite.next();
			if (url != null) {
				result = result.replaceAll(url, url + "  target=\"_blank\"");

			}
		}
		return result;
	}

	public static void parseUrl(Set<String> set, String var) {
		Matcher matcher = null;
		String result = null;
		// ������̵�a��ǩ����Ϊ <a href=http://www.a.cn></a>��������ĳ���Ϊ28
		if (var != null && var.length() > 28) {
			matcher = pattern3.matcher(var);
			// ȷ�����������������
			if (matcher != null && matcher.matches()) {
				matcher = pattern1.matcher(var);
				String aString = null;
				String bString = null;

				while (matcher != null && matcher.find()) {
					if (matcher.groupCount() > 3) {
						bString = matcher.group(matcher.groupCount() - 3);// ���group�������з���������ַ���
						aString = matcher.group(matcher.groupCount() - 2);// ���group����url��html��ǩ
						String url1 = matcher.group(matcher.groupCount() - 1);// ���һ��group����url
						set.add(url1);// ���ҵ���url��������
						bString = bString.replaceAll(aString, "");// ȥ���Ѿ��ҵ���url��html��ǩ
					}

				}
				if (bString != null) {
					parseUrl(set, bString);// ����ѭ����ȡ��һ��url
				}

			}
		}

	}

}
