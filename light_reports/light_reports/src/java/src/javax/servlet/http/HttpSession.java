package javax.servlet.http;

public class HttpSession {
	
	private Object o;
	public void setAttribute(String key, Object obj) {
		o = obj;
	}
		
	public Object getAttribute(final String key) {
		return o;
	}
	
}
