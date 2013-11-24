package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.app.R;
import net.oschina.app.bean.SoftwareCatalogList.SoftwareType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * �������Adapter��
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewSoftwareCatalogAdapter extends BaseAdapter {
	private Context 					context;//����������
	private List<SoftwareType> 			listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ 
	static class ListItemView{				//�Զ���ؼ�����  
	        public TextView name;  
	 }  

	/**
	 * ʵ����Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewSoftwareCatalogAdapter(Context context, List<SoftwareType> data,int resource) {
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
			listItemView.name = (TextView)convertView.findViewById(R.id.softwarecatalog_listitem_name);
			
			//���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//�������ֺ�ͼƬ
		SoftwareType softwareType = listItems.get(position);
		
		listItemView.name.setText(softwareType.name);
		listItemView.name.setTag(softwareType);//�������ز���(ʵ����)
		
		return convertView;
	}
}