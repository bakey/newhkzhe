package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.app.R;
import net.oschina.app.bean.Post;
import net.oschina.app.common.BitmapManager;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * �ʴ�Adapter��
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewQuestionAdapter extends BaseAdapter {
	private Context 					context;//����������
	private List<Post> 					listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ 
	private BitmapManager 				bmpManager;
	static class ListItemView{				//�Զ���ؼ�����  
			public ImageView face;
	        public TextView title;  
		    public TextView author;
		    public TextView date;  
		    public TextView count;
	 }  

	/**
	 * ʵ����Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewQuestionAdapter(Context context, List<Post> data,int resource) {
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
			listItemView.face = (ImageView)convertView.findViewById(R.id.question_listitem_userface);
			listItemView.title = (TextView)convertView.findViewById(R.id.question_listitem_title);
			listItemView.author = (TextView)convertView.findViewById(R.id.question_listitem_author);
			listItemView.count= (TextView)convertView.findViewById(R.id.question_listitem_count);
			listItemView.date= (TextView)convertView.findViewById(R.id.question_listitem_date);
			
			//���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
      
		//�������ֺ�ͼƬ
		Post post = listItems.get(position);
		String faceURL = post.getFace();
		if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
			listItemView.face.setImageResource(R.drawable.widget_dface);
		}else{
			bmpManager.loadBitmap(faceURL, listItemView.face);
		}
		listItemView.face.setOnClickListener(faceClickListener);
		listItemView.face.setTag(post);
		
		listItemView.title.setText(post.getTitle());
		listItemView.title.setTag(post);//�������ز���(ʵ����)
		listItemView.author.setText(post.getAuthor());
		listItemView.date.setText(StringUtils.friendly_time(post.getPubDate()));
		listItemView.count.setText(post.getAnswerCount()+"��|"+post.getViewCount()+"��");
		
		return convertView;
	}
	
	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Post post = (Post)v.getTag();
			UIHelper.showUserCenter(v.getContext(), post.getAuthorId(), post.getAuthor());
		}
	};
}