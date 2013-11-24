package net.oschina.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.app.R;
import net.oschina.app.adapter.ListViewMessageDetailAdapter;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Result;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.widget.PullToRefreshListView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * ��������
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class MessageDetail extends Activity{
	
	private ImageView mBack;
	private ImageView mRefresh;
	private TextView mHeadTitle;
	private ProgressBar mProgressbar;
	
	private PullToRefreshListView mLvComment;
	private ListViewMessageDetailAdapter lvCommentAdapter;
	private List<Comment> lvCommentData = new ArrayList<Comment>();
	private View lvComment_footer;
	private TextView lvComment_foot_more;
	private ProgressBar lvComment_foot_progress;
    private ProgressDialog mProgress;
    private Handler mHandler;
    private int lvSumData;
    
    private int curFriendId;
    private String curFriendName;
	private int curCatalog;	
	private int curLvDataState;
	
	private ViewSwitcher mFootViewSwitcher;
	private ImageView mFootEditebox;
	private EditText mFootEditer;
	private Button mFootPubcomment;
	private InputMethodManager imm;
	private String tempMessageKey = AppConfig.TEMP_MESSAGE;
	
	private int _uid;
	private int _friendid;
	private String _content;
	
	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail);
        
        this.initView();        
        this.initData();        
    }
    
    /**
     * ͷ������չʾ
     * @param type
     */
    private void headButtonSwitch(int type) {
    	switch (type) {
		case DATA_LOAD_ING:
			mProgressbar.setVisibility(View.VISIBLE);
			mRefresh.setVisibility(View.GONE);
			break;
		case DATA_LOAD_COMPLETE:
			mProgressbar.setVisibility(View.GONE);
			mRefresh.setVisibility(View.VISIBLE);
			break;
		}
    }
    
    //��ʼ����ͼ�ؼ�
    private void initView()
    {
		curFriendId = getIntent().getIntExtra("friend_id", 0);
		curFriendName = getIntent().getStringExtra("friend_name");
		curCatalog = CommentList.CATALOG_MESSAGE;

		if(curFriendId > 0) tempMessageKey = AppConfig.TEMP_MESSAGE + "_" + curFriendId;
		
    	mBack = (ImageView)findViewById(R.id.message_detail_back);
    	mRefresh = (ImageView)findViewById(R.id.message_detail_refresh);
    	mHeadTitle = (TextView)findViewById(R.id.message_detail_head_title);
    	mProgressbar = (ProgressBar)findViewById(R.id.message_detail_head_progress);
    	
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE); 
    	
    	mFootViewSwitcher = (ViewSwitcher)findViewById(R.id.message_detail_foot_viewswitcher);
    	mFootPubcomment = (Button)findViewById(R.id.message_detail_foot_pubcomment);
    	mFootPubcomment.setOnClickListener(messagePubClickListener);
    	mFootEditebox = (ImageView)findViewById(R.id.message_detail_footbar_editebox);
    	mFootEditebox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFootViewSwitcher.showNext();
				mFootEditer.setVisibility(View.VISIBLE);
				mFootEditer.requestFocus();
				mFootEditer.requestFocusFromTouch();
			}
		});
    	mFootEditer = (EditText)findViewById(R.id.message_detail_foot_editer);
    	mFootEditer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){  
					imm.showSoftInput(v, 0);  
		        }  
		        else{  
		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
		        }  
			}
		}); 
    	mFootEditer.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if(mFootViewSwitcher.getDisplayedChild()==1){
						mFootViewSwitcher.setDisplayedChild(0);
						mFootEditer.clearFocus();
						mFootEditer.setVisibility(View.GONE);
					}
					return true;
				}
				return false;
			}
		});
    	//�༭������ı�����
    	mFootEditer.addTextChangedListener(UIHelper.getTextWatcher(this, tempMessageKey));
    	
    	//��ʾ��ʱ�༭����
    	UIHelper.showTempEditContent(this, mFootEditer, tempMessageKey);
    	
    	mHeadTitle.setText(getString(R.string.message_detail_head_title,curFriendName));
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mRefresh.setOnClickListener(refreshClickListener);
    	
    	lvComment_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
    	lvComment_foot_more = (TextView)lvComment_footer.findViewById(R.id.listview_foot_more);
        lvComment_foot_progress = (ProgressBar)lvComment_footer.findViewById(R.id.listview_foot_progress);

    	lvCommentAdapter = new ListViewMessageDetailAdapter(this, lvCommentData, R.layout.message_detail_listitem); 
    	mLvComment = (PullToRefreshListView)findViewById(R.id.message_list_listview);
    	
        mLvComment.addFooterView(lvComment_footer);//��ӵײ���ͼ  ������setAdapterǰ
        mLvComment.setAdapter(lvCommentAdapter); 
        mLvComment.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mLvComment.onScrollStateChanged(view, scrollState);
				
				//����Ϊ��--���ü������������
				if(lvCommentData.size() == 0) return;
				
				//�ж��Ƿ�������ײ�
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvComment_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				if(scrollEnd && curLvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvComment_foot_more.setText(R.string.load_ing);
					lvComment_foot_progress.setVisibility(View.VISIBLE);
					//��ǰpageIndex
					int pageIndex = lvSumData/20;
					loadLvCommentData(curFriendId, curCatalog, pageIndex, mHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				mLvComment.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        mLvComment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				//���ͷ�����ײ�����Ч
        		if(position == 0 || view == lvComment_footer) return false;
				
        		TextView username = (TextView)view.findViewById(R.id.messagedetail_listitem_username);
        		final Comment com = (Comment)username.getTag();
				
        		//����--ɾ��         		
        		final Handler handler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						if(msg.what == 1){
							Result res = (Result)msg.obj;
							if(res.OK()){
								lvSumData--;
								lvCommentData.remove(com);
								lvCommentAdapter.notifyDataSetChanged();
							}
							UIHelper.ToastMessage(MessageDetail.this, res.getErrorMessage());
						}else{
							((AppException)msg.obj).makeToast(MessageDetail.this);
						}
					}        			
        		};
        		//����--����������ɾ��
        		final Thread thread = new Thread(){
					public void run() {
						Message msg = new Message();
						try {
							Result res = ((AppContext)getApplication()).delComment(curFriendId, curCatalog, com.getId(), com.getAuthorId());
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
        		
        		UIHelper.showMessageDetailOptionDialog(MessageDetail.this, com, thread);
				return true;
			}        	
		});
        mLvComment.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadLvCommentData(curFriendId, curCatalog, 0, mHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }
    
    //��ʼ���ؼ�����
	private void initData()
	{			
    	mHandler = new Handler()
		{
			public void handleMessage(Message msg) {
				
				headButtonSwitch(DATA_LOAD_COMPLETE);
				
				if(msg.what >= 0){						
					CommentList list = (CommentList)msg.obj;
					Notice notice = list.getNotice();
					//����listview����
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
						lvSumData = msg.what;
						lvCommentData.clear();//�����ԭ������
						lvCommentData.addAll(list.getCommentlist());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						lvSumData += msg.what;
						if(lvCommentData.size() > 0){
							for(Comment com1 : list.getCommentlist()){
								boolean b = false;
								for(Comment com2 : lvCommentData){
									if(com1.getId() == com2.getId() && com1.getAuthorId() == com2.getAuthorId()){
										b = true;
										break;
									}
								}
								if(!b) lvCommentData.add(com1);
							}
						}else{
							lvCommentData.addAll(list.getCommentlist());
						}
						break;
					}	
					
					if(msg.what < 20){
						curLvDataState = UIHelper.LISTVIEW_DATA_FULL;
						lvCommentAdapter.notifyDataSetChanged();
						lvComment_foot_more.setText(R.string.load_full);
					}else if(msg.what == 20){					
						curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
						lvCommentAdapter.notifyDataSetChanged();
						lvComment_foot_more.setText(R.string.load_more);
					}
					//����֪ͨ�㲥
					if(notice != null){
						UIHelper.sendBroadCast(MessageDetail.this, notice);
					}
				}
				else if(msg.what == -1){
					//���쳣--Ҳ��ʾ���� & ����������Ϣ
					curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
					lvComment_foot_more.setText(R.string.load_more);
					((AppException)msg.obj).makeToast(MessageDetail.this);
				}
				if(lvCommentData.size()==0){
					curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
					lvComment_foot_more.setText(R.string.load_empty);
				}
				lvComment_foot_progress.setVisibility(View.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH)
					mLvComment.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
			}
		};
		this.loadLvCommentData(curFriendId,curCatalog,0,mHandler,UIHelper.LISTVIEW_ACTION_INIT);
    }
    /**
     * �̼߳�����������
     * @param id ��ǰ����id
     * @param catalog ����
     * @param pageIndex ��ǰҳ��
     * @param handler ������
     * @param action ������ʶ
     */
	private void loadLvCommentData(final int id,final int catalog,final int pageIndex,final Handler handler,final int action){  
		
		this.headButtonSwitch(DATA_LOAD_ING);
		
		new Thread(){
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					CommentList commentlist = ((AppContext)getApplication()).getCommentList(catalog, id, pageIndex, isRefresh);				
					msg.what = commentlist.getPageSize();
					msg.obj = commentlist;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//��֪handler��ǰaction
                handler.sendMessage(msg);
			}
		}.start();
	} 
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{ 
		if (resultCode != RESULT_OK) return;   
    	if (data == null) return;
        if (requestCode == UIHelper.REQUEST_CODE_FOR_RESULT) 
        { 
        	Comment comm = (Comment)data.getSerializableExtra("COMMENT_SERIALIZABLE");
        	lvCommentData.add(0, comm);
        	lvCommentAdapter.notifyDataSetChanged();
        	mLvComment.setSelection(0);
        }
	}
	
	private View.OnClickListener refreshClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			loadLvCommentData(curFriendId,curCatalog,0,mHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
		}
	};
	
	private View.OnClickListener messagePubClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(MessageDetail.this);
				return;
			}
			
			_uid = ac.getLoginUid();
			_friendid = curFriendId;
			
			if(_uid==0 || _friendid==0) return;
			
			_content = mFootEditer.getText().toString();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(v.getContext(), "��������������");
				return;
			}
			
			mProgress = ProgressDialog.show(v.getContext(), null, "�����С�����",true,true); 
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					
					if(mProgress!=null)mProgress.dismiss();
					
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(MessageDetail.this, res.getErrorMessage());
						if(res.OK()){
							//����֪ͨ�㲥
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(MessageDetail.this, res.getNotice());
							}
							//�ָ���ʼ�ײ���
							mFootViewSwitcher.setDisplayedChild(0);
							mFootEditer.clearFocus();
							mFootEditer.setText("");
							mFootEditer.setVisibility(View.GONE);
							//��ʾ�ոշ��͵�����
				        	lvCommentData.add(0, res.getComment());
				        	lvCommentAdapter.notifyDataSetChanged();
				        	mLvComment.setSelection(0);
				        	//���֮ǰ����ı༭����
							ac.removeProperty(tempMessageKey);
						}
					}
					else {
						((AppException)msg.obj).makeToast(MessageDetail.this);
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
