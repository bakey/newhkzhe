package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.app.R;
import net.oschina.app.bean.FriendList.Friend;
import net.oschina.app.common.BitmapManager;
import net.oschina.app.common.StringUtils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * �û���˿����עAdapter��
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-5-24
 */
public class ListViewFriendAdapter extends BaseAdapter {
	private Context 					context;//����������
	private List<Friend> 				listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ 
	private BitmapManager 				bmpManager;
	static class ListItemView{				//�Զ���ؼ�����  
        public ImageView face;  
        public ImageView gender;
        public TextView name;  
        public TextView expertise;
	}  

	/**
	 * ʵ����Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewFriendAdapter(Context context, List<Friend> data,int resource) {
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
			listItemView.name = (TextView)convertView.findViewById(R.id.friend_listitem_name);
			listItemView.expertise = (TextView)convertView.findViewById(R.id.friend_listitem_expertise);
			listItemView.face = (ImageView)convertView.findViewById(R.id.friend_listitem_userface);
			listItemView.gender = (ImageView)convertView.findViewById(R.id.friend_listitem_gender);
			
			//���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//�������ֺ�ͼƬ
		Friend friend = listItems.get(position);
		
		listItemView.name.setText(friend.getName());
		listItemView.name.setTag(friend);//�������ز���(ʵ����)
		listItemView.expertise.setText(friend.getExpertise());
		
		if(friend.getGender() == 1)
			listItemView.gender.setImageResource(R.drawable.widget_gender_man);
		else
			listItemView.gender.setImageResource(R.drawable.widget_gender_woman);
		
		String faceURL = friend.getFace();
		if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
			listItemView.face.setImageResource(R.drawable.widget_dface);
		}else{
			bmpManager.loadBitmap(faceURL, listItemView.face);
		}
		listItemView.face.setTag(friend);
		
		return convertView;
	}
}