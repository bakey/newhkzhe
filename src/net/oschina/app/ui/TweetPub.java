package net.oschina.app.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.app.R;
import net.oschina.app.adapter.GridViewFaceAdapter;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.Tweet;
import net.oschina.app.common.FileUtils;
import net.oschina.app.common.ImageUtils;
import net.oschina.app.common.MediaUtils;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ��������
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class TweetPub extends Activity{

	private FrameLayout mForm;
	private ImageView mBack;
	private EditText mContent;
	private Button mPublish;
	private ImageView mFace;
	private ImageView mPick;
	private ImageView mAtme;
	private ImageView mSoftware;
	private ImageView mImage;
	private LinearLayout mClearwords;
	private TextView mNumberwords;

	private GridView mGridView;
	private GridViewFaceAdapter mGVFaceAdapter;
	
	private Tweet tweet;
	private File imgFile;
	private String theLarge;
	private String theThumbnail;
	private InputMethodManager imm;
	
	private String tempTweetKey = AppConfig.TEMP_TWEET;
	private String tempTweetImageKey = AppConfig.TEMP_TWEET_IMAGE;
	
	public static LinearLayout mMessage;
	public static Context mContext;
	
	private static final int MAX_TEXT_LENGTH = 160;//�����������
	private static final String TEXT_ATME = "@�������û��� ";
	private static final String TEXT_SOFTWARE = "#������������#";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tweet_pub);
		
		mContext = this;
		//�����̹�����
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		
		//��ʼ��������ͼ
		this.initView();
		//��ʼ��������ͼ
		this.initGridView();
	}
	
    @Override
	protected void onDestroy() {
    	mContext = null;
    	super.onDestroy();
	}
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if(mGridView.getVisibility() == View.VISIBLE){
    		//���ر���
    		hideFace();
    	}
    }
    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		if(mGridView.getVisibility() == View.VISIBLE) {
    			//���ر���
    			hideFace();
    		}else{
    			return super.onKeyDown(keyCode, event);
    		}
    	}
    	return true;
    }
    
	//��ʼ����ͼ�ؼ�
    private void initView()
    {    	
    	mForm = (FrameLayout)findViewById(R.id.tweet_pub_form);
    	mBack = (ImageView)findViewById(R.id.tweet_pub_back);
    	mMessage = (LinearLayout)findViewById(R.id.tweet_pub_message);
    	mImage = (ImageView)findViewById(R.id.tweet_pub_image);
    	mPublish = (Button)findViewById(R.id.tweet_pub_publish);
    	mContent = (EditText)findViewById(R.id.tweet_pub_content);
    	mFace = (ImageView)findViewById(R.id.tweet_pub_footbar_face);
    	mPick = (ImageView)findViewById(R.id.tweet_pub_footbar_photo);
    	mAtme = (ImageView)findViewById(R.id.tweet_pub_footbar_atme);
    	mSoftware = (ImageView)findViewById(R.id.tweet_pub_footbar_software);
    	mClearwords = (LinearLayout)findViewById(R.id.tweet_pub_clearwords);
    	mNumberwords = (TextView)findViewById(R.id.tweet_pub_numberwords);
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mPublish.setOnClickListener(publishClickListener);
    	mImage.setOnLongClickListener(imageLongClickListener);
    	mFace.setOnClickListener(faceClickListener);
    	mPick.setOnClickListener(pickClickListener);
    	mAtme.setOnClickListener(atmeClickListener);
    	mSoftware.setOnClickListener(softwareClickListener);
    	mClearwords.setOnClickListener(clearwordsClickListener);
    	
    	//@ĳ��
    	String atme = getIntent().getStringExtra("at_me");
    	int atuid = getIntent().getIntExtra("at_uid",0);
    	if(atuid > 0){
    		tempTweetKey = AppConfig.TEMP_TWEET + "_" + atuid;
    		tempTweetImageKey = AppConfig.TEMP_TWEET_IMAGE + "_" + atuid;
    	}
    	
    	//�༭�������ı�����
    	mContent.addTextChangedListener(new TextWatcher() {		
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//���浱ǰEditText���ڱ༭������
				((AppContext)getApplication()).setProperty(tempTweetKey, s.toString());
				//��ʾʣ������������
				mNumberwords.setText((MAX_TEXT_LENGTH - s.length()) + "");
			}		
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}		
			public void afterTextChanged(Editable s) {}
		});
    	//�༭������¼�
    	mContent.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//��ʾ������
				showIMM();
			}
		});
    	//���������������
    	InputFilter[] filters = new InputFilter[1];  
    	filters[0] = new InputFilter.LengthFilter(MAX_TEXT_LENGTH);
    	mContent.setFilters(filters);
    	
    	//��ʾ��ʱ�༭����
		UIHelper.showTempEditContent(this, mContent, tempTweetKey);
		//��ʾ��ʱ����ͼƬ
		String tempImage = ((AppContext)getApplication()).getProperty(tempTweetImageKey);
		if(!StringUtils.isEmpty(tempImage)) {
    		Bitmap bitmap = ImageUtils.loadImgThumbnail(tempImage, 100, 100);
    		if(bitmap != null) {
    			imgFile = new File(tempImage);
				mImage.setImageBitmap(bitmap);
				mImage.setVisibility(View.VISIBLE);
			}
		}
		
		if(atuid > 0 && mContent.getText().length() == 0){
			mContent.setText(atme);
    		mContent.setSelection(atme.length());//���ù��λ��
		}
    }
    
    //��ʼ������ؼ�
    private void initGridView() {
    	mGVFaceAdapter = new GridViewFaceAdapter(this);
    	mGridView = (GridView)findViewById(R.id.tweet_pub_faces);
    	mGridView.setAdapter(mGVFaceAdapter);
    	mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//����ı���
				SpannableString ss = new SpannableString(view.getTag().toString());
				Drawable d = getResources().getDrawable((int)mGVFaceAdapter.getItemId(position));
				d.setBounds(0, 0, 35, 35);//���ñ���ͼƬ����ʾ��С
				ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
				ss.setSpan(span, 0, view.getTag().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);				 
				//�ڹ�����ڴ��������
				mContent.getText().insert(mContent.getSelectionStart(), ss);				
			}    		
    	});
    }
    
    private void showIMM() {
    	mFace.setTag(1);
    	showOrHideIMM();
    }
    private void showFace() {
		mFace.setImageResource(R.drawable.widget_bar_keyboard);
		mFace.setTag(1);
		mGridView.setVisibility(View.VISIBLE);
    }
    private void hideFace() {
    	mFace.setImageResource(R.drawable.widget_bar_face);
		mFace.setTag(null);
		mGridView.setVisibility(View.GONE);
    }
    private void showOrHideIMM() {
    	if(mFace.getTag() == null){
			//����������
			imm.hideSoftInputFromWindow(mFace.getWindowToken(), 0);
			//��ʾ����
			showFace();				
		}else{
			//��ʾ������
			imm.showSoftInput(mContent, 0);
			//���ر���
			hideFace();
		}
    }
    
    private View.OnClickListener faceClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			showOrHideIMM();
		}
	};
    
	private View.OnClickListener pickClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//����������
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			//���ر���
			hideFace();		
			
			CharSequence[] items = {
					TweetPub.this.getString(R.string.img_from_album),
					TweetPub.this.getString(R.string.img_from_camera)
			};
			imageChooseItem(items);
		}
	};
	
	private View.OnClickListener atmeClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//��ʾ������
			showIMM();			
				
    		//�ڹ�����ڴ����롰@�û�����
			int curTextLength = mContent.getText().length();
			if(curTextLength < MAX_TEXT_LENGTH) {
				String atme = TEXT_ATME;
				int start,end;
				if((MAX_TEXT_LENGTH - curTextLength) >= atme.length()) {
					start = mContent.getSelectionStart() + 1;
					end = start + atme.length() - 2;
				} else {
					int num = MAX_TEXT_LENGTH - curTextLength;
					if(num < atme.length()) {
						atme = atme.substring(0, num);
					}
					start = mContent.getSelectionStart() + 1;
					end = start + atme.length() - 1;
				}
				if(start > MAX_TEXT_LENGTH || end > MAX_TEXT_LENGTH) {
					start = MAX_TEXT_LENGTH;
					end = MAX_TEXT_LENGTH;
				}
				mContent.getText().insert(mContent.getSelectionStart(), atme);
				mContent.setSelection(start, end);//����ѡ������
			}
		}
	};
	
	private View.OnClickListener softwareClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//��ʾ������
			showIMM();
			
			//�ڹ�����ڴ����롰#������#��
			int curTextLength = mContent.getText().length();
			if(curTextLength < MAX_TEXT_LENGTH) {
				String software = TEXT_SOFTWARE;
				int start,end;
				if((MAX_TEXT_LENGTH - curTextLength) >= software.length()) {
					start = mContent.getSelectionStart() + 1;
					end = start + software.length() - 2;
				} else {
					int num = MAX_TEXT_LENGTH - curTextLength;
					if(num < software.length()) {
						software = software.substring(0, num);
					}					
					start = mContent.getSelectionStart() + 1;
					end = start + software.length() - 1;
				}
				if(start > MAX_TEXT_LENGTH || end > MAX_TEXT_LENGTH) {
					start = MAX_TEXT_LENGTH;
					end = MAX_TEXT_LENGTH;
				}
				mContent.getText().insert(mContent.getSelectionStart(), software);
	    		mContent.setSelection(start, end);//����ѡ������
			}
		}
	};
	
	private View.OnClickListener clearwordsClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			String content = mContent.getText().toString();
			if(!StringUtils.isEmpty(content)){
				UIHelper.showClearWordsDialog(v.getContext(), mContent, mNumberwords);
			}
		}
	};
	
	private View.OnLongClickListener imageLongClickListener = new View.OnLongClickListener() {
		public boolean onLongClick(View v) {
			//����������
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			
			new AlertDialog.Builder(v.getContext())
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle(getString(R.string.delete_image))
			.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//���֮ǰ����ı༭ͼƬ
					((AppContext)getApplication()).removeProperty(tempTweetImageKey);
					
					imgFile = null;
					mImage.setVisibility(View.GONE);
					dialog.dismiss();
				}
			})
			.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create().show();
			return true;
		}
		
	};
	
	/**
	 * ����ѡ��
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items )
	{
		AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle(R.string.ui_insert_image).setIcon(android.R.drawable.btn_star).setItems(items,
			new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int item)
				{
					//�ֻ�ѡͼ
					if( item == 0 )
					{
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
						intent.addCategory(Intent.CATEGORY_OPENABLE); 
						intent.setType("image/*"); 
						startActivityForResult(Intent.createChooser(intent, "ѡ��ͼƬ"),ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD); 
					}
					//����
					else if( item == 1 )
					{	
						String savePath = "";
						//�ж��Ƿ������SD��
						String storageState = Environment.getExternalStorageState();		
						if(storageState.equals(Environment.MEDIA_MOUNTED)){
							savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OSChina/Camera/";//�����Ƭ���ļ���
							File savedir = new File(savePath);
							if (!savedir.exists()) {
								savedir.mkdirs();
							}
						}
						
						//û�й���SD�����޷������ļ�
						if(StringUtils.isEmpty(savePath)){
							UIHelper.ToastMessage(TweetPub.this, "�޷�������Ƭ������SD���Ƿ����");
							return;
						}

						String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
						String fileName = "osc_" + timeStamp + ".jpg";//��Ƭ����
						File out = new File(savePath, fileName);
						Uri uri = Uri.fromFile(out);
						
						theLarge = savePath + fileName;//����Ƭ�ľ���·��
						
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
						startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
					}   
				}}).create();
		
		 imageDialog.show();
	}
	
	@Override 
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{ 
    	if(resultCode != RESULT_OK) return;
		
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what == 1 && msg.obj != null){
					//��ʾͼƬ
					mImage.setImageBitmap((Bitmap)msg.obj);
					mImage.setVisibility(View.VISIBLE);
				}
			}
		};
		
		new Thread(){
			public void run() 
			{
				Bitmap bitmap = null;
				
		        if(requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) 
		        {         	
		        	if(data == null)  return;
		        	
		        	Uri thisUri = data.getData();
		        	String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(thisUri);
		        	
		        	//����Ǳ�׼Uri
		        	if(StringUtils.isEmpty(thePath))
		        	{
		        		theLarge = ImageUtils.getAbsoluteImagePath(TweetPub.this,thisUri);
		        	}
		        	else
		        	{
		        		theLarge = thePath;
		        	}
		        	
		        	String attFormat = FileUtils.getFileFormat(theLarge);
		        	if(!"photo".equals(MediaUtils.getContentType(attFormat)))
		        	{
		        		Toast.makeText(TweetPub.this, getString(R.string.choose_image), Toast.LENGTH_SHORT).show();
		        		return;
		        	}
		        	
		        	//��ȡͼƬ����ͼ ֻ��Android2.1���ϰ汾֧��
		    		if(AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.ECLAIR_MR1)){
		    			String imgName = FileUtils.getFileName(theLarge);
		    			bitmap = ImageUtils.loadImgThumbnail(TweetPub.this, imgName, MediaStore.Images.Thumbnails.MICRO_KIND);
		    		}
		        	
		        	if(bitmap == null && !StringUtils.isEmpty(theLarge))
		        	{
		        		bitmap = ImageUtils.loadImgThumbnail(theLarge, 100, 100);
		        	}
		        }
		        //����ͼƬ
		        else if(requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA)
		        {	
		        	if(bitmap == null && !StringUtils.isEmpty(theLarge))
		        	{
		        		bitmap = ImageUtils.loadImgThumbnail(theLarge, 100, 100);
		        	}
		        }
		        
				if(bitmap!=null)
				{
					//�����Ƭ���ļ���
					String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OSChina/Camera/";
					File savedir = new File(savePath);
					if (!savedir.exists()) {
						savedir.mkdirs();
					}
					
					String largeFileName = FileUtils.getFileName(theLarge);
					String largeFilePath = savePath + largeFileName;
					//�ж��Ƿ��Ѵ�������ͼ
					if(largeFileName.startsWith("thumb_") && new File(largeFilePath).exists()) 
					{
						theThumbnail = largeFilePath;
						imgFile = new File(theThumbnail);
					} 
					else 
					{
						//�����ϴ���800����ͼƬ
						String thumbFileName = "thumb_" + largeFileName;
						theThumbnail = savePath + thumbFileName;
						if(new File(theThumbnail).exists())
						{
							imgFile = new File(theThumbnail);
						}
						else
						{
							try {
								//ѹ���ϴ���ͼƬ
								ImageUtils.createImageThumbnail(TweetPub.this, theLarge, theThumbnail, 800, 80);
								imgFile = new File(theThumbnail);
							} catch (IOException e) {
								e.printStackTrace();
							}	
						}						
					}					
					//���涯����ʱͼƬ
					((AppContext)getApplication()).setProperty(tempTweetImageKey, theThumbnail);
					
					Message msg = new Message();
					msg.what = 1;
					msg.obj = bitmap;
					handler.sendMessage(msg);
				}				
			};
		}.start();
    }
	
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//����������
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			
			String content = mContent.getText().toString();
			if(StringUtils.isEmpty(content)){
				UIHelper.ToastMessage(v.getContext(), "�����붯������");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(TweetPub.this);
				return;
			}	
			
			mMessage.setVisibility(View.VISIBLE);
			mForm.setVisibility(View.GONE);
			
			tweet = new Tweet();
			tweet.setAuthorId(ac.getLoginUid());
			tweet.setBody(content);
			tweet.setImageFile(imgFile);
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					mMessage.setVisibility(View.GONE);
					if(msg.what == 1){
						//���֮ǰ����ı༭����
						ac.removeProperty(tempTweetKey,tempTweetImageKey);						
						finish();
					}else{
						mMessage.setVisibility(View.GONE);
						mForm.setVisibility(View.VISIBLE);
					}
				}				
			};
			
			new Thread(){
				public void run() {
					Message msg =new Message();
					Result res = null;
					int what = 0;
					try {
						res = ac.pubTweet(tweet);
						what = 1;
						msg.what = 1;
						msg.obj = res;
		            } catch (AppException e) {
		            	e.printStackTrace();
						msg.what = -1;
						msg.obj = e;
		            }
					handler.sendMessage(msg);
					UIHelper.sendBroadCastTweet(TweetPub.this, what, res, tweet);
				}
			}.start();
		}
	};
}