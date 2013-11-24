package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.app.R;

import net.oschina.app.adapter.ListViewBlogAdapter.ListItemView;
import net.oschina.app.bean.Category;
import net.oschina.app.bean.CategoryList;
import net.oschina.app.common.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ListViewCategoryAdapter extends BaseAdapter {
	private Context 					context;//����������
	private List<Category> 					listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ 
	private int							cateType;
	static class ListItemView{				//�Զ���ؼ�����  
	        public TextView title;
	        public TextView author;
		    public TextView date;  
		    public TextView count;
		    public ImageView type;
	 }  

	/**
	 * ʵ����Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewCategoryAdapter(Context context, int catetype, List<Category> data, int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//������ͼ����������������
		this.itemViewResource = resource;
		this.listItems = data;
		this.cateType = catetype;
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
		
		// �Զ�����ͼ
		ListItemView listItemView = null;

		if (convertView == null) {
			// ��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// ��ȡ�ؼ�����
			listItemView.title = (TextView) convertView
					.findViewById(R.id.cate_listitem_title);
			listItemView.author = (TextView) convertView
					.findViewById(R.id.cate_listitem_author);
			listItemView.count = (TextView) convertView
					.findViewById(R.id.cate_listitem_commentCount);
			listItemView.date = (TextView) convertView
					.findViewById(R.id.cate_listitem_date);
			listItemView.type = (ImageView) convertView
					.findViewById(R.id.cate_listitem_documentType);

			// ���ÿؼ�����convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		// �������ֺ�ͼƬ
		Category cate = listItems.get(position);
		
		listItemView.title.setText(cate.getTitle());
		listItemView.title.setTag(cate);//�������ز���(ʵ����)
		listItemView.date.setText(StringUtils.friendly_time(cate.getPubDate()));
		listItemView.count.setText(cate.getCommentCount()+"");
		if(cate.getDocumentType() == cate.DOC_TYPE_ORIGINAL)
			listItemView.type.setImageResource(R.drawable.widget_original_icon);
		else
			listItemView.type.setImageResource(R.drawable.widget_repaste_icon);
		
		if(cateType == CategoryList.CATALOG_USER){
			listItemView.author.setVisibility(View.GONE);
		}else{
			listItemView.author.setText(cate.getAuthor()+"   ������");
		}
		
		return convertView;
	}
}