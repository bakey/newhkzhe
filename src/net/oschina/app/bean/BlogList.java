package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppException;
import net.oschina.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * �����б�ʵ����
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class BlogList extends Entity{
	
	public static final int CATALOG_USER = 1;//�û�����
	public static final int CATALOG_LATEST = 2;//���²���
	public static final int CATALOG_RECOMMEND = 3;//�Ƽ�����
	
	public static final String TYPE_LATEST = "latest";
	public static final String TYPE_RECOMMEND = "recommend";
	
	private int blogsCount;
	private int pageSize;
	private List<Blog> bloglist = new ArrayList<Blog>();
	
	public int getBlogsCount() {
		return blogsCount;
	}
	public int getPageSize() {
		return pageSize;
	}
	public List<Blog> getBloglist() {
		return bloglist;
	}
	
	public static BlogList parse(InputStream inputStream) throws IOException, AppException {
		BlogList bloglist = new BlogList();
		Blog blog = null;
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
			    		if(tag.equalsIgnoreCase("blogsCount")) 
			    		{
			    			bloglist.blogsCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			bloglist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase("blog")) 
			    		{ 
			    			blog = new Blog();
			    		}
			    		else if(blog != null)
			    		{	
				            if(tag.equalsIgnoreCase("id"))
				            {			      
				            	blog.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase("title"))
				            {			            	
				            	blog.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("url"))
				            {			            	
				            	blog.setUrl(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("pubDate"))
				            {			            	
				            	blog.setPubDate(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("authoruid"))
				            {			            	
				            	blog.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("authorname"))
				            {			            	
				            	blog.setAuthor(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("documentType"))
				            {			            	
				            	blog.setDocumentType(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("commentCount"))
				            {			            	
				            	blog.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			            //֪ͨ��Ϣ
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	bloglist.setNotice(new Notice());
			    		}
			            else if(bloglist.getNotice() != null)
			    		{
			    			if(tag.equalsIgnoreCase("atmeCount"))
				            {			      
			    				bloglist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("msgCount"))
				            {			            	
				            	bloglist.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("reviewCount"))
				            {			            	
				            	bloglist.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("newFansCount"))
				            {			            	
				            	bloglist.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//���������ǩ��������Ѷ�����ӽ�������
				       	if (tag.equalsIgnoreCase("blog") && blog != null) { 
				       		bloglist.getBloglist().add(blog); 
				       		blog = null; 
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
        return bloglist;       
	}
}
