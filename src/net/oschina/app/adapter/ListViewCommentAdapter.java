package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.app.R;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Comment.Refer;
import net.oschina.app.bean.Comment.Reply;
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
 * �û�����Adapter��
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewCommentAdapter extends BaseAdapter {
	private Context 					context;//����������
	private List<Comment> 				listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ 
	private BitmapManager 				bmpManager;
	static class ListItemView{				//�Զ���ؼ�����
			public ImageView face;
	        public TextView name;  
		    public TextView date;  
		    public LinkView content;
		    public TextView client;
		    public LinearLayout relies;
		    public LinearLayout refers;
	 }  

	/**
	 * ʵ����Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewCommentAdapter(Context context, List<Comment> data,int resource) {
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
			listItemView.face = (ImageView)convertView.findViewById(R.id.comment_listitem_userface);
			listItemView.name = (TextView)convertView.findViewById(R.id.comment_listitem_username);
			listItemView.date = (TextView)convertView.findViewById(R.id.comment_listitem_date);
			listItemView.content = (LinkView)convertView.findViewById(R.id.comment_listitem_content);
			listItemView.client= (TextView)convertView.findViewById(R.id.comment_listitem_client);
			listItemView.relies = (LinearLayout)convertView.findViewById(R.id.comment_listitem_relies);
			listItemView.refers = (LinearLayout)convertView.findViewById(R.id.comment_listitem_refers);
			
			//���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//�������ֺ�ͼƬ
		Comment comment = listItems.get(position);
		String faceURL = comment.getFace();
		if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
			listItemView.face.setImageResource(R.drawable.widget_dface);
		}else{
			bmpManager.loadBitmap(faceURL, listItemView.face);
		}
		listItemView.face.setTag(comment);//�������ز���(ʵ����)
		listItemView.face.setOnClickListener(faceClickListener);
		listItemView.name.setText(comment.getAuthor());
		listItemView.date.setText(StringUtils.friendly_time(comment.getPubDate()));
		listItemView.content.setText(comment.getContent());
		listItemView.content.parseLinkText();
		listItemView.content.setTag(comment);//�������ز���(ʵ����)
		
		switch(comment.getAppClient())
		{	
			case 0:
			case 1:
				listItemView.client.setText("");
				break;
			case 2:
				listItemView.client.setText("����:�ֻ�");
				break;
			case 3:
				listItemView.client.setText("����:Android");
				break;
			case 4:
				listItemView.client.setText("����:iPhone");
				break;
			case 5:
				listItemView.client.setText("����:Windows Phone");
				break;
		}
		if(StringUtils.isEmpty(listItemView.client.getText().toString()))
			listItemView.client.setVisibility(View.GONE);
		else
			listItemView.client.setVisibility(View.VISIBLE);
		
		listItemView.relies.setVisibility(View.GONE);
		listItemView.relies.removeAllViews();//�����
		if(comment.getReplies().size() > 0){
			//������Ŀ
			View view = listContainer.inflate(R.layout.comment_reply, null);
			TextView tv = (TextView)view.findViewById(R.id.comment_reply_content);
			tv.setText(context.getString(R.string.comment_reply_title, comment.getReplies().size()));
			listItemView.relies.addView(view);
			//��������
			for(Reply reply : comment.getReplies()){
				View view2 = listContainer.inflate(R.layout.comment_reply, null);
				TextView tv2 = (TextView)view2.findViewById(R.id.comment_reply_content);
				tv2.setText(reply.rauthor+"("+StringUtils.friendly_time(reply.rpubDate)+")��"+reply.rcontent);
				listItemView.relies.addView(view2);
			}
			listItemView.relies.setVisibility(View.VISIBLE);
		}
		
		listItemView.refers.setVisibility(View.GONE);
		listItemView.refers.removeAllViews();//�����
		if(comment.getRefers().size() > 0){
			//��������
			for(Refer refer : comment.getRefers()){
				View view = listContainer.inflate(R.layout.comment_refer, null);
				TextView title = (TextView)view.findViewById(R.id.comment_refer_title);
				TextView body = (TextView)view.findViewById(R.id.comment_refer_body);
				title.setText(refer.refertitle);
				body.setText(refer.referbody);
				listItemView.refers.addView(view);
			}
			listItemView.refers.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}
	
	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Comment comment = (Comment)v.getTag();
			UIHelper.showUserCenter(v.getContext(), comment.getAuthorId(), comment.getAuthor());
		}
	};
}