package net.oschina.app.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.bean.ActiveList;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.BlogCommentList;
import net.oschina.app.bean.BlogList;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.FriendList;
import net.oschina.app.bean.MessageList;
import net.oschina.app.bean.MyInformation;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.PostList;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.SearchList;
import net.oschina.app.bean.Software;
import net.oschina.app.bean.SoftwareCatalogList;
import net.oschina.app.bean.SoftwareList;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetList;
import net.oschina.app.bean.URLs;
import net.oschina.app.bean.Update;
import net.oschina.app.bean.User;
import net.oschina.app.bean.UserInformation;
import net.oschina.app.common.StringUtils;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.hkzhe.wwtt.common.Utils;
import com.weibo.net.Utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * API�ͻ��˽ӿڣ����ڷ�����������
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ApiClient {

	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";
	
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 3;

	private static String appCookie;
	private static String appUserAgent;
	
	public static String s_proxy_host  = "" ;
	public static int    s_proxy_port  = 0 ;

	public static void cleanCookie() {
		appCookie = "";
	}
	
	private static String getCookie(AppContext appContext) {
		if(appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		return appCookie;
	}
	
	private static String getUserAgent(AppContext appContext) {
		if(appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("OSChina.NET");
			ua.append('/'+appContext.getPackageInfo().versionName+'_'+appContext.getPackageInfo().versionCode);//App�汾
			ua.append("/Android");//�ֻ�ϵͳƽ̨
			ua.append("/"+android.os.Build.VERSION.RELEASE);//�ֻ�ϵͳ�汾
			ua.append("/"+android.os.Build.MODEL); //�ֻ��ͺ�
			ua.append("/"+appContext.getAppId());//�ͻ���Ψһ��ʶ
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}
	

	private static HttpClient getHttpClient( AppContext ac ) {        
        HttpClient httpClient = new HttpClient();
		// ���� HttpClient ���� Cookie,���������һ���Ĳ���
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // ���� Ĭ�ϵĳ�ʱ���Դ������
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// ���� ���ӳ�ʱʱ��
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// ���� �����ݳ�ʱʱ�� 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// ���� �ַ���
		httpClient.getParams().setContentCharset(UTF_8);
		String proxy = ac.getProxyHostString();
		if ( proxy != null && !proxy.isEmpty() ) {
			int port = 0;
			String host ;
			String arr[] = proxy.split(":");
			if ( arr.length < 2 ) {
				host = proxy;
				port = 8080;
			}else {
				host = arr[0];
				port = Integer.parseInt( arr[1] );
			}
			httpClient.getHostConfiguration().setProxy( host , port );
			Log.d("Main" , "set proxy = " + host + ":" + port );
		}		
		return httpClient;
	}	
	private static HttpClient getHttpClient() {        
        HttpClient httpClient = new HttpClient();
		// ���� HttpClient ���� Cookie,���������һ���Ĳ���
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // ���� Ĭ�ϵĳ�ʱ���Դ������
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// ���� ���ӳ�ʱʱ��
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// ���� �����ݳ�ʱʱ�� 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// ���� �ַ���
		httpClient.getParams().setContentCharset(UTF_8);		
		return httpClient;
	}
	
	
	private static GetMethod getHttpGet(String url, String cookie, String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// ���� ����ʱʱ��
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection","Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}
	
	private static PostMethod getHttpPost(String url, String cookie, String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// ���� ����ʱʱ��
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", URLs.HOST);
		httpPost.setRequestHeader("Connection","Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		return httpPost;
	}
	
	private static String _MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);

		for(String name : params.keySet()){
			url.append('/');
			url.append(name);
			url.append('/');
			url.append(String.valueOf(params.get(name)));
			//����URLEncoder����
			//url.append(URLEncoder.encode(String.valueOf(params.get(name)), UTF_8));
		}

		return url.toString().replace("?&", "?");
	}
	
	/**
	 * get����URL
	 * @param url
	 * @throws AppException 
	 */
	private static InputStream http_get(AppContext appContext, String url) throws AppException {	
		
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		
		HttpClient httpClient = null;
		GetMethod httpGet = null;

		String responseBody = "";
		
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient( appContext );
				httpGet = getHttpGet(url, cookie, userAgent);			
				int statusCode = httpClient.executeMethod(httpGet);		
				if (statusCode != HttpStatus.SC_OK) {
					String body = httpGet.getResponseBodyAsString();
					Log.d( "Main" , body );
					
					throw AppException.http(statusCode);
				}
				responseBody = httpGet.getResponseBodyAsString();				
				//responseBodyStream = httpGet.getResponseBodyAsStream();
				//System.out.println("XMLDATA=====>"+responseBody);
				break;				
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// �����������쳣��������Э�鲻�Ի��߷��ص�����������
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// ���������쳣
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// �ͷ�����
				httpGet.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		
		//responseBody = Utils.readStream( responseBodyStream );
		responseBody = responseBody.replace('', '?');
		if(responseBody.contains("result") && responseBody.contains("errorCode")){
			try {
				Result res = Result.parse(new ByteArrayInputStream(responseBody.getBytes()));	
				if(res.getErrorCode() == 0){
					appContext.Logout();
					appContext.getUnLoginHandler().sendEmptyMessage(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		return new ByteArrayInputStream(responseBody.getBytes());
	}
	
	/**
	 * ����post����
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	private static InputStream _post(AppContext appContext, String url, Map<String, Object> params, Map<String,File> files) throws AppException {
		//System.out.println("post_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		
		HttpClient httpClient = null;
		PostMethod httpPost = null;
		
		//post����������
		int length = (params == null ? 0 : params.size()) + (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
        if(params != null) {
        	for(String name : params.keySet()){
        		parts[i++] = new StringPart(name, String.valueOf(params.get(name)), UTF_8);        		
        	}
        }
        if(files != null) {
        	for(String file : files.keySet()){
        		try {
        			parts[i++] = new FilePart(file, files.get(file));
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		}
           	}
        }
		
		String responseBody = "";
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient( appContext );
				httpPost = getHttpPost(url, cookie, userAgent);	  
				if ( params != null ) {
					for (String name : params.keySet() ) {
						httpPost.addParameter( new NameValuePair(name , String.valueOf(params.get(name))) );
					}
				}
		        //httpPost.setRequestEntity(new MultipartRequestEntity(parts,httpPost.getParams()));		        
		        int statusCode = httpClient.executeMethod(httpPost);
		        if(statusCode != HttpStatus.SC_OK) 
		        {
		        	Log.d( "Main" , "url = " + url + " , status = " + statusCode );
		        	responseBody = httpPost.getResponseBodyAsString();		        	
		        	throw AppException.http(statusCode);
		        }
		        else 
		        {
		            Cookie[] cookies = httpClient.getState().getCookies();
		            String tmpcookies = "";
		            for (Cookie ck : cookies) {
		                tmpcookies += ck.toString()+";";
		            }
		            //����cookie   
	        		if(appContext != null && tmpcookies != ""){
	        			appContext.setProperty("cookie", tmpcookies);
	        			appCookie = tmpcookies;
	        		}
		        }
		     	responseBody = httpPost.getResponseBodyAsString();
		        //System.out.println("XMLDATA=====>"+responseBody);
		     	break;	     	
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// �����������쳣��������Э�鲻�Ի��߷��ص�����������
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// ���������쳣
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// �ͷ�����
				httpPost.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
        
        responseBody = responseBody.replace('', '?');
		if(responseBody.contains("result") && responseBody.contains("errorCode")){
			try {
				Result res = Result.parse(new ByteArrayInputStream(responseBody.getBytes()));	
				if(res.getErrorCode() == 0){
					appContext.Logout();
					appContext.getUnLoginHandler().sendEmptyMessage(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
        return new ByteArrayInputStream(responseBody.getBytes());
	}
	public static String addMyFavorite( AppContext appCxt , int uid , int tid ) {
		String url = URLs.SAVE_FAVORITE_RSC + "/uid/" + uid + "/tid/" + tid ;
		try {
			InputStream is = _post( appCxt , url , new HashMap<String, Object>() , null );
			return  Utils.readStream( is );		
		}catch( AppException e) {
			e.printStackTrace();
			return "";
		}	
	}
	public static String eliminateFavorite( AppContext appCxt , final int uid , final int tid ) {
		String url = _MakeURL(URLs.ELIMINATE_FAVORITE_RSC, new HashMap<String, Object>(){{
			put("uid", uid);
			put("tid" , tid);
		}});
		Log.d( "Main" , "eliminate url = " + url );
		try {
			InputStream is = _post( appCxt , url , null , null );
			return Utils.readStream( is );
		}catch( AppException e ) {
			e.printStackTrace();
			return "";
		}		
	}
	public static NewsList getMyFavorites( AppContext appCxt , int uid ) {
		String newUrl = URLs.GET_FAVORITE_RSC + "/uid/" + uid ;
		try {
			Log.d( "Main" , "get favorite url = " + newUrl );
			return NewsList.parseJSON( http_get( appCxt , newUrl ) );
		}catch( AppException e ) {
			e.printStackTrace();
			Log.d( "Main" , "got app exceptin when get my favorite ");
			return null;
		}catch ( IOException e ) {
			e.printStackTrace();
			Log.d( "Main" , "got io exceptin when get my favorite ");
			return null;
		}
	}
	public static boolean notifyServerLogin( AppContext appCxt ,  HashMap<String, Object> params ) {
		String uid = String.valueOf( params.get("uid") ) ;
		String access_token = String.valueOf( params.get("access_token") ); 
		String url = URLs.LOGIN_NOTIFY_URL + "/uid/" + uid + "/access_token/" + access_token ;
		try {
			InputStream is = _post( appCxt , url , params , null );
			String resp = Utils.readStream( is );
			JSONObject obj = new JSONObject( resp );
			if ( obj.getString("status").equalsIgnoreCase("ok") ) {
				Log.d( "Main" , "notify server login respon ok ");
				return true;
			}else {
				return false;
			}
		}catch( AppException e ) {
			e.printStackTrace();
			Log.d( "Main" , "got app exception : " + e );
			return false;
		}catch( JSONException e ) {
			e.printStackTrace();
			Log.d( "Main" , "got json exception : " + e );
			return false;
		}
	}
	public static String getWeiboAuthorizeMsg( AppContext appCxt  , HashMap<String, Object> params) {
		String url = URLs.WEIBO_ACCESS_TOKEN_URL ;
		try {
			InputStream is = _post( appCxt , url , params , null );
			String jstr = Utils.readStream( is );
			return jstr;			
		}catch(AppException e) {
			Log.d( "Main" , "appException : " + e.getCode() );
			e.printStackTrace();
			return "";
		}		
	}
	
	public static String getWeiboUserInfo( AppContext appCxt , HashMap<String,Object> params ) {
		final String access_token = params.get("access_token").toString();
		final String uid = params.get("uid").toString();
		String newUrl = URLs.WEIBO_GET_USER_URL + "?access_token=" + access_token + "&uid=" + uid;
		try {
			InputStream is = http_get( appCxt , newUrl );
			String resStr = Utils.readStream( is );
			return resStr;
		}catch( AppException e ) {
			Log.d( "Main" , "appException : " + e.getCode() );
			e.printStackTrace();
			return "";
		}
		
	}
	
	/**
	 * post����URL
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException 
	 * @throws IOException 
	 * @throws  
	 */
	private static Result http_post(AppContext appContext, String url, Map<String, Object> params, Map<String,File> files) throws AppException, IOException {
        return Result.parse(_post(appContext, url, params, files));  
	}	
	
	/**
	 * ��ȡ����ͼƬ
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		//System.out.println("image_url==> "+url);
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
		        InputStream inStream = httpGet.getResponseBodyAsStream();
		        bitmap = BitmapFactory.decodeStream(inStream);
		        inStream.close();
		        break;
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// �����������쳣��������Э�鲻�Ի��߷��ص�����������
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// ���������쳣
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// �ͷ�����
				httpGet.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		return bitmap;
	}
	
	/**
	 * ���汾����
	 * @param url
	 * @return
	 */
	public static Update checkVersion(AppContext appContext) throws AppException {
		try{
			return Update.parse(http_get(appContext, URLs.UPDATE_VERSION));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��¼�� �Զ�����cookie
	 * @param url
	 * @param username
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public static User login(AppContext appContext, String username, String pwd) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("username", username);
		params.put("pwd", pwd);
		params.put("keep_login", 1);
				
		String loginurl = URLs.LOGIN_VALIDATE_HTTP;
		if(appContext.isHttpsLogin()){
			loginurl = URLs.LOGIN_VALIDATE_HTTPS;
		}
		
		try{
			return User.parse(_post(appContext, loginurl, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}

	/**
	 * �ҵĸ�������
	 * @param appContext
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static MyInformation myInformation(AppContext appContext, int uid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
				
		try{
			return MyInformation.parse(_post(appContext, URLs.MY_INFORMATION, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	public static MyInformation myInformation( AppContext ctx , String infoStr ) {
		try {
			JSONObject json = new JSONObject( infoStr );
			MyInformation info = new MyInformation();
			info.setName( json.getString("screen_name") );
			info.setFace( json.getString("profile_image_url") );
			String gender = json.getString("gender");
					
			if ( gender.equals("m") ) {
				Log.d( "Main" , "gender equals m ");
				info.setGender( MyInformation.GENDER_MAN );
			}else {
				Log.d( "Main" , "gender not equasls m ");
				info.setGender( MyInformation.GENDER_WOMAN );
			}
			info.setFanscount( json.getInt("followers_count")  );
			info.setFollowerscount( json.getInt("friends_count")  );
			info.setFavoritecount( 0 );
			String time_str = json.getString("created_at");
			String[] element_arr = time_str.split(" ");
			String mytime = element_arr[5] + "-" + Utility.MonthToIntStr( element_arr[1] ) + "-" + element_arr[2] +" " + element_arr[3] ;			
			info.setJointime( mytime );
			info.setFrom( json.getString("location") );
			info.setInterestArea("");
			info.setUserLevel("");		
			return info;
		}catch( JSONException e ) {
			e.printStackTrace();
			return null;
		}		
	}

	
	/**
	 * ��ȡ�û���Ϣ����רҳ���������û��Ķ�̬��Ϣ�Լ�������Ϣ��
	 * @param uid �Լ���uid
	 * @param hisuid ���鿴�û���uid
	 * @param hisname ���鿴�û����û���
	 * @param pageIndex ҳ������
	 * @param pageSize ÿҳ��ȡ�Ķ�̬����
	 * @return
	 * @throws AppException
	 */
	public static UserInformation information(AppContext appContext, int uid, int hisuid, String hisname, int pageIndex, int pageSize) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("hisuid", hisuid);
		params.put("hisname", hisname);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
				
		try{
			return UserInformation.parse(_post(appContext, URLs.USER_INFORMATION, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * �����û�֮���ϵ���ӹ�ע��ȡ����ע��
	 * @param uid �Լ���uid
	 * @param hisuid �Է��û���uid
	 * @param newrelation 0:ȡ�������Ĺ�ע 1:��ע��
	 * @return
	 * @throws AppException
	 */
	public static Result updateRelation(AppContext appContext, int uid, int hisuid, int newrelation) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("hisuid", hisuid);
		params.put("newrelation", newrelation);
				
		try{
			return Result.parse(_post(appContext, URLs.USER_UPDATERELATION, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ�û�֪ͨ��Ϣ
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static Notice getUserNotice(AppContext appContext, int uid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
				
		try{
			return Notice.parse(_post(appContext, URLs.USER_NOTICE, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ���֪ͨ��Ϣ
	 * @param uid
	 * @param type 1:@�ҵ���Ϣ 2:δ����Ϣ 3:���۸��� 4:�·�˿����
	 * @return
	 * @throws AppException
	 */
	public static Result noticeClear(AppContext appContext, int uid, int type) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("type", type);
				
		try{
			return Result.parse(_post(appContext, URLs.NOTICE_CLEAR, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * �û���˿����ע���б�
	 * @param uid
	 * @param relation 0:��ʾ�Լ��ķ�˿ 1:��ʾ�Լ��Ĺ�ע��
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static FriendList getFriendList(AppContext appContext, final int uid, final int relation, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.FRIENDS_LIST, new HashMap<String, Object>(){{
			put("uid", uid);
			put("relation", relation);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return FriendList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ��Ѷ�б�
	 * @param url
	 * @param catalog
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static NewsList getLatestThreadsList(AppContext appContext, final int uid , final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.LATEST_THREADS_LIST, new HashMap<String, Object>(){{
			if ( uid != 0 ) {
				put("uid", uid);
			}
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		Log.d( "bakey" , "get latst thread list url = " + newUrl );		
		try{
			return NewsList.parseJSON(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException) {
				throw (AppException)e;
			}
			throw AppException.network(e);
		}
	}
	
	public static NewsList getThreadsList(AppContext appContext, final int catalog, final int pageIndex, final int pageSize , String url ) throws AppException  {
		String newUrl = _MakeURL( url , new HashMap<String, Object>(){{
			put("catalog", catalog);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		try {
			return NewsList.parseJSON(http_get(appContext, newUrl));	
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}		
	}
	
	public static NewsList getRankThreadsList(AppContext appContext, final int catalog, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.RANK_THREADS_LIST, new HashMap<String, Object>(){{
			put("catalog", catalog);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		try{
			return NewsList.parseJSON(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	public static News getThreadsDetail(AppContext appContext, final int news_id) throws AppException {
		String newUrl = _MakeURL(URLs.THREAD_DETAIL, new HashMap<String, Object>(){{
			put("id", news_id);
		}});
		Log.d("bakey" , "thread detail url = " + newUrl );		
		try{
			return News.parseJSON(http_get(appContext, newUrl));			
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ��Ѷ������
	 * @param url
	 * @param news_id
	 * @return
	 * @throws AppException
	 */
	public static News getNewsDetail(AppContext appContext, final int news_id) throws AppException {
		String newUrl = _MakeURL(URLs.NEWS_DETAIL, new HashMap<String, Object>(){{
			put("id", news_id);
		}});
		
		try{
			return News.parse(http_get(appContext, newUrl));			
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡĳ�û��Ĳ����б�
	 * @param authoruid
	 * @param uid
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static BlogList getUserBlogList(AppContext appContext, final int authoruid, final String authorname, final int uid, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.USERBLOG_LIST, new HashMap<String, Object>(){{
			put("authoruid", authoruid);
			put("authorname", URLEncoder.encode(authorname));
			put("uid", uid);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});

		try{
			return BlogList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ�����б�
	 * @param type �Ƽ���recommend ���£�latest
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static BlogList getBlogList(AppContext appContext, final String type, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.BLOG_LIST, new HashMap<String, Object>(){{
			put("type", type);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});

		try{
			return BlogList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ɾ��ĳ�û��Ĳ���
	 * @param uid
	 * @param authoruid
	 * @param id
	 * @return
	 * @throws AppException
	 */
	public static Result delBlog(AppContext appContext, int uid, int authoruid, int id) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("authoruid", authoruid);
		params.put("id", id);

		try{
			return http_post(appContext, URLs.USERBLOG_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ��������
	 * @param blog_id
	 * @return
	 * @throws AppException
	 */
	public static Blog getBlogDetail(AppContext appContext, final int blog_id) throws AppException {
		String newUrl = _MakeURL(URLs.BLOG_DETAIL, new HashMap<String, Object>(){{
			put("id", blog_id);
		}});
		
		try{
			return Blog.parse(http_get(appContext, newUrl));			
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ�������
	 * @param soft_id
	 * @return
	 * @throws AppException
	 */
	public static Software getSoftwareDetail(AppContext appContext, final String ident) throws AppException {
		String newUrl = _MakeURL(URLs.SOFTWARE_DETAIL, new HashMap<String, Object>(){{
			put("ident", ident);
		}});
		
		try{
			return Software.parse(http_get(appContext, newUrl));			
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ�����б�
	 * @param url
	 * @param catalog
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static PostList getPostList(AppContext appContext, final int catalog, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.POST_LIST, new HashMap<String, Object>(){{
			put("catalog", catalog);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});

		try{
			return PostList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ���ӵ�����
	 * @param url
	 * @param post_id
	 * @return
	 * @throws AppException
	 */
	public static Post getPostDetail(AppContext appContext, final int post_id) throws AppException {
		String newUrl = _MakeURL(URLs.POST_DETAIL, new HashMap<String, Object>(){{
			put("id", post_id);
		}});
		try{
			return Post.parse(http_get(appContext, newUrl));			
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ������
	 * @param post ��uid��title��catalog��content��isNoticeMe��
	 * @return
	 * @throws AppException
	 */
	public static Result pubPost(AppContext appContext, Post post) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", post.getAuthorId());
		params.put("title", post.getTitle());
		params.put("catalog", post.getCatalog());
		params.put("content", post.getBody());
		params.put("isNoticeMe", post.getIsNoticeMe());				
		
		try{
			return http_post(appContext, URLs.POST_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ�����б�
	 * @param uid
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static TweetList getTweetList(AppContext appContext, final int uid, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.TWEET_LIST, new HashMap<String, Object>(){{
			put("uid", uid);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return TweetList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ��������
	 * @param tweet_id
	 * @return
	 * @throws AppException
	 */
	public static Tweet getTweetDetail(AppContext appContext, final int tweet_id) throws AppException {
		String newUrl = _MakeURL(URLs.TWEET_DETAIL, new HashMap<String, Object>(){{
			put("id", tweet_id);
		}});
		try{
			return Tweet.parse(http_get(appContext, newUrl));			
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ������
	 * @param Tweet-uid & msg & image
	 * @return
	 * @throws AppException
	 */
	public static Result pubTweet(AppContext appContext, Tweet tweet) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", tweet.getAuthorId());
		params.put("msg", tweet.getBody());
				
		Map<String, File> files = new HashMap<String, File>();
		if(tweet.getImageFile() != null)
			files.put("img", tweet.getImageFile());
		
		try{
			return http_post(appContext, URLs.TWEET_PUB, params, files);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}

	/**
	 * ɾ������
	 * @param uid
	 * @param tweetid
	 * @return
	 * @throws AppException
	 */
	public static Result delTweet(AppContext appContext, int uid, int tweetid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("tweetid", tweetid);

		try{
			return http_post(appContext, URLs.TWEET_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ��̬�б�
	 * @param uid
	 * @param catalog 1���¶�̬  2@��  3����  4���Լ� 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static ActiveList getActiveList(AppContext appContext, final int uid,final int catalog, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.ACTIVE_LIST, new HashMap<String, Object>(){{
			put("uid", uid);
			put("catalog", catalog);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return ActiveList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ�����б�
	 * @param uid
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static MessageList getMessageList(AppContext appContext, final int uid, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.MESSAGE_LIST, new HashMap<String, Object>(){{
			put("uid", uid);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return MessageList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��������
	 * @param uid ��¼�û�uid
	 * @param receiver �����ߵ��û�id
	 * @param content ��Ϣ���ݣ�ע�ⲻ�ܳ���250���ַ�
	 * @return
	 * @throws AppException
	 */
	public static Result pubMessage(AppContext appContext, int uid, int receiver, String content) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("receiver", receiver);
		params.put("content", content);

		try{
			return http_post(appContext, URLs.MESSAGE_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ת������
	 * @param uid ��¼�û�uid
	 * @param receiver �����ߵ��û���
	 * @param content ��Ϣ���ݣ�ע�ⲻ�ܳ���250���ַ�
	 * @return
	 * @throws AppException
	 */
	public static Result forwardMessage(AppContext appContext, int uid, String receiverName, String content) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("receiverName", receiverName);
		params.put("content", content);

		try{
			return http_post(appContext, URLs.MESSAGE_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ɾ������
	 * @param uid ��¼�û�uid
	 * @param friendid ������id
	 * @return
	 * @throws AppException
	 */
	public static Result delMessage(AppContext appContext, int uid, int friendid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("friendid", friendid);

		try{
			return http_post(appContext, URLs.MESSAGE_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ���������б�
	 * @param id ����id
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static BlogCommentList getBlogCommentList(AppContext appContext, final int id, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.BLOGCOMMENT_LIST, new HashMap<String, Object>(){{
			put("id", id);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return BlogCommentList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ����������
	 * @param blog ����id
	 * @param uid ��½�û���uid
	 * @param content ��������
	 * @return
	 * @throws AppException
	 */
	public static Result pubBlogComment(AppContext appContext, int blog, int uid, String content) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("blog", blog);
		params.put("uid", uid);
		params.put("content", content);
		
		try{
			return http_post(appContext, URLs.BLOGCOMMENT_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ����������
	 * @param blog ����id
	 * @param uid ��½�û���uid
	 * @param content ��������
	 * @param reply_id ����id
	 * @param objuid �����۵����۷����ߵ�uid
	 * @return
	 * @throws AppException
	 */
	public static Result replyBlogComment(AppContext appContext, int blog, int uid, String content, int reply_id, int objuid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("blog", blog);
		params.put("uid", uid);
		params.put("content", content);
		params.put("reply_id", reply_id);
		params.put("objuid", objuid);
		
		try{
			return http_post(appContext, URLs.BLOGCOMMENT_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ɾ����������
	 * @param uid ��¼�û���uid
	 * @param blogid ����id
	 * @param replyid ����id
	 * @param authorid ���۷����ߵ�uid
	 * @param owneruid ��������uid
	 * @return
	 * @throws AppException
	 */
	public static Result delBlogComment(AppContext appContext, int uid, int blogid, int replyid, int authorid, int owneruid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("blogid", blogid);		
		params.put("replyid", replyid);
		params.put("authorid", authorid);
		params.put("owneruid", owneruid);

		try{
			return http_post(appContext, URLs.BLOGCOMMENT_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	

	public static CommentList getCommentList(AppContext appContext, final int catalog, final int id, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.COMMENT_LIST, new HashMap<String, Object>(){{
			put("catalog", catalog);
			put("id", id);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		Log.d( "Main" , "load comment list url = " + newUrl );
		try{
			return CommentList.parseJson(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException) {
				throw (AppException)e;
			}
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��������
	 * @param catalog 1����  2����  3����  4��̬
	 * @param id ĳ�����ţ����ӣ�������id
	 * @param uid �û�uid
	 * @param content �������۵�����
	 * @param isPostToMyZone �Ƿ�ת�����ҵĿռ�  0��ת��  1ת��
	 * @return
	 * @throws AppException
	 */
	public static Result pubComment(AppContext appContext, int catalog, int id, int uid, String content, int isPostToMyZone) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("catalog", catalog);
		params.put("id", id);
		params.put("uid", uid);
		params.put("content", content);
		params.put("isPostToMyZone", isPostToMyZone);
		
		try{
			return http_post(appContext, URLs.COMMENT_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}

	/**
	 * 
	 * @param id ��ʾ�����۵�ĳ�����ţ����ӣ�������id ����ĳ����Ϣ�� friendid 
	 * @param catalog ��ʾ����������ʲô���ͣ�1����  2����  3����  4��̬
	 * @param replyid ��ʾ���ظ��ĵ�������id
	 * @param authorid ��ʾ�����۵�ԭʼ����id
	 * @param uid �û�uid һ�㶼�ǵ�ǰ��¼�û�uid
	 * @param content �������۵�����
	 * @return
	 * @throws AppException
	 */
	public static Result replyComment(AppContext appContext, int id, int catalog, int replyid, int authorid, int uid, String content) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("catalog", catalog);
		params.put("id", id);
		params.put("uid", uid);
		params.put("content", content);
		params.put("replyid", replyid);
		params.put("authorid", authorid);
		
		try{
			return http_post(appContext, URLs.COMMENT_REPLY, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ɾ������
	 * @param id ��ʾ�����۶�Ӧ��ĳ������,����,������id ����ĳ����Ϣ�� friendid
	 * @param catalog ��ʾ����������ʲô���ͣ�1����  2����  3����  4��̬&����
	 * @param replyid ��ʾ���ظ��ĵ�������id
	 * @param authorid ��ʾ�����۵�ԭʼ����id
	 * @return
	 * @throws AppException
	 */
	public static Result delComment(AppContext appContext, int id, int catalog, int replyid, int authorid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		params.put("catalog", catalog);
		params.put("replyid", replyid);
		params.put("authorid", authorid);

		try{
			return http_post(appContext, URLs.COMMENT_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * �û��ղ��б�
	 * @param uid �û�UID
	 * @param type 0:ȫ���ղ� 1:��� 2:���� 3:���� 4:���� 5:����
	 * @param pageIndex ҳ������ 0��ʾ��һҳ
	 * @param pageSize ÿҳ������
	 * @return
	 * @throws AppException
	 */
	public static FavoriteList getFavoriteList(AppContext appContext, final int uid, final int type, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.FAVORITE_LIST, new HashMap<String, Object>(){{
			put("uid", uid);
			put("type", type);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return FavoriteList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}	
	
	/**
	 * �û�����ղ�
	 * @param uid �û�UID
	 * @param objid ����������ID �����ʴ�ID ���߶���ID
	 * @param type 1:��� 2:���� 3:���� 4:���� 5:����
	 * @return
	 * @throws AppException
	 */
	public static Result addFavorite(AppContext appContext, int uid, int objid, int type) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("objid", objid);
		params.put("type", type);

		try{
			return http_post(appContext, URLs.FAVORITE_ADD, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * �û�ɾ���ղ�
	 * @param uid �û�UID
	 * @param objid ����������ID �����ʴ�ID ���߶���ID
	 * @param type 1:��� 2:���� 3:���� 4:���� 5:����
	 * @return
	 * @throws AppException
	 */
	public static Result delFavorite(AppContext appContext, int uid, int objid, int type) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("objid", objid);
		params.put("type", type);

		try{
			return http_post(appContext, URLs.FAVORITE_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��ȡ�����б�
	 * @param catalog ȫ��:all ����:news  �ʴ�:post ���:software ����:blog ����:code
	 * @param content ����������
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static SearchList getSearchList(AppContext appContext, String catalog, String content, int pageIndex, int pageSize) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("catalog", catalog);
		params.put("content", content);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);

		try{
			return SearchList.parse(_post(appContext, URLs.SEARCH_LIST, params, null));	
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ����б�
	 * @param searchTag �������  �Ƽ�:recommend ����:time ����:view ����:list_cn
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static SoftwareList getSoftwareList(AppContext appContext,final String searchTag,final int pageIndex,final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.SOFTWARE_LIST, new HashMap<String, Object>(){{
			put("searchTag", searchTag);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});

		try{
			return SoftwareList.parse(http_get(appContext, newUrl));	
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ������������б�
	 * @param searchTag ��softwarecatalog_list��ȡ��tag
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static SoftwareList getSoftwareTagList(AppContext appContext,final int searchTag,final int pageIndex,final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.SOFTWARETAG_LIST, new HashMap<String, Object>(){{
			put("searchTag", searchTag);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});

		try{
			return SoftwareList.parse(http_get(appContext, newUrl));	
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * ��������б�
	 * @param tag ��һ��:0  �ڶ���:tag
	 * @return
	 * @throws AppException
	 */
	public static SoftwareCatalogList getSoftwareCatalogList(AppContext appContext,final int tag) throws AppException {
		String newUrl = _MakeURL(URLs.SOFTWARECATALOG_LIST, new HashMap<String, Object>(){{
			put("tag", tag);
		}});

		try{
			return SoftwareCatalogList.parse(http_get(appContext, newUrl));	
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
}
