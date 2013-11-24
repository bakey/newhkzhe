package net.oschina.app.widget;

import com.hkzhe.app.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;

/**
 * �û���Ϣ�Ի���ؼ�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-7-2
 */
public class UserInfoDialog extends Dialog {
	
	private LayoutParams lp;

	public UserInfoDialog(Context context) {
		super(context, R.style.Dialog);		
		setContentView(R.layout.user_center_content);
		
		// ���õ���Ի���֮������ʧ
		setCanceledOnTouchOutside(true);
		// ����window����
		lp = getWindow().getAttributes();
		lp.gravity = Gravity.TOP;
		lp.dimAmount = 0; // ȥ�����ڸ�
		lp.alpha = 1.0f;
		lp.y = 55;
		getWindow().setAttributes(lp);

	}
}
