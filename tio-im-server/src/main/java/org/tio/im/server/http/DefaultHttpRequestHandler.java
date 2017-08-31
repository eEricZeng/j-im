package org.tio.im.server.http;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.im.common.http.HttpRequestPacket;
import org.tio.im.common.http.HttpResponsePacket;
import org.tio.im.common.http.RequestLine;
import org.tio.im.server.http.mvc.Routes;
import org.tio.im.server.util.ClassUtils;
import org.tio.im.server.util.Resps;

import com.xiaoleilu.hutool.convert.Convert;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.ClassUtil;

/**
 * 
 * @author tanyaowu 
 *
 */
public class DefaultHttpRequestHandler implements IHttpRequestHandler {
	private static Logger log = LoggerFactory.getLogger(DefaultHttpRequestHandler.class);

	protected HttpServerConfig httpServerConfig;

	protected Routes routes = null;

	private IHttpServerListener httpServerListener;

	public DefaultHttpRequestHandler() {
	}

	@Override
	public HttpResponsePacket handler(HttpRequestPacket httpRequestPacket, RequestLine requestLine, ChannelContext channelContext)
			throws Exception {
		HttpResponsePacket ret = null;
		try {
			if (httpServerListener != null) {
				ret = httpServerListener.doBeforeHandler(httpRequestPacket, requestLine, channelContext);
				if (ret != null) {
					return ret;
				}
			}
			
			String path = requestLine.getPath();
			if (StringUtils.endsWith(path, "/")) {
				path = path + "index.html";
			}

			Method method = routes.pathMethodMap.get(path);
			if (method != null) {
				String[] paramnames = routes.methodParamnameMap.get(method);
				Class<?>[] parameterTypes = method.getParameterTypes();

				Object bean = routes.methodBeanMap.get(method);
				Object obj = null;
				Map<String, Object[]> params = httpRequestPacket.getParams();
				//				OgnlContext context = new OgnlContext(params);
				if (parameterTypes == null || parameterTypes.length == 0) {
					//					obj = method.invoke(bean, httpRequestPacket, httpServerConfig, channelContext);
					obj = method.invoke(bean);
				} else {
					//赋值这段代码待重构，先用上
					Object[] paramValues = new Object[parameterTypes.length];
					int i = 0;
					for (Class<?> paramType : parameterTypes) {
						try {
							if (paramType.isAssignableFrom(HttpRequestPacket.class)) {
								paramValues[i] = httpRequestPacket;
							} else if (paramType.isAssignableFrom(HttpServerConfig.class)) {
								paramValues[i] = httpServerConfig;
							} else if (paramType.isAssignableFrom(ChannelContext.class)) {
								paramValues[i] = channelContext;
							} else {
								if (params != null) {
									if (ClassUtils.isSimpleTypeOrArray(paramType)) {
//										paramValues[i] = Ognl.getValue(paramnames[i], (Object) params, paramType);
										Object[] value = params.get(paramnames[i]);
										if (value != null && value.length > 0) {
											if(paramType.isArray()) {
												paramValues[i] = Convert.convert(paramType, value);
											} else {
												paramValues[i] = Convert.convert(paramType, value[0]);
											}
										}
									} else {
										paramValues[i] = paramType.newInstance();//BeanUtil.mapToBean(params, paramType, true);
										
										Set<Entry<String, Object[]>> set = params.entrySet();
										label2: for (Entry<String, Object[]> entry : set) {
											String fieldName = entry.getKey();
											Object[] fieldValue = entry.getValue();
											
											PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(paramType, fieldName, true);
											if (propertyDescriptor == null) {
												continue label2;
											} else {
												Method writeMethod = propertyDescriptor.getWriteMethod();
												if (writeMethod == null) {
													continue label2;
												}
												writeMethod = ClassUtil.setAccessible(writeMethod);
												Class<?>[] clazzes = writeMethod.getParameterTypes();
												if (clazzes == null || clazzes.length != 1) {
													log.info("方法的参数长度不为1，{}.{}", paramType.getName(), writeMethod.getName());
													continue label2;
												}
												Class<?> clazz = clazzes[0];
												
												if (ClassUtils.isSimpleTypeOrArray(clazz)) {
													if (fieldValue != null && fieldValue.length > 0) {
														if(clazz.isArray()) {
//															paramValues[i] = Convert.convert(clazz, fieldValue);
															writeMethod.invoke(paramValues[i], Convert.convert(clazz, fieldValue));
														} else {
//															paramValues[i] = Convert.convert(clazz, fieldValue[0]);
															writeMethod.invoke(paramValues[i], Convert.convert(clazz, fieldValue[0]));
														}
													}
												}
											}
										}
										
//										Set<Entry<String, Object[]>> set = params.entrySet();
//										for (Entry<String, Object[]> entry : set) {
//											String fieldName = entry.getKey();
//											Object[] fieldValue = entry.getValue();
//											//										Ognl.setValue(paramValues[i], fieldName, fieldValue);
//											try {
//												Ognl.setValue(fieldName, paramValues[i], fieldValue);
//											} catch (NoSuchPropertyException e) {
//												// 暂时skip it，后续优化
//											} catch (Exception e) {
//												log.error(e.toString(), e);
//											}
//										}
									}
								}
							}
						} catch (Exception e) {
							log.error(e.toString(), e);
						} finally {
							i++;
						}
					}
					obj = method.invoke(bean, paramValues);
				}

				if (obj instanceof HttpResponsePacket) {
					return (HttpResponsePacket) obj;
				} else {
					//					log.error(bean.getClass().getName() + "#"+method.getName()+"返回的对象不是" + HttpResponsePacket.class.getName());
					throw new Exception(bean.getClass().getName() + "#" + method.getName() + "返回的对象不是" + HttpResponsePacket.class.getName());
				}
			} else {
				String root = httpServerConfig.getRoot();
				File file = new File(root, path);
				if (file.exists()) {
					ret = Resps.file(httpRequestPacket, file);
					return ret;
				}
			}

			ret = resp404(httpRequestPacket, requestLine, channelContext);//Resps.html(httpRequestPacket, "404--并没有找到你想要的内容", httpServerConfig.getCharset());
			return ret;
		} catch (Exception e) {
			String errorlog = "";//"error occured,\r\n";
			errorlog += requestLine.getInitStr();// + "\r\n";
			//			errorlog += e.toString();
			log.error(errorlog, e);
			ret = resp500(httpRequestPacket, requestLine, channelContext, e);//Resps.html(httpRequestPacket, "500--服务器出了点故障", httpServerConfig.getCharset());
			return ret;
		} finally {
			if (httpServerListener != null) {
				httpServerListener.doAfterHandler(httpRequestPacket, requestLine, channelContext, ret);
				return ret;
			}
		}
	}

	@Override
	public HttpResponsePacket resp404(HttpRequestPacket httpRequestPacket, RequestLine requestLine, ChannelContext channelContext) {
		String file404 = "/404.html";
		String root = httpServerConfig.getRoot();
		File file = new File(root, file404);
		if (file.exists()) {
			HttpResponsePacket ret = Resps.redirect(httpRequestPacket, file404 + "?initpath=" + requestLine.getPathAndQuerystr());
			return ret;
		} else {
			HttpResponsePacket ret = Resps.html(httpRequestPacket, "404", httpRequestPacket.getCharset());
			return ret;
		}
	}

	@Override
	public HttpResponsePacket resp500(HttpRequestPacket httpRequestPacket, RequestLine requestLine, ChannelContext channelContext,
			Throwable throwable) {
		String file500 = "/500.html";
		String root = httpServerConfig.getRoot();
		File file = new File(root, file500);
		if (file.exists()) {
			HttpResponsePacket ret = Resps.redirect(httpRequestPacket, file500 + "?initpath=" + requestLine.getPathAndQuerystr());
			return ret;
		} else {
			HttpResponsePacket ret = Resps.html(httpRequestPacket, "500", httpRequestPacket.getCharset());
			return ret;
		}
	}

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:13:15
	 * 
	 */
	public DefaultHttpRequestHandler(HttpServerConfig httpServerConfig) {
		this.httpServerConfig = httpServerConfig;
	}

	public DefaultHttpRequestHandler(HttpServerConfig httpServerConfig, Routes routes) {
		this(httpServerConfig);
		this.routes = routes;
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:13:15
	 * 
	 */
	public static void main(String[] args) {

//		System.out.println(ClassUtil.isBasicType(String.class));
//		System.out.println(ClassUtil.isBasicType(Object.class));
//		System.out.println(ClassUtil.isBasicType(Integer.class));
//		System.out.println(ClassUtil.isBasicType(int.class));
//
//		Map<String, String[]> params = new HashMap<>();
//		String[] names = new String[] { "111" };
//		params.put("id", names);
//
//		User user = BeanUtil.mapToBean(params, User.class, true);
//
//		try {
//			Object obj = Ognl.getValue("id", (Object) params, (Class<?>) Integer.class);
//			System.out.println(obj);
//
//		} catch (OgnlException e) {
//			log.error(e.toString(), e);
//		}
	}

	public static class User {
		private int[] id;

		/**
		 * @return the id
		 */
		public int[] getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(int[] id) {
			this.id = id;
		}
	}

	/**
	 * @return the httpServerConfig
	 */
	public HttpServerConfig getHttpServerConfig() {
		return httpServerConfig;
	}

	/**
	 * @param httpServerConfig the httpServerConfig to set
	 */
	public void setHttpServerConfig(HttpServerConfig httpServerConfig) {
		this.httpServerConfig = httpServerConfig;
	}

	public IHttpServerListener getHttpServerListener() {
		return httpServerListener;
	}

	public void setHttpServerListener(IHttpServerListener httpServerListener) {
		this.httpServerListener = httpServerListener;
	}
	


}
