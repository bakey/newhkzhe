package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppException;
import net.oschina.app.common.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.hkzhe.wwtt.common.Utils;

import android.util.Log;
import android.util.Xml;


public class CategoryList extends Entity{
	
	public static final int CATALOG_USER = 1;
	public static final int CATALOG_LATEST = 2;
	public static final int CATALOG_RECOMMEND = 3;
	
	public static final String TYPE_LATEST = "latest";
	public static final String TYPE_RECOMMEND = "recommend";

	final static String TAG = "CategoryList";
	private int CategoryCount;
	private int pageSize;
	private List<Category> categorylist = new ArrayList<Category>();
	
	public int getCategoryCount() {
		return CategoryCount;
	}
	public int getPageSize() {
		return pageSize;
	}
	public List<Category> getCatelist() {
		return categorylist;
	}	
	public static CategoryList parse(InputStream inputStream) throws IOException, AppException {
		CategoryList clist = new CategoryList();
		Category cate = null;
		clist.pageSize = 1;
		try{			
			 String jstr = Utils.readStream( inputStream ); //new String( buffer );
			 JSONObject json_obj = new JSONObject( jstr );
			 JSONArray alist = json_obj.getJSONArray("Category");
			 for( int i = 0 ; i < alist.length() ; i ++ ) {
					JSONObject obj = alist.getJSONObject( i );
					cate = new Category();
					cate.id = obj.getInt("id");
					cate.setTitle( obj.getString("title") );
					cate.setPubDate( obj.getString("pubDate") );	
					cate.setAuthor("admin");
					cate.setCommentCount(0);
					clist.getCatelist().add(cate);				    	
					cate = null;
				}
        } catch( JSONException e ) {
			e.printStackTrace();
			throw AppException.xml(e);			
		}
		finally {
        	inputStream.close();	
        }      
        return clist;       
	}
}
