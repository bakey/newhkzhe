package net.oschina.app.ui;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.app.R;
import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.User;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import com.hkzhe.wwtt.common.Constants;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.Utility;

/**
 * 用户登录对话框
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class LoginDialog extends Activity{
	
	private ViewSwitcher mViewSwitcher;
	private ImageButton btn_close;
	private Button btn_login;
	private Button btn_weibo_login;
	private AutoCompleteTextView mAccount;
	private EditText mPwd;
	private AnimationDrawable loadingAnimation;
	private View loginLoading;
	private CheckBox chb_rememberMe;
	private int curLoginType;
	private InputMethodManager imm;
	private final Handler m_login_handler = new Handler()  {
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				User user = (User)msg.obj;
				if(user != null){
					//清空原先cookie
					ApiClient.cleanCookie();
					//发送通知广播
					UIHelper.sendBroadCast(LoginDialog.this, user.getNotice());
					//提示登陆成功
					UIHelper.ToastMessage(LoginDialog.this, R.string.msg_login_success);
					if(curLoginType == LOGIN_MAIN){
						//跳转--加载用户动态
						Intent intent = new Intent(LoginDialog.this, Main.class);
						intent.putExtra("LOGIN", true);
						startActivity(intent);
					}else if(curLoginType == LOGIN_SETTING){
						//跳转--用户设置页面
						Intent intent = new Intent(LoginDialog.this, Setting.class);
						intent.putExtra("LOGIN", true);
						startActivity(intent);
					}
					finish();
				}
			}else if(msg.what == 0){
				mViewSwitcher.showPrevious();
				UIHelper.ToastMessage(LoginDialog.this, getString(R.string.msg_login_fail)+msg.obj);
			}else if(msg.what == -1){
				mViewSwitcher.showPrevious();
				((AppException)msg.obj).makeToast(LoginDialog.this);
			}
		}
	};
	
	private Weibo mWeibo; 
	private SsoHandler mSsoHandler;
	
	public final static int LOGIN_OTHER = 0x00;
	public final static int LOGIN_MAIN = 0x01;
	public final static int LOGIN_SETTING = 0x02;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);
        
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        
        curLoginType = getIntent().getIntExtra("LOGINTYPE", LOGIN_OTHER);
        
        mViewSwitcher = (ViewSwitcher)findViewById(R.id.logindialog_view_switcher);       
        loginLoading = (View)findViewById(R.id.login_loading);
        mAccount = (AutoCompleteTextView)findViewById(R.id.login_account);
        mPwd = (EditText)findViewById(R.id.login_password);
        chb_rememberMe = (CheckBox)findViewById(R.id.login_checkbox_rememberMe);
        
        mWeibo = Weibo.getInstance(Constants.APP_KEY, Constants.REDIRECT_URL,Constants.SCOPE);

        
        btn_close = (ImageButton)findViewById(R.id.login_close_button);
        btn_close.setOnClickListener(UIHelper.finish(this));        
        
        btn_login = (Button)findViewById(R.id.login_btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//隐藏软键盘
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
				
				String account = mAccount.getText().toString();
				String pwd = mPwd.getText().toString();
				boolean isRememberMe = chb_rememberMe.isChecked();
				//判断输入
				if(StringUtils.isEmpty(account)){
					UIHelper.ToastMessage(v.getContext(), getString(R.string.msg_login_email_null));
					return;
				}
				if(StringUtils.isEmpty(pwd)){
					UIHelper.ToastMessage(v.getContext(), getString(R.string.msg_login_pwd_null));
					return;
				}
				
		        btn_close.setVisibility(View.GONE);
		        loadingAnimation = (AnimationDrawable)loginLoading.getBackground();
		        loadingAnimation.start();
		        mViewSwitcher.showNext();
		        
		        login(account, pwd, isRememberMe);
			}
		});
        btn_weibo_login = (Button)findViewById( R.id.weibo_login_btn_login );
        btn_weibo_login.setOnClickListener( new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
		        mSsoHandler = new SsoHandler(LoginDialog.this, mWeibo);
		        mSsoHandler.authorize( new AuthDialogListener( m_login_handler ) , null );				
			}
		});

        //是否显示登录信息
        AppContext ac = (AppContext)getApplication();
        User user = ac.getLoginInfo();
        if(user==null || !user.isRememberMe()) return;
        if(!StringUtils.isEmpty(user.getAccount())){
        	mAccount.setText(user.getAccount());
        	mAccount.selectAll();
        	chb_rememberMe.setChecked(user.isRememberMe());
        }
        if(!StringUtils.isEmpty(user.getPwd())){
        	mPwd.setText(user.getPwd());
        }
    }
    
    //登录验证
    private void login(final String account, final String pwd, final boolean isRememberMe) {
		new Thread(){
			public void run() {
				Message msg =new Message();
				try {
					AppContext ac = (AppContext)getApplication(); 
	                User user = ac.loginVerify(account, pwd);
	                user.setAccount(account);
	                user.setPwd(pwd);
	                user.setRememberMe(isRememberMe);
	                Result res = user.getValidate();
	                if(res.OK()){
	                	ac.saveLoginInfo(user , AppContext.LOGIN_TYPE_NORMAL );//保存登录信息
	                	msg.what = 1;//成功
	                	msg.obj = user;
	                }else{
	                	ac.cleanLoginInfo();//清除登录信息
	                	msg.what = 0;//失败
	                	msg.obj = res.getErrorMessage();
	                }
	            } catch (AppException e) {
	            	e.printStackTrace();
			    	msg.what = -1;
			    	msg.obj = e;
	            }
				m_login_handler.sendMessage(msg);
			}
		}.start();
    }
    
    class AuthDialogListener implements WeiboAuthListener {
    	private final Handler m_login_handler;
    	    	
    	public AuthDialogListener( Handler loginHandler ) {
    		m_login_handler = loginHandler;  
    	}
    	
    	public void loginAsWeiboUser( final String access_token , final String uid ) throws JSONException {
    		final AppContext appCxt = (AppContext)getApplication();
    		final HashMap<String,Object> newParams = new HashMap<String,Object>();
			newParams.put( "uid", uid );
			newParams.put( "access_token", access_token );
			appCxt.setAccessToken( access_token );
			String user_info_str = appCxt.getWeiboUserInfo( newParams );
			Message msg =new Message();
			if ( user_info_str == "" ) {
				msg.what = 0 ;
				msg.obj = getString( R.string.msg_weibo_authorize_fail ) ;
				Log.d( "Main" , "get weibo user info faild ");
			}
			else {
				try {
					JSONObject json = new JSONObject( user_info_str );
					final String screen_name = json.getString("screen_name");
							
					final User user = new User();
					Result res = new Result();
					res.setErrorCode( 1 );
					user.setAccount( json.getString("name") );
					user.setUid( StringUtils.toInt( uid ) );
					user.setName( screen_name );
					user.setFace( json.getString("profile_image_url") );
					user.setRememberMe( true );
					user.setValidate( res );
					user.setFollowers( json.getInt("followers_count") );				
				
					int login_type = AppContext.LOGIN_TYPE_WEIBO;
					appCxt.saveLoginInfo(user , login_type );
					Log.d( "Main" , "login success with weibo");
					
					final HashMap<String, Object> params = new HashMap<String, Object>() { {
						put( "uid" , uid );
						put( "access_token" , access_token );
						put( "uname" , screen_name );
						put( "profile_image_url" , user.getFace() );
					}};
					new Thread() {
						public void run() {
							Log.d( "Main" , "start notify server login");
							appCxt.notifyServerLogin(params);
						}
						
					}.start();
					
					msg.what = 1;
					msg.obj = user;				
				}catch( JSONException e ) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;				
				}    	
			}
			m_login_handler.sendMessage(msg);
    	}

        @Override
        public void onComplete(Bundle values) {  	
       	
        	String code = values.getString("code");
        	if(code != null){
        		String token = values.getString("access_token");        		       		
        		final HashMap<String, Object> params = new HashMap<String,Object>();
        		params.put( "client_id", Constants.APP_KEY );
        		params.put( "client_secret" , Constants.APP_SECRET );
        		params.put( "grant_type",  "authorization_code");
        		params.put( "code", code );
        		params.put( "redirect_uri" , Constants.REDIRECT_URL );
        		
        		new Thread() {
        			public void run() {
        				AppContext appCxt = (AppContext)getApplication();
        				String token_json = appCxt.getWeiboAuthorizeMsg( params );
        				try {
                			JSONObject json_obj = new JSONObject( token_json );
                			String uid = json_obj.getString("uid");
                			String token = json_obj.getString("access_token");
                			Log.d( "Main" , "get token = " + token + ",uid = " + uid );
                			loginAsWeiboUser( token , uid );
                		}catch( JSONException e ) {
                			e.printStackTrace();
                		}
        				
        			}
        		}.start();      	
        		
        		//UIHelper.finish( LoginDialog.this );
        		
	        	return;
        	}
            
           /* String expires_in = values.getString("expires_in");
            MainActivity.accessToken = new Oauth2AccessToken(token, expires_in);
            if (MainActivity.accessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new java.util.Date(MainActivity.accessToken
                                .getExpiresTime()));
                mText.setText("认证成功: \r\n access_token: " + token + "\r\n"
                        + "expires_in: " + expires_in + "\r\n有效期：" + date);
             
                AccessTokenKeeper.keepAccessToken(MainActivity.this,
                        accessToken);
                Toast.makeText(MainActivity.this, "认证成功", Toast.LENGTH_SHORT)
                        .show();
            }*/
        }

        @Override
        public void onError(WeiboDialogError e) {
            Toast.makeText(getApplicationContext(),
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "Auth cancel",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(),
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }
}


