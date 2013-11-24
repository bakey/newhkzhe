package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppException;
import net.oschina.app.bean.Comment.Refer;
import net.oschina.app.bean.Comment.Reply;
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
public class CommentList extends Entity{

	public final static int CATALOG_NEWS = 1;
	public final static int CATALOG_POST = 2;
	public final static int CATALOG_TWEET = 3;
	public final static int CATALOG_ACTIVE = 4;
	public final static int CATALOG_MESSAGE = 4;//��̬�����Զ�������Ϣ����
	//private static final CommentList CommentList() = null;
	
	private int pageSize;
	private int allCount;
	private List<Comment> commentlist = new ArrayList<Comment>();
	
	public int getPageSize() {
		return pageSize;
	}
	public int getAllCount() {
		return allCount;
	}
	public List<Comment> getCommentlist() {
		return commentlist;
	}
	public static CommentList parseJson( InputStream inputStream ) throws AppException, IOException {
		CommentList commentList = new CommentList();
		Comment     comment     = null;
	
		try {	
			
		    String jstr = Utils.readStream( inputStream ); //new String( buffer );
			JSONObject json_obj = new JSONObject( jstr );
			JSONArray alist = json_obj.getJSONArray("CommentList");
			commentList.pageSize = json_obj.getInt("page_size");
			commentList.allCount = json_obj.getInt("all_count");
			Log.d("Main" , "get comment list len = " + alist.length() );
			for( int i = 0 ; i < alist.length() ; i ++ ) {
				JSONObject obj = alist.getJSONObject( i );
				comment = new Comment();
				//comment.setCommentId( obj.getInt("id") );		
				comment.setAuthor( obj.getString("author") );	
				comment.setFace("");
				//comment.setPubDate( obj.getString("pub_time") );
				comment.setCommentId(i);
				comment.setPubDate( "2013-10-28 10:00:00" );
				comment.setContent( obj.getString("content"));
				comment.setAppClient( 1 );
				
				commentList.getCommentlist().add(comment);				    	
				comment = null;
			} 		
		}catch( JSONException e ) {
			e.printStackTrace();
			throw AppException.xml(e);			
		}finally {
			inputStream.close();
		}
		return commentList;
	}

	public static CommentList parse(InputStream inputStream) throws IOException, AppException {
		CommentList commlist = new CommentList();
		Comment comm = null;
		Reply reply = null;
		Refer refer = null;
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
			    		if(tag.equalsIgnoreCase("allCount")) 
			    		{
			    			commlist.allCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			commlist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase("comment")) 
			    		{ 
			    			comm = new Comment();
			    		}
			    		else if(comm != null)
			    		{	
				            if(tag.equalsIgnoreCase("id"))
				            {			      
				            	comm.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase("portrait"))
				            {			            	
				            	comm.setFace(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("author"))
				            {			            	
				            	comm.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("authorid"))
				            {			            	
				            	comm.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase("content"))
				            {			            	
				            	comm.setContent(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("pubDate"))
				            {			            	
				            	comm.setPubDate(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("appclient"))
				            {			            	
				            	comm.setAppClient(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("reply"))
				            {			            	
				            	reply = new Reply();         	
				            }
				            else if(reply!=null && tag.equalsIgnoreCase("rauthor"))
				            {
				            	reply.rauthor = xmlParser.nextText();
				            }
				            else if(reply!=null && tag.equalsIgnoreCase("rpubDate"))
				            {
				            	reply.rpubDate = xmlParser.nextText();
				            }
				            else if(reply!=null && tag.equalsIgnoreCase("rcontent"))
				            {
				            	reply.rcontent = xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase("refer"))
				            {			            	
				            	refer = new Refer();         	
				            }
				            else if(refer!=null && tag.equalsIgnoreCase("refertitle"))
				            {
				            	refer.refertitle = xmlParser.nextText();
				            }
				            else if(refer!=null && tag.equalsIgnoreCase("referbody"))
				            {
				            	refer.referbody = xmlParser.nextText();
				            }
			    		}
			            //֪ͨ��Ϣ
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	commlist.setNotice(new Notice());
			    		}
			            else if(commlist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				commlist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	commlist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	commlist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	commlist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//���������ǩ��������Ѷ�����ӽ�������
				       	if (tag.equalsIgnoreCase("comment") && comm != null) { 
				       		commlist.getCommentlist().add(comm); 
				       		comm = null; 
				       	}
				       	else if (tag.equalsIgnoreCase("reply") && comm!=null && reply!=null) { 
				       		comm.getReplies().add(reply);
				       		reply = null; 
				       	}
				       	else if(tag.equalsIgnoreCase("refer") && comm!=null && refer!=null) {
				       		comm.getRefers().add(refer);
				       		refer = null;
				       	}
				       	break; 
			    }
			    //���xmlû�н������򵼺�����һ���ڵ�
			    evtType=xmlParser.next();
			}		
        } catch (XmlPullParserException e) {
			throw AppException.xml(e);
        } finally {
        	inputStream.close();	
        }      
        return commlist;       
	}
}
