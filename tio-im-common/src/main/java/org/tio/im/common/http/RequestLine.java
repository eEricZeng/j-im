package org.tio.im.common.http;

/**
 * @author tanyaowu 
 * 2017年6月28日 下午2:20:32
 */
public class RequestLine {
	private Method method;
	private String path;    //譬如http://www.163.com/user/get?name=tan&id=789，那些此值就是/user/get
	private String queryStr; //譬如http://www.163.com/user/get?name=tan&id=789，那些此值就是name=tan&id=789
	private String pathAndQuerystr;
	private String version;
	private String initStr;

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the initStr
	 */
	public String getInitStr() {
		return initStr;
	}

	/**
	 * @param initStr the initStr to set
	 */
	public void setInitStr(String initStr) {
		this.initStr = initStr;
	}

	/**
	 * @return the queryStr
	 */
	public String getQueryStr() {
		return queryStr;
	}

	/**
	 * @param queryStr the queryStr to set
	 */
	public void setQueryStr(String queryStr) {
		this.queryStr = queryStr;
	}

	public String getPathAndQuerystr() {
		return pathAndQuerystr;
	}

	public void setPathAndQuerystr(String pathAndQuerystr) {
		this.pathAndQuerystr = pathAndQuerystr;
	}
}
