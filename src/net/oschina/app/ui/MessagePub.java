package net.oschina.app.ui;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.app.R;
import net.oschina.app.bean.Result;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ��������
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class MessagePub extends Activity{
	
	private ImageView mBack;
	private TextView mReceiver;
	private EditText mContent;
	private Button mPublish;
    private ProgressDialog mProgress;
    private InputMethodManager imm;
    private String tempMessageKey = AppConfig.TEMP_MESSAGE;
	
	private int _uid;
	private int _friendid;
	private String _friendname;
	private String _content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_pub);
		
		this.initView();
	}
	
    //��ʼ����ͼ�ؼ�
    private void initView()
    {
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	
		_uid = getIntent().getIntExtra("user_id", 0);
		_friendid = getIntent().getIntExtra("friend_id", 0);
		_friendname = getIntent().getStringExtra("friend_name");
    	
		if(_friendid > 0) tempMessageKey = AppConfig.TEMP_MESSAGE + "_" + _friendid;
		
    	mBack = (ImageView)findViewById(R.id.message_pub_back);
    	mPublish = (Button)findViewById(R.id.message_pub_publish);
    	mContent = (EditText)findViewById(R.id.message_pub_content);
    	mReceiver = (TextView)findViewById(R.id.message_pub_receiver);
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mPublish.setOnClickListener(publishClickListener);
    	//�༭�������ı�����
    	mContent.addTextChangedListener(UIHelper.getTextWatcher(this, tempMessageKey));
    	
    	//��ʾ��ʱ�༭����
    	UIHelper.showTempEditContent(this, mContent, tempMessageKey);
    	
    	mReceiver.setText("�������Ը�  "+_friendname);
    }    
	
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//����������
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			
			_content = mContent.getText().toString();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(v.getContext(), "��������������");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(MessagePub.this);
				return;
			}
			
			mProgress = ProgressDialog.show(v.getContext(), null, "�����С�����",true,true); 
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					
					if(mProgress!=null)mProgress.dismiss();
					
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(MessagePub.this, res.getErrorMessage());
						if(res.OK()){
							//����֪ͨ�㲥
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(MessagePub.this, res.getNotice());
							}
							//���֮ǰ����ı༭����
							ac.removeProperty(tempMessageKey);
							//���ظոշ���������
							Intent intent = new Intent();
							intent.putExtra("COMMENT_SERIALIZABLE", res.getComment());
							setResult(RESULT_OK, intent);
							finish();
						}
					}
					else {
						((AppException)msg.obj).makeToast(MessagePub.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg =new Message();
					try {
						Result res = ac.pubMessage(_uid, _friendid, _content);
						msg.what = 1;
						msg.obj = res;
		            } catch (AppException e) {
		            	e.printStackTrace();
						msg.what = -1;
						msg.obj = e;
		            }
					handler.sendMessage(msg);
				}
			}.start();
		}
	};
}
