package net.oschina.app.adapter;

import java.util.List;

import net.oschina.app.AppContext;
import com.hkzhe.app.R;
import net.oschina.app.bean.Comment;
import net.oschina.app.common.BitmapManager;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.widget.LinkView;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * �û���������Adapter��
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewMessageDetailAdapter extends BaseAdapter {
	private Context 					context;//����������
	private List<Comment> 				listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ
	private BitmapManager 				bmpManager;
	static class ListItemView{				//�Զ���ؼ�����  
			public ImageView userface1;
			public ImageView userface2;
			public LinkView username;  
		    public TextView date;  
		    public LinearLayout contentll;
	 }  

	/**
	 * ʵ����Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewMessageDetailAdapter(Context context, List<Comment> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//������ͼ����������������
		this.itemViewResource = resource;
		this.listItems = data;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_dface_loading));
	}
	
	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}
	   
	/**
	 * ListView Item����
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("method", "getView");
		
		//�Զ�����ͼ
		ListItemView  listItemView = null;
		
		if (convertView == null) {
			//��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//��ȡ�ؼ�����
			listItemView.userface1 = (ImageView)convertView.findViewById(R.id.messagedetail_listitem_userface1);
			listItemView.userface2 = (ImageView)convertView.findViewById(R.id.messagedetail_listitem_userface2);
			listItemView.username = (LinkView)convertView.findViewById(R.id.messagedetail_listitem_username);
			listItemView.date = (TextView)convertView.findViewById(R.id.messagedetail_listitem_date);
			listItemView.contentll = (LinearLayout)convertView.findViewById(R.id.messagedetail_listitem_contentll);
			
			//���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		//�������ֺ�ͼƬ
		Comment msg = listItems.get(position);
		listItemView.username.setLinkText("<font color='#0e5986'><b>" + msg.getAuthor() + "</b></font>��" + msg.getContent());
		//listItemView.username.setText(UIHelper.parseMessageSpan(msg.getAuthor(), msg.getContent(), ""));
		//listItemView.username.parseLinkText();
		listItemView.username.setTag(msg);//�������ز���(ʵ����)
		listItemView.date.setText(StringUtils.friendly_time(msg.getPubDate()));
		
		String faceURL = msg.getFace();
		AppContext ac = (AppContext)context.getApplicationContext();
		//������������
		if(msg.getAuthorId() == ac.getLoginUid())
		{
			if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
				listItemView.userface2.setImageResource(R.drawable.widget_dface);
			}else{
				bmpManager.loadBitmap(faceURL, listItemView.userface2);
			}
			listItemView.userface2.setOnClickListener(faceClickListener);
			listItemView.userface2.setTag(msg);
			listItemView.userface2.setVisibility(ImageView.VISIBLE);
			listItemView.userface1.setVisibility(ImageView.GONE);
			listItemView.contentll.setBackgroundResource(R.drawable.review_bg_right);
		}else{
			if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
				listItemView.userface1.setImageResource(R.drawable.widget_dface);
			}else{
				bmpManager.loadBitmap(faceURL, listItemView.userface1);
			}
			listItemView.userface1.setOnClickListener(faceClickListener);
			listItemView.userface1.setTag(msg);
			listItemView.userface1.setVisibility(ImageView.VISIBLE);
			listItemView.userface2.setVisibility(ImageView.GONE);
			listItemView.contentll.setBackgroundResource(R.drawable.review_bg_left);
		}
		
		return convertView;
	}
	
	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Comment msg = (Comment)v.getTag();
			UIHelper.showUserCenter(v.getContext(), msg.getAuthorId(), msg.getAuthor());
		}
	};
    
}