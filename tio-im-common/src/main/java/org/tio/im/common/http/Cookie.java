package org.tio.im.common.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 部分代码参考了: https://github.com/helyho/Voovan
 * @author tanyaowu 
 * 2017年5月29日 上午7:45:58
 */
public class Cookie {
	private static Logger log = LoggerFactory.getLogger(Cookie.class);

	
	private String domain = null;
	private String path = null;
	private Integer maxAge = null;
	private String expires = null;
	private boolean secure = false;
	private boolean httpOnly = false;

	private String name;
	private String value;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public boolean isHttpOnly() {
		return httpOnly;
	}

	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return (this.name != null || this.value != null ? (this.name + "=" + this.value) : "") + (this.domain != null ? ("; domain=" + this.domain) : "")
				+ (this.maxAge != null ? ("; max-age=" + this.maxAge) : "") + (this.path != null ? ("; path=" + this.path) : " ") + (this.httpOnly ? "; httponly; " : "")
				+ (this.secure ? "; secure" : "");
	}

	/**
	 * 通过 Map 构建一个 Cookie 对象
	 * @param cookieMap Cookie 属性 Map
	 * @return Cookie 对象
	 */
	public static Cookie buildCookie(Map<String, String> cookieMap) {
		Cookie cookie = new Cookie();
		for (Entry<String, String> cookieMapItem : cookieMap.entrySet()) {
			switch (cookieMapItem.getKey().toLowerCase()) {
			case "domain":
				cookie.setDomain(cookieMapItem.getValue());
				break;
			case "path":
				cookie.setPath(cookieMapItem.getValue());
				break;
			case "max-age":
				cookie.setMaxAge(Integer.parseInt(cookieMapItem.getValue()));
				break;
			case "secure":
				cookie.setSecure(true);
				break;
			case "httponly":
				cookie.setHttpOnly(true);
				break;
			case "expires":
				cookie.setExpires(cookieMapItem.getValue());
				break;
			default:
				cookie.setName(cookieMapItem.getKey());
				try {
					cookie.setValue(URLDecoder.decode(cookieMapItem.getValue(), HttpConst.CHARSET_NAME));
				} catch (UnsupportedEncodingException e) {
					log.error(e.toString(), e);
				}
				break;
			}
		}
		return cookie;
	}

	/**
	 * 创建一个 Cookie
	 * @param domain	cookie的受控域
	 * @param name		名称
	 * @param value		值
	 * @param maxAge	失效时间,单位秒
	 * @return Cookie 对象
	 */
	public Cookie (String domain, String name, String value, int maxAge) {
		setName(name);
		setValue(value);
		setPath("/");
		setDomain(domain);
		setMaxAge(maxAge);
		setHttpOnly(false);
	}

	/**
	 * 
	 * @author: tanyaowu
	 */
	public Cookie() {
	}

	public static Map<String, String> getEqualMap(String cookieline) {
		Map<String, String> equalMap = new HashMap<String, String>();
		String[] searchedStrings = searchByRegex(cookieline, "([^ ;,]+=[^ ;,]+)");
		for (String groupString : searchedStrings) {
			//这里不用 split 的原因是有可能等号后的值字符串中出现等号
			String[] equalStrings = new String[2];
			int equalCharIndex = groupString.indexOf("=");
			equalStrings[0] = groupString.substring(0, equalCharIndex);
			equalStrings[1] = groupString.substring(equalCharIndex + 1, groupString.length());
			if (equalStrings.length == 2) {
				String key = equalStrings[0];
				String value = equalStrings[1];
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length() - 1);
				}
				equalMap.put(key, value);
			}
		}
		return equalMap;
	}

	public static String[] searchByRegex(String source, String regex) {
		if (source == null) {
			return null;
		}

		Map<Integer, Pattern> regexPattern = new HashMap<Integer, Pattern>();

		Pattern pattern = null;
		if (regexPattern.containsKey(regex.hashCode())) {
			pattern = regexPattern.get(regex.hashCode());
		} else {
			pattern = Pattern.compile(regex);
			regexPattern.put(regex.hashCode(), pattern);
		}
		Matcher matcher = pattern.matcher(source);
		ArrayList<String> result = new ArrayList<String>();
		while (matcher.find()) {
			result.add(matcher.group());
		}
		return result.toArray(new String[0]);
	}
}
