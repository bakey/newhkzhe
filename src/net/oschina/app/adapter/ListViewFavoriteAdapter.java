package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.app.R;
import net.oschina.app.bean.FavoriteList.Favorite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * �û��ղ�Adapter��
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-5-24
 */
public class ListViewFavoriteAdapter extends BaseAdapter {
	private Context 					context;//����������
	private List<Favorite> 				listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ 
	static class ListItemView{				//�Զ���ؼ�����  
        public TextView title;  
	}  

	/**
	 * ʵ����Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewFavoriteAdapter(Context context, List<Favorite> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//������ͼ����������������
		this.itemViewResource = resource;
		this.listItems = data;
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
			listItemView.title = (TextView)convertView.findViewById(R.id.favorite_listitem_title);
			
			//���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//�������ֺ�ͼƬ
		Favorite favorite = listItems.get(position);
		
		listItemView.title.setText(favorite.title);
		listItemView.title.setTag(favorite);//�������ز���(ʵ����)
		
		return convertView;
	}
}