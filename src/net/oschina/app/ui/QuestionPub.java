package net.oschina.app.ui;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.app.R;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.Result;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

/**
 * ��������
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class QuestionPub extends Activity{

	private ImageView mBack;
	private EditText mTitle;
	private EditText mContent;
	private Spinner mCatalog;
	private CheckBox mEmail;
	private Button mPublish;
    private ProgressDialog mProgress;
	private Post post;
	private InputMethodManager imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_pub);
		
		this.initView();
		
	}
	
    //��ʼ����ͼ�ؼ�
    private void initView()
    {    	
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	
    	mBack = (ImageView)findViewById(R.id.question_pub_back);
    	mPublish = (Button)findViewById(R.id.question_pub_publish);
    	mTitle = (EditText)findViewById(R.id.question_pub_title);
    	mContent = (EditText)findViewById(R.id.question_pub_content);
    	mEmail = (CheckBox)findViewById(R.id.question_pub_email);
    	mCatalog = (Spinner)findViewById(R.id.question_pub_catalog);
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mPublish.setOnClickListener(publishClickListener);
    	mCatalog.setOnItemSelectedListener(catalogSelectedListener);
    	//�༭������ı�����
    	mTitle.addTextChangedListener(UIHelper.getTextWatcher(this, AppConfig.TEMP_POST_TITLE));
    	mContent.addTextChangedListener(UIHelper.getTextWatcher(this, AppConfig.TEMP_POST_CONTENT));
    	
    	//��ʾ��ʱ�༭����
    	UIHelper.showTempEditContent(this, mTitle, AppConfig.TEMP_POST_TITLE);
    	UIHelper.showTempEditContent(this, mContent, AppConfig.TEMP_POST_CONTENT);
    	//��ʾ��ʱѡ�����
    	String position = ((AppContext)getApplication()).getProperty(AppConfig.TEMP_POST_CATALOG);
    	mCatalog.setSelection(StringUtils.toInt(position, 0));
    }
	
    private AdapterView.OnItemSelectedListener catalogSelectedListener = new AdapterView.OnItemSelectedListener(){
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			//������ʱѡ��ķ���
			((AppContext)getApplication()).setProperty(AppConfig.TEMP_POST_CATALOG, position+"");
		}
		public void onNothingSelected(AdapterView<?> parent) {}
    };
    
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			//���������
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			
			String title = mTitle.getText().toString();
			if(StringUtils.isEmpty(title)){
				UIHelper.ToastMessage(v.getContext(), "���������");
				return;
			}
			String content = mContent.getText().toString();
			if(StringUtils.isEmpty(content)){
				UIHelper.ToastMessage(v.getContext(), "��������������");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(QuestionPub.this);
				return;
			}
			
			mProgress = ProgressDialog.show(v.getContext(), null, "�����С�����",true,true); 
			
			post = new Post();
			post.setAuthorId(ac.getLoginUid());
			post.setTitle(title);
			post.setBody(content);
			post.setCatalog(mCatalog.getSelectedItemPosition()+1);
			if(mEmail.isChecked())
				post.setIsNoticeMe(1);
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					if(mProgress!=null)mProgress.dismiss();
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(QuestionPub.this, res.getErrorMessage());
						if(res.OK()){
							//����֪ͨ�㲥
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(QuestionPub.this, res.getNotice());
							}
							//���֮ǰ����ı༭����
							ac.removeProperty(AppConfig.TEMP_POST_TITLE,AppConfig.TEMP_POST_CATALOG,AppConfig.TEMP_POST_CONTENT);
							//��ת����������
							finish();
						}
					}
					else {
						((AppException)msg.obj).makeToast(QuestionPub.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg = new Message();					
					try {
						Result res = ac.pubPost(post);
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
