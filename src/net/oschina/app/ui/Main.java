package net.oschina.app.ui;

import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hkzhe.app.R;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.AppManager;
import net.oschina.app.adapter.ListViewActiveAdapter;
import net.oschina.app.adapter.ListViewBlogAdapter;
import net.oschina.app.adapter.ListViewMessageAdapter;
import net.oschina.app.adapter.ListViewNewsAdapter;
import net.oschina.app.adapter.ListViewQuestionAdapter;
import net.oschina.app.adapter.ListViewTweetAdapter;
import net.oschina.app.bean.Active;
import net.oschina.app.bean.ActiveList;
import net.oschina.app.bean.BBSThreads;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.BlogList;
import net.oschina.app.bean.MessageList;
import net.oschina.app.bean.Messages;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.PostList;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetList;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.common.UpdateManager;
import net.oschina.app.widget.BadgeView;
import net.oschina.app.widget.PullToRefreshListView;
import net.oschina.app.widget.ScrollLayout;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import net.oschina.app.bean.Hotels;
/**
 * Ӧ�ó�����ҳ
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class Main extends Activity {
	private final String TAG="Main";
	
	private ScrollLayout mScrollLayout;	
	private RadioButton[] mButtons;
	private String[] mHeadTitles;
	private int mViewCount;
	private int mCurSel;
	
	private ImageView mHeadLogo;
	private TextView mHeadTitle;
	private ProgressBar mHeadProgress;
	private ImageButton mHead_search;
	
	private int curNewsCatalog = NewsList.CATALOG_ALL;
	
	private PullToRefreshListView lvThreads;
	private PullToRefreshListView lvRank;
	private PullToRefreshListView lvActivities;	
	private PullToRefreshListView m_lvFavorite;
	
	private ListViewNewsAdapter lvThreadsAdapter;
	private ListViewNewsAdapter lvRankAdapter;
	private ListViewNewsAdapter lvActivitiesAdapter;
	private ListViewNewsAdapter m_lvFavoriteAdapter;

	
	private List<News> lvThreadsData = new ArrayList<News>();
	private List<News> lvRankData = new ArrayList<News>();
	private List<News> m_lvFaviritesData = new ArrayList<News>();
	private List<News> m_lvDiscountData = new ArrayList<News>();
	
	private Handler lvThreadsHandler;
	private Handler lvRankHandler;
	private Handler lvActivitiesHandler;
	private Handler m_UserFavoritesHandler;
	
	private int lvNewsSumData;
	private int lvRankSumData;
	private int lvFavoriteSumData;
	private int lvDiscountSumData;
	
	private RadioButton fbThreads;
	private RadioButton fbMsg;
	private RadioButton fbFavorite;
	private ImageView fbSetting;

	
	private Button framebtn_Threads_lastest;
	private Button framebtn_Threads_rank;
	private Button framebtn_Activities;
	
	private View lvThreads_footer;
	private View lvRank_footer;
	private View lvActivities_footer;
	private View m_lvUserFavoriteFooter;
	
	
	private TextView lvThreads_foot_more;
	private TextView lvRank_foot_more;
	private TextView lvActivities_foot_more;
	private TextView m_lvUserFavoritFootMore;
	
	
	private ProgressBar lvThreads_foot_progress;
	private ProgressBar lvRank_foot_progress;
	private ProgressBar lvActivities_foot_progress;	
	private ProgressBar m_lvFavoriteFootProgress;
	
	public static BadgeView bv_active;
	public static BadgeView bv_message;
	public static BadgeView bv_atme;
	public static BadgeView bv_review;
	
    private QuickActionWidget mGrid;//������ؼ�
	
	private boolean isClearNotice = false;
	private int curClearNoticeType = 0;
	
	private AppContext appContext;//ȫ��Context
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        
        AppManager.getAppManager().addActivity(this);
        
        
        appContext = (AppContext)getApplication();
        //���������ж�
        if(!appContext.isNetworkConnected()) {
        	UIHelper.ToastMessage(this, R.string.network_not_connected);
        }
        //��ʼ����¼
        appContext.initLoginInfo();
		
      
		this.initHeadView();
		Log.d(TAG , "init head view success");
        this.initFootBar();
        Log.d(TAG , "init foot bar success");
        this.initPageScroll();
        Log.d(TAG , "init page scroll success");
        this.initFrameButton();
        Log.d(TAG , "init Frame Button success");
        this.initBadgeView();
        //Log.d(TAG , "init badge view success");
        this.initQuickActionGrid();
        Log.d(TAG , "init quick action grid success");
        this.initFrameListView();
        Log.d( TAG , "init complete success ");
        
        //����°汾
       // UpdateManager.getUpdateManager().checkAppUpdate(this, false);
        
        //������ѯ֪ͨ��Ϣ
       // this.foreachUserNotice();
        
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if(mViewCount == 0) {
    		mViewCount = 3;
    	}
    	if(mCurSel == 0 && !fbThreads.isChecked()) {
    		fbThreads.setChecked(true);
    		fbMsg.setChecked(false);
    		fbFavorite.setChecked(false);    		
    	}
    	//��ȡ���һ�������
    	if(appContext.isScroll()) {
    		mScrollLayout.setIsScroll(true);
    	}
    	else {
    		mScrollLayout.setIsScroll(false);
    	}
    }        

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		if(intent.getBooleanExtra("LOGIN", false)){
			
		}else if(intent.getBooleanExtra("NOTICE", false)){
			//�鿴������Ϣ - ��ֹ��ǰ��ͼ���ڡ��ҵĿռ䡯��������OnViewChange�¼�
			if(mScrollLayout.getCurScreen() != 3){
				mScrollLayout.scrollToScreen(3);
			}else{
				setCurPoint(3);
			}
		}
	}
    
 
    
    /**
     * ��ʼ�������
     */
    private void initQuickActionGrid() {
        mGrid = new QuickActionGrid(this);
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_login, R.string.main_menu_login));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_myinfo, R.string.main_menu_myinfo));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_software, R.string.main_menu_software));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_search, R.string.main_menu_search));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_setting, R.string.main_menu_setting));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_exit, R.string.main_menu_exit));
        
        mGrid.setOnQuickActionClickListener(mActionListener);
    }
    
    /**
     * �����item����¼�
     */
    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		switch (position) {
    		case 0://�û���¼-ע��
    			UIHelper.loginOrLogout(Main.this);
    			break;
    		case 1:
    			UIHelper.showUserInfo(Main.this);
    			break;
    		case 2:
    			UIHelper.showSoftware(Main.this);
    			break;
    		case 3:
    			//UIHelper.showSearch(Main.this);
    			break;
    		case 4://����
    			UIHelper.showSetting(Main.this);
    			break;
    		case 5://�˳�
    			UIHelper.Exit(Main.this);
    			break;
    		}
        }
    };
    
    /**
     * ��ʼ������ListView
     */
    private void initFrameListView()
    {
    	//��ʼ��listview�ؼ�
		this.initLatestThreadsListView();
		this.initRankThreadsListView();
		this.initActivitiesListView();
		this.initUserFavoriteListView();
		//����listview����
		this.initFrameListViewData();
    }
 
    private void initFrameListViewData()
    {
        //��ʼ��Handler
        lvThreadsHandler = this.getLvHandler(lvThreads, lvThreadsAdapter, lvThreads_foot_more, lvThreads_foot_progress, AppContext.PAGE_SIZE);
        lvRankHandler = this.getLvHandler(lvRank, lvRankAdapter, lvRank_foot_more, lvRank_foot_progress, AppContext.PAGE_SIZE);
        lvActivitiesHandler = this.getLvHandler(lvActivities, lvActivitiesAdapter, lvActivities_foot_more, lvActivities_foot_progress, AppContext.PAGE_SIZE);
        m_UserFavoritesHandler = this.getLvHandler( m_lvFavorite ,  m_lvFavoriteAdapter , m_lvUserFavoritFootMore , 
        												m_lvFavoriteFootProgress, AppContext.PAGE_SIZE ); 	
    	
        //��������				
		if(lvThreadsData.size() == 0) {
			loadLvLatestNewsData(curNewsCatalog, 0, lvThreadsHandler, UIHelper.LISTVIEW_ACTION_INIT);
		}
    }
    private void initUserFavoriteListView()
    {
    	m_lvFavoriteAdapter = new ListViewNewsAdapter( this , m_lvFaviritesData , R.layout.news_listitem );
    	m_lvUserFavoriteFooter = getLayoutInflater().inflate( R.layout.listview_footer , null );
    	m_lvUserFavoritFootMore = (TextView)m_lvUserFavoriteFooter.findViewById( R.id.listview_foot_more ) ;
    	m_lvFavoriteFootProgress = (ProgressBar)m_lvUserFavoriteFooter.findViewById( R.id.listview_foot_progress );
    	m_lvFavorite = (PullToRefreshListView)findViewById( R.id.frame_listview_user_favorites );
    	m_lvFavorite.setAdapter( m_lvFavoriteAdapter );
    	m_lvFavorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         		//���ͷ�����ײ�����Ч
         		if(position == 0 || view == lvActivities_footer) {
         			return;
         		}
         		Log.d( TAG , "click the hotels data ");
         	}        	
 		});
    	
    }
    private void initActivitiesListView()
    {
    	lvActivitiesAdapter = new ListViewNewsAdapter(this, m_lvDiscountData, R.layout.news_listitem);        
    	lvActivities_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
    	lvActivities_foot_more = (TextView)lvActivities_footer.findViewById(R.id.listview_foot_more);
    	lvActivities_foot_progress = (ProgressBar)lvActivities_footer.findViewById(R.id.listview_foot_progress);
    	lvActivities = (PullToRefreshListView)findViewById( R.id.frame_listview_activities );
    	lvActivities.addFooterView(lvActivities_footer);//��ӵײ���ͼ  ������setAdapterǰ
    	lvActivities.setAdapter(lvActivitiesAdapter); 
    	lvActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         		//���ͷ�����ײ�����Ч
         		if(position == 0 || view == lvActivities_footer) {
         			return;
         		}
         		
         		News news = null;        		
         		//�ж��Ƿ���TextView
         		if(view instanceof TextView){
         			news = (News)view.getTag();
         		}else{
         			TextView tv = (TextView)view.findViewById(R.id.news_listitem_title);
         			news = (News)tv.getTag();
         		}
         		if(news == null) return;
         		Log.d( TAG , "click the hotels data ");
         		//��ת����������
         		//UIHelper.showNewsRedirect(view.getContext(), news);
         	}        	
 		});
    	
    }
    private void initRankThreadsListView()
    {
    	lvRankAdapter = new ListViewNewsAdapter(this, lvRankData, R.layout.news_listitem);        
    	lvRank_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
    	lvRank_foot_more = (TextView)lvThreads_footer.findViewById(R.id.listview_foot_more);
    	lvRank_foot_progress = (ProgressBar)lvThreads_footer.findViewById(R.id.listview_foot_progress);
    	lvRank = (PullToRefreshListView)findViewById( R.id.frame_listview_threads_rank );
    	lvRank.addFooterView(lvRank_footer);//��ӵײ���ͼ  ������setAdapterǰ
    	lvRank.setAdapter( lvRankAdapter ); 
    	lvRank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         		//���ͷ�����ײ�����Ч
         		if(position == 0 || view == lvRank_footer) return;
         		
         		News news = null;        		
         		//�ж��Ƿ���TextView
         		if(view instanceof TextView){
         			news = (News)view.getTag();
         		}else{
         			TextView tv = (TextView)view.findViewById(R.id.news_listitem_title);
         			news = (News)tv.getTag();
         		}
         		if(news == null) {
         			return;
         		}         		    
         		UIHelper.showNewsRedirect(view.getContext(), news);         		
         	}        	
 		});
    	lvRank.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvRank.onScrollStateChanged(view, scrollState);
				
				//����Ϊ��--���ü������������
				if(lvRankData.size() == 0) {
					return;
				}
				
				//�ж��Ƿ�������ײ�
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvRank_footer) == view.getLastVisiblePosition()) {
						scrollEnd = true;
					}
				} catch (Exception e) {
					scrollEnd = false;
				}		
				
				int lvDataState = StringUtils.toInt(lvRank.getTag());				
				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvRank_foot_more.setText(R.string.load_ing);
					lvRank_foot_progress.setVisibility(View.VISIBLE);
					//��ǰpageIndex
					int pageIndex = lvRankSumData/AppContext.PAGE_SIZE;
					Log.d( TAG , "ready load " + pageIndex + " rank page data ");
					loadLvRankThreadsData(curNewsCatalog, pageIndex, lvRankHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				lvRank.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
    	lvRank.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
            	loadLvRankThreadsData( curNewsCatalog, 0, lvRankHandler, UIHelper.LISTVIEW_ACTION_REFRESH );            	
            }
        });	
    	
    }
  
    private void initLatestThreadsListView()
    {
        lvThreadsAdapter = new ListViewNewsAdapter(this, lvThreadsData, R.layout.news_listitem);        
        lvThreads_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvThreads_foot_more = (TextView)lvThreads_footer.findViewById(R.id.listview_foot_more);
        lvThreads_foot_progress = (ProgressBar)lvThreads_footer.findViewById(R.id.listview_foot_progress);
        lvThreads = (PullToRefreshListView)findViewById( R.id.frame_listview_threads_latest );
        lvThreads.addFooterView(lvThreads_footer);//��ӵײ���ͼ  ������setAdapterǰ
        lvThreads.setAdapter(lvThreadsAdapter); 
        lvThreads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//���ͷ�����ײ�����Ч
        		if(position == 0 || view == lvThreads_footer) {
        			return;
        		}
        		
        		News news = null;        		
        		//�ж��Ƿ���TextView
        		if(view instanceof TextView){
        			news = (News)view.getTag();
        		}else{
        			TextView tv = (TextView)view.findViewById(R.id.news_listitem_title);
        			news = (News)tv.getTag();
        		}
        		if(news == null) {
        			return;
        		}
        		
        		//��ת����������
        		UIHelper.showNewsRedirect(view.getContext(), news);
        	}        	
		});
        lvThreads.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvThreads.onScrollStateChanged(view, scrollState);			
				
				//����Ϊ��--���ü������������
				if(lvThreadsData.size() == 0) {
					return;
				}
				
				//�ж��Ƿ�������ײ�
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvThreads_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}				
				int lvDataState = StringUtils.toInt(lvThreads.getTag());
				Log.d( TAG , "scroll end = " + scrollEnd + ", data state = " + lvDataState );
				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvThreads_foot_more.setText(R.string.load_ing);
					lvThreads_foot_progress.setVisibility(View.VISIBLE);
					//��ǰpageIndex
					int pageIndex = lvNewsSumData/AppContext.PAGE_SIZE;
					Log.d( TAG , "ready load " + pageIndex + " page data ");
					loadLvLatestNewsData(curNewsCatalog, pageIndex, lvThreadsHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				lvThreads.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        lvThreads.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
            	Log.d( TAG , "on refresh latest threads data ");            		
            	loadLvLatestNewsData(curNewsCatalog, 0, lvThreadsHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });					
    }
    /**
     * ��ʼ�������б�
     */
	/*private void initBlogListView()
    {
        lvBlogAdapter = new ListViewBlogAdapter(this, BlogList.CATALOG_LATEST, lvBlogData, R.layout.blog_listitem);        
        lvBlog_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvBlog_foot_more = (TextView)lvBlog_footer.findViewById(R.id.listview_foot_more);
        lvBlog_foot_progress = (ProgressBar)lvBlog_footer.findViewById(R.id.listview_foot_progress);
        lvBlog = (PullToRefreshListView)findViewById( R.id.frame_listview_blog);
        lvBlog.addFooterView(lvBlog_footer);//��ӵײ���ͼ  ������setAdapterǰ
        lvBlog.setAdapter(lvBlogAdapter); 
        lvBlog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//���ͷ�����ײ�����Ч
        		if(position == 0 || view == lvBlog_footer) return;
        		
        		Blog blog = null;        		
        		//�ж��Ƿ���TextView
        		if(view instanceof TextView){
        			blog = (Blog)view.getTag();
        		}else{
        			TextView tv = (TextView)view.findViewById(R.id.blog_listitem_title);
        			blog = (Blog)tv.getTag();
        		}
        		if(blog == null) return;
        		
        		//��ת����������
        		UIHelper.showUrlRedirect(view.getContext(), blog.getUrl());
        	}        	
		});
        lvBlog.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvBlog.onScrollStateChanged(view, scrollState);
				
				//����Ϊ��--���ü������������
				if(lvBlogData.size() == 0) return;
				
				//�ж��Ƿ�������ײ�
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvBlog_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(lvBlog.getTag());
				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvBlog_foot_more.setText(R.string.load_ing);
					lvBlog_foot_progress.setVisibility(View.VISIBLE);
					//��ǰpageIndex
					int pageIndex = lvBlogSumData/AppContext.PAGE_SIZE;
					loadLvBlogData(curNewsCatalog, pageIndex, lvBlogHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				lvBlog.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        lvBlog.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
            	loadLvBlogData(curNewsCatalog, 0, lvBlogHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });					
    }
    /**
     * ��ʼ�������б�
     */
  /*  private void initQuestionListView()
    {    	
        lvQuestionAdapter = new ListViewQuestionAdapter(this, lvQuestionData, R.layout.question_listitem);        
        lvQuestion_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvQuestion_foot_more = (TextView)lvQuestion_footer.findViewById(R.id.listview_foot_more);
        lvQuestion_foot_progress = (ProgressBar)lvQuestion_footer.findViewById(R.id.listview_foot_progress);
        lvQuestion = (PullToRefreshListView)findViewById(R.id.frame_listview_question);
        lvQuestion.addFooterView(lvQuestion_footer);//��ӵײ���ͼ  ������setAdapterǰ
        lvQuestion.setAdapter(lvQuestionAdapter); 
        lvQuestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//���ͷ�����ײ�����Ч
        		if(position == 0 || view == lvQuestion_footer) return;
        		
        		Post post = null;		
        		//�ж��Ƿ���TextView
        		if(view instanceof TextView){
        			post = (Post)view.getTag();
        		}else{
        			TextView tv = (TextView)view.findViewById(R.id.question_listitem_title);
        			post = (Post)tv.getTag();
        		}
        		if(post == null) return;
        		
        		//��ת����������
        		UIHelper.showQuestionDetail(view.getContext(), post.getId());
        	}        	
		});
        lvQuestion.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvQuestion.onScrollStateChanged(view, scrollState);
				
				//����Ϊ��--���ü������������
				if(lvQuestionData.size() == 0) return;
				
				//�ж��Ƿ�������ײ�
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvQuestion_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(lvQuestion.getTag());
				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvQuestion_foot_more.setText(R.string.load_ing);
					lvQuestion_foot_progress.setVisibility(View.VISIBLE);
					//��ǰpageIndex
					int pageIndex = lvQuestionSumData/AppContext.PAGE_SIZE;
					loadLvQuestionData(curQuestionCatalog, pageIndex, lvQuestionHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				lvQuestion.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        lvQuestion.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
            	loadLvQuestionData(curQuestionCatalog, 0, lvQuestionHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });			
    }
    /**
     * ��ʼ�������б�
     */
    /*private void initTweetListView()
    {   
        lvTweetAdapter = new ListViewTweetAdapter(this, lvTweetData, R.layout.tweet_listitem);        
        lvTweet_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvTweet_foot_more = (TextView)lvTweet_footer.findViewById(R.id.listview_foot_more);
        lvTweet_foot_progress = (ProgressBar)lvTweet_footer.findViewById(R.id.listview_foot_progress);
        lvTweet = (PullToRefreshListView)findViewById(R.id.frame_listview_tweet);
        lvTweet.addFooterView(lvTweet_footer);//��ӵײ���ͼ  ������setAdapterǰ
        lvTweet.setAdapter(lvTweetAdapter); 
        lvTweet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//���ͷ�����ײ�����Ч
        		if(position == 0 || view == lvTweet_footer) return;
        		
        		Tweet tweet = null;	
        		//�ж��Ƿ���TextView
        		if(view instanceof TextView){
        			tweet = (Tweet)view.getTag();
        		}else{
        			TextView tv = (TextView)view.findViewById(R.id.tweet_listitem_username);
        			tweet = (Tweet)tv.getTag();
        		}
        		if(tweet == null) return;        		
        		
        		//��ת����������&����ҳ��
        		UIHelper.showTweetDetail(view.getContext(), tweet.getId());
        	}        	
		});
        lvTweet.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvTweet.onScrollStateChanged(view, scrollState);
				
				//����Ϊ��--���ü������������
				if(lvTweetData.size() == 0) return;
				
				//�ж��Ƿ�������ײ�
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvTweet_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(lvTweet.getTag());
				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvTweet_foot_more.setText(R.string.load_ing);
					lvTweet_foot_progress.setVisibility(View.VISIBLE);
					//��ǰpageIndex
					int pageIndex = lvTweetSumData/AppContext.PAGE_SIZE;
					loadLvTweetData(curTweetCatalog, pageIndex, lvTweetHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				lvTweet.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        lvTweet.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				//���ͷ�����ײ�����Ч
        		if(position == 0 || view == lvTweet_footer) return false;
				
				Tweet _tweet = null;
        		//�ж��Ƿ���TextView
        		if(view instanceof TextView){
        			_tweet = (Tweet)view.getTag();
        		}else{
    				TextView tv = (TextView)view.findViewById(R.id.tweet_listitem_username);
        			_tweet = (Tweet)tv.getTag();
        		} 
        		if(_tweet == null) return false;
        		
        		final Tweet tweet = _tweet;				
				
				//ɾ������
        		if(appContext.getLoginUid() == tweet.getAuthorId()) {
					final Handler handler = new Handler(){
						public void handleMessage(Message msg) {
							if(msg.what == 1){
								Result res = (Result)msg.obj;
								if(res.OK()){
									lvTweetData.remove(tweet);
									lvTweetAdapter.notifyDataSetChanged();
								}
								UIHelper.ToastMessage(Main.this, res.getErrorMessage());
							}else{
								((AppException)msg.obj).makeToast(Main.this);
							}
						}
					};
					Thread thread = new Thread(){
						public void run() {
							Message msg = new Message();
							try {
								Result res = appContext.delTweet(appContext.getLoginUid(),tweet.getId());
								msg.what = 1;
								msg.obj = res;
							} catch (AppException e) {
				            	e.printStackTrace();
				            	msg.what = -1;
				            	msg.obj = e;
							}
							handler.sendMessage(msg);
						}
					};
					UIHelper.showTweetOptionDialog(Main.this, thread);
        		} else {
        			UIHelper.showTweetOptionDialog(Main.this, null);
        		}
				return true;
			}        	
		});
        lvTweet.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
            	loadLvTweetData(curTweetCatalog, 0, lvTweetHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });			
    }*/
   
    private void initHeadView()
    {
    	mHeadLogo = (ImageView)findViewById(R.id.main_head_logo);
    	mHeadTitle = (TextView)findViewById(R.id.main_head_title);
    	mHeadProgress = (ProgressBar)findViewById(R.id.main_head_progress);
    	mHead_search = (ImageButton)findViewById( R.id.main_head_search);
    	//mHeadPub_post = (ImageButton)findViewById(R.id.main_head_pub_post);
    	//mHeadPub_tweet = (ImageButton)findViewById(R.id.main_head_pub_tweet);
  
    	if ( mHead_search == null ) {
    		Log.d(TAG,"get head search null");
    	}
    	mHead_search.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UIHelper.showSearch(v.getContext());
			}
		});
    	/*mHeadPub_post.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UIHelper.showQuestionPub(v.getContext());
			}
		});
    	mHeadPub_tweet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UIHelper.showTweetPub(Main.this);
			}
		});*/
    }
    /**
     * ��ʼ���ײ���
     */
    private void initFootBar()
    {
    	fbThreads = (RadioButton)findViewById(R.id.main_footbar_threads);
    	fbMsg    = (RadioButton)findViewById(R.id.main_footbar_msg);
    	fbFavorite = (RadioButton)findViewById( R.id.main_footbar_favorite );
    	
    	
    	fbSetting = (ImageView)findViewById(R.id.main_footbar_setting);
    	fbSetting.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {    			
    			//չʾ�����&�ж��Ƿ��¼&�Ƿ��������ͼƬ
    			UIHelper.showSettingLoginOrLogout(Main.this, mGrid.getQuickAction(0));
    			mGrid.show(v);
    		}
    	});    	
    }
    /**
     * ��ʼ��֪ͨ��Ϣ��ǩ�ؼ�
     */
    private void initBadgeView()
    {
    	/*bv_active = new BadgeView(this, fbactive);
		bv_active.setBackgroundResource(R.drawable.widget_count_bg);
    	bv_active.setIncludeFontPadding(false);
    	bv_active.setGravity(Gravity.CENTER);
    	bv_active.setTextSize(8f);
    	bv_active.setTextColor(Color.WHITE);
    	
    	bv_atme = new BadgeView(this, framebtn_Active_atme);
    	bv_atme.setBackgroundResource(R.drawable.widget_count_bg);
    	bv_atme.setIncludeFontPadding(false);
    	bv_atme.setGravity(Gravity.CENTER);
    	bv_atme.setTextSize(8f);
    	bv_atme.setTextColor(Color.WHITE);
    	
    	bv_review = new BadgeView(this, framebtn_Active_comment);
    	bv_review.setBackgroundResource(R.drawable.widget_count_bg);
    	bv_review.setIncludeFontPadding(false);
    	bv_review.setGravity(Gravity.CENTER);
    	bv_review.setTextSize(8f);
    	bv_review.setTextColor(Color.WHITE);
    	
    	bv_message = new BadgeView(this, framebtn_Active_message);
    	bv_message.setBackgroundResource(R.drawable.widget_count_bg);
    	bv_message.setIncludeFontPadding(false);
    	bv_message.setGravity(Gravity.CENTER);
    	bv_message.setTextSize(8f);
    	bv_message.setTextColor(Color.WHITE);*/
    }    
	/**
     * ��ʼ��ˮƽ������ҳ
     */
    private void initPageScroll()
    {
    	mScrollLayout = (ScrollLayout) findViewById(R.id.main_scrolllayout);
    	
    	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_linearlayout_footer);
    	mHeadTitles = getResources().getStringArray(R.array.head_titles);
    	mViewCount = 3; //linearLayout.getChildCount() / 2 ;
    	mButtons = new RadioButton[mViewCount];
  
    	for(int i = 0; i < mViewCount; i++)
    	{
    		switch( i ){
    		case 0:
    			mButtons[i] = fbThreads;
    			break;
    		case 1:
    			mButtons[i] = fbMsg;
    			break;
    		case 2:
    			mButtons[i] = fbFavorite;
    			break;
    		}
    		mButtons[i].setTag(i);
    		mButtons[i].setChecked(false);
    		mButtons[i].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {					
					int pos = (Integer)(v.getTag());
					Log.d( "Main" , "user click pos = " + pos + ",m cur select = " + mCurSel );
					//��ǰ����ˢ��
					switch( pos ) {
					case 2:
						loadLvUserFavoriteData( curNewsCatalog, 0, m_UserFavoritesHandler , UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG );
						break;
					}
				
					setCurPoint(pos);
					mScrollLayout.snapToScreen(pos);
				}
			});
    	}
    	
    	//���õ�һ��ʾ��
    	mCurSel = 0;
    	mButtons[mCurSel].setChecked(true);
    	
    	mScrollLayout.SetOnViewChangeListener(new ScrollLayout.OnViewChangeListener() {
			public void OnViewChange(int viewIndex) {
				setCurPoint(viewIndex);
			}
		});
    }
    /**
     * ���õײ�����ǰ����
     * @param index
     */
    private void setCurPoint(int index)
    {
    	if (index < 0 || index > mViewCount - 1 || mCurSel == index)
    		return;
   	
    	mButtons[mCurSel].setChecked(false);
    	mButtons[index].setChecked(true);    	
    	mHeadTitle.setText(mHeadTitles[index]);    	
    	mCurSel = index;
    	
    	mHead_search.setVisibility(View.GONE);
    	//mHeadPub_post.setVisibility(View.GONE);
    	//mHeadPub_tweet.setVisibility(View.GONE);
		//ͷ��logo����������������ť��ʾ
    	if(index == 0){
    		mHeadLogo.setImageResource(R.drawable.frame_logo_news);
    		mHead_search.setVisibility(View.VISIBLE);
    	}
    	else if(index == 1){
    		mHeadLogo.setImageResource(R.drawable.widget_bar_mail_nor);
    		//mHeadPub_post.setVisibility(View.VISIBLE);
    	}
    	else if(index == 2){
    		mHeadLogo.setImageResource(R.drawable.head_favorite_p);
    		//mHeadPub_tweet.setVisibility(View.VISIBLE);
    	}
    	//����֪ͨ��Ϣ
    	else if(index == 3){
    		/*mHeadLogo.setImageResource(R.drawable.frame_logo_active);
    		//mHeadPub_tweet.setVisibility(View.VISIBLE);
    		
    		//�жϵ�¼
			int uid = appContext.getLoginUid();
			if(uid == 0){
				UIHelper.showLoginDialog(Main.this);
				return;
			} */   		
		}
    }
  
    private void initFrameButton()
    {    	
    	//��ʼ����ť�ؼ�
    	framebtn_Threads_lastest = (Button)findViewById( R.id.frame_btn_threads_latest );
    	framebtn_Threads_rank    = (Button)findViewById( R.id.frame_btn_threads_rank );
    	framebtn_Activities  = (Button)findViewById( R.id.frame_btn_new_activities );
    	
    	//������ѡ����
    	framebtn_Threads_lastest.setEnabled( false );
    	framebtn_Threads_rank.setEnabled(true);
    	framebtn_Activities.setEnabled(true); 
    	
    
    	framebtn_Threads_lastest.setOnClickListener(frameNewsBtnClick(framebtn_Threads_lastest,NewsList.CATALOG_ALL));
    	framebtn_Threads_rank.setOnClickListener( frameNewsBtnClick( framebtn_Threads_rank , NewsList.CATALOG_ALL ) );
    	framebtn_Activities.setOnClickListener( frameNewsBtnClick( framebtn_Activities , NewsList.CATALOG_ALL ) );
    	
    	
    }
    private View.OnClickListener frameNewsBtnClick(final Button btn,final int catalog){
    	return new View.OnClickListener() {
			public void onClick(View v) {		
		    	if(btn == framebtn_Threads_lastest){
		    		framebtn_Threads_lastest.setEnabled(false);		    	
		    	}else{
		    		framebtn_Threads_lastest.setEnabled(true);
		    	}
		    	if(btn == framebtn_Threads_rank){
		    		framebtn_Threads_rank.setEnabled(false);
		    	}else{
		    		framebtn_Threads_rank.setEnabled(true);
		    	}
		    	if(btn == framebtn_Activities){
		    		framebtn_Activities.setEnabled(false);
		    	}else{
		    		framebtn_Activities.setEnabled(true);
		    	}
		   

		    	curNewsCatalog = catalog;
		    	
				//�������б�
		    	if(btn == framebtn_Threads_lastest)
		    	{
		    		lvThreads.setVisibility(View.VISIBLE);
		    		lvRank.setVisibility(View.GONE);
		    		lvActivities.setVisibility(View.GONE);
		    		
		    		lvThreads_foot_more.setText(R.string.load_more);
					lvThreads_foot_progress.setVisibility(View.GONE);
					
					loadLvLatestNewsData(curNewsCatalog, 0, lvThreadsHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);					
					
		    	}
		    	else if ( btn == framebtn_Threads_rank )
		    	{
		    		lvThreads.setVisibility(View.GONE);
		    		lvRank.setVisibility(View.VISIBLE );
		    		lvActivities.setVisibility(View.GONE);		    		    		
		    		loadLvRankThreadsData( curNewsCatalog, 0, lvRankHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG );	    
		    	}
		    	else if ( btn == framebtn_Activities ) {
		    		Log.d( TAG , "click activities frame button ");
		    		lvThreads.setVisibility(View.GONE);
		    		lvRank.setVisibility(View.GONE );
		    		lvActivities.setVisibility(View.VISIBLE);
		    		int firstPage = 0;
		    		loadLvActivitiesData( curNewsCatalog, firstPage , lvActivitiesHandler , UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG );
		    	}else {
		    		Log.d( TAG , "unknown buttons ");
		    	}
			}
		};
    }
    /*private View.OnClickListener frameQuestionBtnClick(final Button btn,final int catalog){
    	return new View.OnClickListener() {
			public void onClick(View v) {
		    	if(btn == framebtn_Question_ask)
		    		framebtn_Question_ask.setEnabled(false);
		    	else
		    		framebtn_Question_ask.setEnabled(true);
		    	if(btn == framebtn_Question_share)
		    		framebtn_Question_share.setEnabled(false);
		    	else
		    		framebtn_Question_share.setEnabled(true);
		    	if(btn == framebtn_Question_other)
		    		framebtn_Question_other.setEnabled(false);
		    	else
		    		framebtn_Question_other.setEnabled(true);
		    	if(btn == framebtn_Question_job)
		    		framebtn_Question_job.setEnabled(false);
		    	else
		    		framebtn_Question_job.setEnabled(true);
		    	if(btn == framebtn_Question_site)
		    		framebtn_Question_site.setEnabled(false);
		    	else
		    		framebtn_Question_site.setEnabled(true);
		    	
				lvQuestion_foot_more.setText(R.string.load_more);
				lvQuestion_foot_progress.setVisibility(View.GONE);
				
				curQuestionCatalog = catalog;
				loadLvQuestionData(curQuestionCatalog, 0, lvQuestionHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
			}
		};
    }
    private View.OnClickListener frameTweetBtnClick(final Button btn,final int catalog){
    	return new View.OnClickListener() {
			public void onClick(View v) {
		    	if(btn == framebtn_Tweet_lastest)
		    		framebtn_Tweet_lastest.setEnabled(false);
		    	else
		    		framebtn_Tweet_lastest.setEnabled(true);
		    	if(btn == framebtn_Tweet_hot)
		    		framebtn_Tweet_hot.setEnabled(false);
		    	else
		    		framebtn_Tweet_hot.setEnabled(true);
		    	if(btn == framebtn_Tweet_my)
		    		framebtn_Tweet_my.setEnabled(false);
		    	else
		    		framebtn_Tweet_my.setEnabled(true);	
		    	
				lvTweet_foot_more.setText(R.string.load_more);
				lvTweet_foot_progress.setVisibility(View.GONE);
				
				curTweetCatalog = catalog;
				loadLvTweetData(curTweetCatalog, 0, lvTweetHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);		    	
			}
		};
    }
    private View.OnClickListener frameActiveBtnClick(final Button btn,final int catalog){
    	return new View.OnClickListener() {
			public void onClick(View v) {
				//�жϵ�¼
				int uid = appContext.getLoginUid();
				if(uid == 0){
					UIHelper.showLoginDialog(Main.this);
					return;
				}
				
				frameActiveBtnOnClick(btn, catalog);
			}
		};
    }
    private void frameActiveBtnOnClick(Button btn, int catalog) {
    	frameActiveBtnOnClick(btn, catalog, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
    }
    private void frameActiveBtnOnClick(Button btn, int catalog, int action) {
    	if(btn == framebtn_Active_lastest)
    		framebtn_Active_lastest.setEnabled(false);
    	else
    		framebtn_Active_lastest.setEnabled(true);
    	if(btn == framebtn_Active_atme)
    		framebtn_Active_atme.setEnabled(false);
    	else
    		framebtn_Active_atme.setEnabled(true);
    	if(btn == framebtn_Active_comment)
    		framebtn_Active_comment.setEnabled(false);
    	else
    		framebtn_Active_comment.setEnabled(true);
    	if(btn == framebtn_Active_myself)
    		framebtn_Active_myself.setEnabled(false);
    	else
    		framebtn_Active_myself.setEnabled(true);
    	if(btn == framebtn_Active_message)
    		framebtn_Active_message.setEnabled(false);
    	else
    		framebtn_Active_message.setEnabled(true);
    	
		//�Ƿ���֪ͨ��Ϣ
		if(btn == framebtn_Active_atme && bv_atme.isShown()){
			this.isClearNotice = true;
			this.curClearNoticeType = Notice.TYPE_ATME;
		}else if(btn == framebtn_Active_comment && bv_review.isShown()){
			this.isClearNotice = true;
			this.curClearNoticeType = Notice.TYPE_COMMENT;
		}else if(btn == framebtn_Active_message && bv_message.isShown()){
			this.isClearNotice = true;
			this.curClearNoticeType = Notice.TYPE_MESSAGE;
		}
    	
    	//������չʾ��̬�б�
    	if(btn != framebtn_Active_message)
    	{
    		lvActive.setVisibility(View.VISIBLE);
    		lvMsg.setVisibility(View.GONE);
    		
			lvActive_foot_more.setText(R.string.load_more);
			lvActive_foot_progress.setVisibility(View.GONE);
			
			curActiveCatalog = catalog;
			loadLvActiveData(curActiveCatalog, 0, lvActiveHandler, action);
    	}
    	else
    	{
    		lvActive.setVisibility(View.GONE);
    		lvMsg.setVisibility(View.VISIBLE);
    		
    		if(lvMsgData.size() == 0){
    			loadLvMsgData(0, lvMsgHandler, action);
    		}else{
    			lvMsg_foot_more.setText(R.string.load_more);
    			lvMsg_foot_progress.setVisibility(View.GONE);
    			
    			loadLvMsgData(0, lvMsgHandler, action);
    		}
    	}
    }*/

    private Handler getLvHandler(final PullToRefreshListView lv,final BaseAdapter adapter,final TextView more,final ProgressBar progress,final int pageSize){
    	return new Handler(){
			public void handleMessage(Message msg) {
				Log.d( "Main" , "handle message , what = " + msg.what );
				if(msg.what >= 0){
					//listview���ݴ���
					Notice notice = handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);										
					if(msg.what < pageSize){
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);
					}else if(msg.what == pageSize){
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);			
					}
					//����֪ͨ�㲥
					if(notice != null){
						UIHelper.sendBroadCast(lv.getContext(), notice);
					}
					//�Ƿ����֪ͨ��Ϣ
					if(isClearNotice){
						ClearNotice(curClearNoticeType);
						isClearNotice = false;//����
						curClearNoticeType = 0;
					}
				}
				else if(msg.what == -1){
					//���쳣--��ʾ���س��� & ����������Ϣ
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(Main.this);
				}
				if(adapter.getCount()==0){
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}
				progress.setVisibility(ProgressBar.GONE);
				mHeadProgress.setVisibility(ProgressBar.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
					lv.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					lv.setSelection(0);
				}else if(msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG){
					lv.onRefreshComplete();
					lv.setSelection(0);
				}
				if ( msg.arg2 == UIHelper.LISTVIEW_DATATYPE_DISCOUNT_ACTIVITIES ) {
					Log.d( "Main" , "load discount msg");
				}
			}
		};
    }
    /**
     * listview���ݴ���
     * @param what ����
     * @param obj ����
     * @param objtype ��������
     * @param actiontype ��������
     * @return notice ֪ͨ��Ϣ
     */
    private Notice handleLvData(int what,Object obj,int objtype,int actiontype){
    	Notice notice = null;
    	int totalPageSize = what;
		switch (actiontype) {
			case UIHelper.LISTVIEW_ACTION_INIT:
			case UIHelper.LISTVIEW_ACTION_REFRESH:
			case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
				switch (objtype) {
					case UIHelper.LISTVIEW_DATATYPE_LATEST_THREADS:
						NewsList nlist = (NewsList)obj;
						notice = nlist.getNotice();
						lvNewsSumData = totalPageSize;
						lvThreadsData.clear();
						lvThreadsData.addAll( nlist.getNewslist() );					
						break;
					case UIHelper.LISTVIEW_DATATYPE_RANK_THREADS:
						NewsList rlist = (NewsList)obj;
						notice = rlist.getNotice();
						lvRankSumData = totalPageSize;
						lvRankData.clear();
						lvRankData.addAll( rlist.getNewslist() );
						break;
					case UIHelper.LISTVIEW_DATATYPE_MYFAVORITE_DATA:
						NewsList flist = (NewsList)obj;
						lvFavoriteSumData = totalPageSize;
						m_lvFaviritesData.clear();
						m_lvFaviritesData.addAll( flist.getNewslist() );
						break;
					case UIHelper.LISTVIEW_DATATYPE_DISCOUNT_ACTIVITIES:
						NewsList discountList = (NewsList)obj;
						lvDiscountSumData = totalPageSize;
						m_lvDiscountData.clear();
						m_lvDiscountData.addAll( discountList.getNewslist() );						
						break;						
				}
				break;
			case UIHelper.LISTVIEW_ACTION_SCROLL:
				switch (objtype) {
					case UIHelper.LISTVIEW_DATATYPE_LATEST_THREADS:
						NewsList list = (NewsList)obj;
						notice = list.getNotice();
						lvNewsSumData += what;
						if(lvThreadsData.size() > 0){
							for(News news1 : list.getNewslist()){
								boolean b = false;
								for(News news2 : lvThreadsData){
									if(news1.getId() == news2.getId()){
										b = true;
										break;
									}
								}
								if(!b) {
									lvThreadsData.add(news1);
								}
							}
						}else{
							lvThreadsData.addAll(list.getNewslist());
						}
						break;					
					case UIHelper.LISTVIEW_DATATYPE_RANK_THREADS:
						NewsList rlist = ( NewsList)obj;
						lvRankSumData += what;
						if ( lvRankData.size() > 0 ) {
							for(News news1 : rlist.getNewslist()){
								boolean b = false;
								for(News news2 : lvRankData){
									if(news1.getId() == news2.getId()){
										b = true;
										break;
									}
								}
								if(!b) {
									lvRankData.add(news1);
								}
							}
						}else {
							lvRankData.addAll( rlist.getNewslist() );
						}						
						break;
					case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
					
						break;
					case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
						
						break;
				}
				break;
		}
		return notice;
    }
    
    private void loadLvUserFavoriteData(final int catalog,final int pageIndex,final Handler handler,final int action) {
    	mHeadProgress.setVisibility(ProgressBar.VISIBLE);
    	
    	new Thread() {
    		public void run() {
    			Message msg = new Message();
    			boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL) {
					isRefresh = true;
				}
				NewsList list = appContext.getMyFavorite( pageIndex, isRefresh);		
				if ( list != null ) {
					Log.d( "Main" , "get my favorite list success , list size = " + list.getNewslist().size() );
					msg.what = list.getPageSize();
					msg.obj = list;
				}else {
					Log.d( "Main" , "get my favorite list failed");
					msg.what = 0;
					msg.obj = null;
				}
	           
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_MYFAVORITE_DATA;
                if(curNewsCatalog == catalog) {
                	handler.sendMessage(msg);
                }
    		}
    		
    	}.start();
    }

	private void loadLvLatestNewsData(final int catalog,final int pageIndex,final Handler handler,final int action){ 
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);		
		new Thread(){
			public void run() {				
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL) {
					isRefresh = true;
				}
				try {					
					//��ȡ���������б�
					NewsList list = appContext.getLatestThreadsList(catalog, pageIndex, isRefresh);		
					if ( list != null ) {
						msg.what = list.getPageSize();
						msg.obj = list;
					}
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_LATEST_THREADS;
                if(curNewsCatalog == catalog) {
                	handler.sendMessage(msg);
                }
			}
		}.start();
	} 
	
	private void loadLvRankThreadsData(final int catalog,final int pageIndex,final Handler handler,final int action){ 
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);		
		new Thread(){
			public void run() {				
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL) {
					isRefresh = true;
				}
				try {					
					NewsList list = appContext.getRankThreadsList(catalog, pageIndex, isRefresh);					
					msg.what = list.getPageSize();
					msg.obj = list;
				
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_RANK_THREADS;
                if(curNewsCatalog == catalog) {
                	handler.sendMessage(msg);
                }
			}
		}.start();
	} 
	private void loadLvActivitiesData(final int catalog,final int pageIndex,final Handler handler,final int action) {
		mHeadProgress.setVisibility( ProgressBar.VISIBLE );
		new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL) {
					isRefresh = true;
				}
				try {					
					NewsList list = appContext.getDiscountActivitiesList(catalog, pageIndex, isRefresh);					
					msg.what = list.getPageSize();
					msg.obj = list;
				
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_DISCOUNT_ACTIVITIES;
                if(curNewsCatalog == catalog) {
                	handler.sendMessage(msg);
                }
				
			}
		}.start();		
	}
  
	
	/**
	 * ��ѯ֪ͨ��Ϣ
	 */
	private void foreachUserNotice(){
		final int uid = appContext.getLoginUid();
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==1){
					UIHelper.sendBroadCast(Main.this, (Notice)msg.obj);
				}
				foreachUserNotice();//�ص�
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					sleep(60*1000);
					if (uid > 0) {
						Notice notice = appContext.getUserNotice(uid);
						msg.what = 1;
						msg.obj = notice;
					} else {
						msg.what = 0;
					}					
				} catch (AppException e) {
					e.printStackTrace();
	            	msg.what = -1;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
	 * ֪ͨ��Ϣ����
	 * @param type 1:@�ҵ���Ϣ 2:δ����Ϣ 3:���۸��� 4:�·�˿����
	 */
	private void ClearNotice(final int type)
	{
		final int uid = appContext.getLoginUid();
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==1 && msg.obj != null){
					Result res = (Result)msg.obj;
					if(res.OK() && res.getNotice()!=null){
						UIHelper.sendBroadCast(Main.this, res.getNotice());
					}
				}else{
					((AppException)msg.obj).makeToast(Main.this);
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					Result res = appContext.noticeClear(uid, type);
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
	
	/**
	 * ����menu TODO ͣ��ԭ���˵�
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
		//MenuInflater inflater = getMenuInflater();
		//inflater.inflate(R.menu.main_menu, menu);
		//return true;
	}
	
	/**
	 * �˵�����ʾ֮ǰ���¼�
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		UIHelper.showMenuLoginOrLogout(this, menu);
		return true;
	}

	/**
	 * ����menu���¼�
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();
		switch (item_id) {
		case R.id.main_menu_user:
			UIHelper.loginOrLogout(this);
			break;
		case R.id.main_menu_about:
			UIHelper.showAbout(this);
			break;
		case R.id.main_menu_setting:
			UIHelper.showSetting(this);
			break;
		case R.id.main_menu_exit:
			UIHelper.Exit(this);
			break;
		}
		return true;
	}
	
	/**
	 * ��������--�Ƿ��˳�����
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			//�Ƿ��˳�Ӧ��
			UIHelper.Exit(this);
		}else if(keyCode == KeyEvent.KEYCODE_MENU){
			//չʾ�����&�ж��Ƿ��¼
			UIHelper.showSettingLoginOrLogout(Main.this, mGrid.getQuickAction(0));
			mGrid.show(fbSetting, true);
		}else if(keyCode == KeyEvent.KEYCODE_SEARCH){
			//չʾ����ҳ
			UIHelper.showSearch(Main.this);
		}
		return true;
	}
}
