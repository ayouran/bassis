package bassis.bassis_web.assist;

import java.util.HashMap;
import java.util.Map;

import bassis.bassis_tools.string.StringUtils;

/**
 * 控制器资源管家
 * @author ytx
 *
 */
public class StewardResource {

	private static class LazyHolder {
		private static final StewardResource INSTANCE = new StewardResource();
	}

	private StewardResource() {
	}

	public static final StewardResource getInstance() {
		return LazyHolder.INSTANCE;
	}
	static Map<String, Resource> mapResource = new HashMap<String, Resource>();
	/**
	 * 移除一个Resource
	 * @param key action的请求路径  
	 */
	public static void remove(String key) {
		mapResource.remove(key);
	}
	/**
	 * 将Resource放入存储器
	 * @param key action的请求路径  
	 * @param value 资源对象
	 */
	public static void put(String key,Resource value) {
		mapResource.put(key, value);
	}
	/**
	 * 获得Resource
	 * @param key action的内存地址
	 */
	public static Resource get(String key) {
		if(StringUtils.isEmptyString(key) || !mapResource.containsKey(key))
			return null;
		
		return mapResource.get(key);
	}
}