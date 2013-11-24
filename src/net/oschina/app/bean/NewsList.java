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

/**
 * �����б�ʵ����
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class NewsList extends Entity{

	public final static int CATALOG_ALL = 1;
	public final static int CATALOG_INTEGRATION = 2;
	public final static int CATALOG_SOFTWARE = 3;
	
	private int catalog;
	private int pageSize;
	private int newsCount;
	private List<News> newslist = new ArrayList<News>();
	
	public int getCatalog() {
		return catalog;
	}
	public int getPageSize() {
		return pageSize;
	}
	public int getNewsCount() {
		return newsCount;
	}
	public List<News> getNewslist() {
		return newslist;
	}
	public static NewsList parseJSON( InputStream inputStream ) throws IOException,AppException {
		NewsList newslist = new NewsList();
		News news = null;
		try {
			newslist.catalog = 1;
			newslist.newsCount = 0;
			newslist.pageSize = 1;
			//byte[] buffer = new byte[ inputStream.available() ];
			//inputStream.read( buffer , 0 , inputStream.available() );
		    String jstr = Utils.readStream( inputStream ); //new String( buffer );
			JSONObject json_obj = new JSONObject( jstr );
			JSONArray alist = json_obj.getJSONArray("ThreadList");
			newslist.pageSize = json_obj.getInt("page_size");
			for( int i = 0 ; i < alist.length() ; i ++ ) {
				JSONObject obj = alist.getJSONObject( i );
				news = new News();
				news.id = obj.getInt("id");
				String title = obj.getString("title");
				news.setTitle( title );				
				news.setAuthor( obj.getString("author") );
				news.setCommentCount( obj.getInt("reply_count") );
				news.setHitCount( obj.getInt("hit_count"));
				news.setPubDate( obj.getString("pub_time") );
				int favorite = 0;
				if ( obj.has("is_favorite") ) {
					favorite = obj.getInt("is_favorite");					
				}
				news.setFavorite( favorite );
				news.getNewType().type = 0 ;
				newslist.getNewslist().add(news);				    	
				news = null;
			} 
		}catch( JSONException e ) {
			e.printStackTrace();
			throw AppException.xml(e);			
		}finally {
			inputStream.close();
		}
		return newslist ;		
	}
	
	public static NewsList parse(InputStream inputStream) throws IOException, AppException {
		NewsList newslist = new NewsList();
		News news = null;
        //���XmlPullParser������
        XmlPullParser xmlParser = Xml.newPullParser();
        try {        	
            xmlParser.setInput(inputStream, UTF8);
            //��ý��������¼���������п�ʼ�ĵ��������ĵ�����ʼ��ǩ��������ǩ���ı��ȵ��¼���
            int evtType=xmlParser.getEventType();
			//һֱѭ����ֱ���ĵ�����    
			while(evtType!=XmlPullParser.END_DOCUMENT){ 
	    		String tag = xmlParser.getName(); 
			    switch(evtType){ 
			    	case XmlPullParser.START_TAG:
			    		if(tag.equalsIgnoreCase("catalog")) 
			    		{
			    			newslist.catalog = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			newslist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("newsCount")) 
			    		{
			    			newslist.newsCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase(News.NODE_START)) 
			    		{ 
			    			news = new News();
			    		}
			    		else if(news != null)
			    		{	
				            if(tag.equalsIgnoreCase(News.NODE_ID))
				            {			      
				            	news.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_TITLE))
				            {			            	
				            	news.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_URL))
				            {			            	
				            	news.setUrl(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_AUTHOR))
				            {			            	
				            	news.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_AUTHORID))
				            {			            	
				            	news.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_COMMENTCOUNT))
				            {			            	
				            	news.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_PUBDATE))
				            {			            	
				            	news.setPubDate(xmlParser.nextText());	
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_TYPE))
				            {	
				            	news.getNewType().type = StringUtils.toInt(xmlParser.nextText(),0); 
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_ATTACHMENT))
				            {			            	
				            	news.getNewType().attachment = xmlParser.nextText(); 	
				            }
				            else if(tag.equalsIgnoreCase(News.NODE_AUTHORUID2))
				            {			            	
				            	news.getNewType().authoruid2 = StringUtils.toInt(xmlParser.nextText(),0); 
				            }
			    		}
			            //֪ͨ��Ϣ
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	newslist.setNotice(new Notice());
			    		}
			            else if(newslist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				newslist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	newslist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	newslist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	newslist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//���������ǩ��������Ѷ�����ӽ�������
				       	if (tag.equalsIgnoreCase("news") && news != null) { 
				    	   newslist.getNewslist().add(news); 
				           news = null; 
				       	}
				       	break; 
			    }
			    //���xmlû�н������򵼺�����һ���ڵ�
			    int a =xmlParser.next();
			    evtType=a;
			}		
        } catch (XmlPullParserException e) {
        	e.printStackTrace();
			throw AppException.xml(e);
        } finally {
        	inputStream.close();	
        }      
        return newslist;       
	}
}
