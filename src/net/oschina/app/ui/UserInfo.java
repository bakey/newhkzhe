package net.oschina.app.ui;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.app.R;
import net.oschina.app.bean.FriendList;
import net.oschina.app.bean.MyInformation;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.widget.LoadingDialog;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 用户资料
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class UserInfo extends Activity{	
	
	private ImageView back;
	private ImageView refresh;
	private ImageView face;
	private ImageView gender;
	private TextView name;
	private TextView jointime;
	private TextView from;
	private TextView m_tv_user_interest_area;
	private TextView m_tv_user_level ;
	private TextView followers;
	private TextView fans;
	private TextView favorites;
	private LinearLayout favorites_ll;
	private LinearLayout followers_ll;
	private LinearLayout fans_ll;
	private LoadingDialog loading;
	private MyInformation user;
	private Handler mHandler;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);		
		
		//初始话视图控件
		this.initView();
		//初始化视图数据
		this.initData();
	}
	
	private void initView(){
		back = (ImageView)findViewById(R.id.user_info_back);
		refresh = (ImageView)findViewById(R.id.user_info_refresh);
		back.setOnClickListener(UIHelper.finish(this));
		refresh.setOnClickListener(refreshClickListener);
		
		face = (ImageView)findViewById(R.id.user_info_userface);
		gender = (ImageView)findViewById(R.id.user_info_gender);
		name = (TextView)findViewById(R.id.user_info_username);
		jointime = (TextView)findViewById(R.id.user_info_jointime);
		from = (TextView)findViewById(R.id.user_info_from);
		m_tv_user_interest_area = (TextView)findViewById(R.id.user_interest_area);
		m_tv_user_level = (TextView)findViewById(R.id.user_info_level);
		followers = (TextView)findViewById(R.id.user_info_followers);
		fans = (TextView)findViewById(R.id.user_info_fans);
		favorites = (TextView)findViewById(R.id.user_info_favorites);
		favorites_ll = (LinearLayout)findViewById(R.id.user_info_favorites_ll);
		followers_ll = (LinearLayout)findViewById(R.id.user_info_followers_ll);
		fans_ll = (LinearLayout)findViewById(R.id.user_info_fans_ll);
	}
	
	private void initData(){
		mHandler = new Handler(){
			public void handleMessage(Message msg) {
				Log.d( "Main" , "handle user info msg . msg what = " + msg.what );
				if(loading != null)	{
					loading.dismiss();
				}
				if(msg.what == 1 && msg.obj != null){
					user = (MyInformation)msg.obj;
					
					//加载用户头像
					UIHelper.showUserFace(face, user.getFace());
					Log.d( "Main" , "user gender = " + user.getGender() );
					Log.d( "Main" , "user join time = " + user.getJointime() );
					
					//用户性别
					if(user.getGender() == MyInformation.GENDER_MAN ) {
						gender.setImageResource(R.drawable.widget_gender_man);
					}
					else {
						gender.setImageResource(R.drawable.widget_gender_woman);
					}
					
					//其他资料
					name.setText(user.getName());
					jointime.setText(StringUtils.friendly_time(user.getJointime()));
					from.setText(user.getFrom());
					m_tv_user_interest_area.setText(user.getInterestArea());
					m_tv_user_level.setText(user.getUserLevel());
					followers.setText(user.getFollowerscount()+"");
					fans.setText(user.getFanscount()+"");
					favorites.setText(user.getFavoritecount()+"");
					
					favorites_ll.setOnClickListener(favoritesClickListener);
					fans_ll.setOnClickListener(fansClickListener);
					followers_ll.setOnClickListener(followersClickListener);
					
				}else if(msg.obj != null){
					((AppException)msg.obj).makeToast(UserInfo.this);
				}
			}
		};		
		this.loadUserInfoThread(false);
	}
	
	private void loadUserInfoThread(final boolean isRefresh){
		loading = new LoadingDialog(this);		
		loading.show();
		
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					MyInformation user = ((AppContext)getApplication()).getMyInformation(isRefresh);
					msg.what = 1;
	                msg.obj = user;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	                msg.obj = e;
	            }
				mHandler.sendMessage(msg);
			}
		}.start();
	}
	
	private View.OnClickListener refreshClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			loadUserInfoThread(true);
		}
	};
	
	private View.OnClickListener favoritesClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			//UIHelper.showUserFavorite(v.getContext());
		}
	};
	
	private View.OnClickListener fansClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			int followers = user!=null ? user.getFollowerscount() : 0;
			int fans = user!=null ? user.getFanscount() : 0;
			UIHelper.showUserFriend(v.getContext(), FriendList.TYPE_FANS, followers, fans);
		}
	};
	
	private View.OnClickListener followersClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			int followers = user!=null ? user.getFollowerscount() : 0;
			int fans = user!=null ? user.getFanscount() : 0;
			UIHelper.showUserFriend(v.getContext(), FriendList.TYPE_FOLLOWER, followers, fans);
		}
	};
}
