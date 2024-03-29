package net.oschina.app.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.hkzhe.app.R;

import net.oschina.app.AppContext;
import net.oschina.app.bean.News;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.ui.LoginDialog;
import net.oschina.app.ui.Main;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 新闻资讯Adapter类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewNewsAdapter extends BaseAdapter {
	private Context 					context;//运行上下文
	private List<News> 					listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 
	static class ListItemView{				//自定义控件集合  
	        public TextView title;  
		    public TextView author;
		    public TextView date;  
		    public TextView count;
		    public ImageView flag;
		    public ImageView favorite_icon;
	 }  

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewNewsAdapter(Context context, List<News> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
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
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
				
		//自定义视图
		ListItemView  listItemView = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取控件对象
			listItemView.title = (TextView)convertView.findViewById(R.id.news_listitem_title);
			listItemView.author = (TextView)convertView.findViewById(R.id.news_listitem_author);
			listItemView.count= (TextView)convertView.findViewById(R.id.news_listitem_commentCount);
			listItemView.date= (TextView)convertView.findViewById(R.id.news_listitem_date);
			listItemView.flag= (ImageView)convertView.findViewById(R.id.news_listitem_flag);
			listItemView.favorite_icon = ( ImageView )convertView.findViewById( R.id.thread_favorite_iv );
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//设置文字和图片
		final News news = listItems.get(position);
		
		listItemView.title.setText(news.getTitle());
		listItemView.title.setTag(news);//设置隐藏参数(实体类)
		listItemView.author.setText(news.getAuthor());
		listItemView.date.setText(StringUtils.friendly_time(news.getPubDate()));
		listItemView.count.setText(news.getCommentCount()+"");
		if(StringUtils.isToday(news.getPubDate())) {
			listItemView.flag.setVisibility(View.VISIBLE);
		}
		else {
			listItemView.flag.setVisibility(View.GONE);
		}
		final AppContext appCxt = (AppContext)((Activity)(this.context)).getApplication();
		if ( !appCxt.isLogin() ) {
			listItemView.favorite_icon.setVisibility( View.GONE );
		}
		final ImageView favorite = listItemView.favorite_icon;
		final Handler handler = new Handler() {
			public void handleMessage( Message msg ) {
				if ( msg.what == 1 ) {
					if ( msg.arg1 == News.NEWS_OP_TYPE_ADDFAVORITE ) {
						UIHelper.ToastMessage( appCxt , R.string.add_favorite_success );	
						favorite.setImageResource( R.drawable.xin_01 );
					}else {
						UIHelper.ToastMessage( appCxt , R.string.eliminate_favorite_success );
						favorite.setImageResource( R.drawable.xin_02 );
					}
				}else {
					UIHelper.ToastMessage( appCxt , R.string.favorite_op_failed );
				}
			}
		};
		if ( news.getFavorite() != 0 ) {
			favorite.setImageResource( R.drawable.xin_01 );			
		}else {
			favorite.setImageResource( R.drawable.xin_02 );
		}
		listItemView.favorite_icon.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				new Thread() {
        			public void run() {
        				Message msg =new Message();
        				if ( news.getFavorite() == 0 ) {
        					msg.what =  appCxt.saveMyFavorite( news.getId() ) ? 1 : 0;
        					msg.arg1 = News.NEWS_OP_TYPE_ADDFAVORITE;
        					if ( msg.what > 0 ) {
        						news.setFavorite( 1 );
        					}
        				}else {
        					msg.what = appCxt.eliminateFavorite( news.getId() ) ? 1 : 0 ;
        					msg.arg1 = News.NEWS_OP_TYPE_ELIMINATEFAVORITE;
        					if ( msg.what > 0 ) {
        						news.setFavorite( 0 );
        					}
        				}
        				handler.sendMessage(msg);
        			}        			
        		}.start();					
			}
		});
		
		return convertView;
	}
}