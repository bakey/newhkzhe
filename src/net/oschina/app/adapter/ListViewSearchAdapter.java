package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.app.R;
import net.oschina.app.bean.SearchList.Result;
import net.oschina.app.common.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ����Adapter��
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewSearchAdapter extends BaseAdapter {
	private Context 					context;//����������
	private List<Result> 				listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ 
	static class ListItemView{				//�Զ���ؼ�����  
        public TextView title;  
	    public TextView author;
	    public TextView date;  
	    public LinearLayout layout;
	}  

	/**
	 * ʵ����Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewSearchAdapter(Context context, List<Result> data,int resource) {
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
			listItemView.title = (TextView)convertView.findViewById(R.id.search_listitem_title);
			listItemView.author = (TextView)convertView.findViewById(R.id.search_listitem_author);
			listItemView.date = (TextView)convertView.findViewById(R.id.search_listitem_date);
			listItemView.layout = (LinearLayout)convertView.findViewById(R.id.search_listitem_ll);
			
			//���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//�������ֺ�ͼƬ
		Result res = listItems.get(position);
		
		listItemView.title.setText(res.getTitle());
		listItemView.title.setTag(res);//�������ز���(ʵ����)
		if(StringUtils.isEmpty(res.getAuthor())) {
			listItemView.layout.setVisibility(LinearLayout.GONE);
		}else{
			listItemView.layout.setVisibility(LinearLayout.VERTICAL);
			listItemView.author.setText(res.getAuthor());
			listItemView.date.setText(StringUtils.friendly_time(res.getPubDate()));
		}
		
		return convertView;
	}
}