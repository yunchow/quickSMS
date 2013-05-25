package nick.chow.app.context;

import nick.chow.smsshow.R;
import android.content.Context;

/**
 * @author zhouyun
 *
 */
public class SimpleMail {
	private Context context;
	private Mail m;
	
	public SimpleMail(Context context) {
		this.context = context;
		m = new Mail("yunzhounj@gmail.com", "googlePassw0rd");
        String[] toArr = {"yunchow@qq.com"};
    	m.set_to(toArr); 
        m.set_from("nick@chow.com"); 
        m.set_subject(this.context.getString(R.string.subject)); 
	}
	
	public void setBody(String body) {
		m.setBody(body);
	}
	
	public boolean send() throws Exception {
		return m.send();
	}
	
}
