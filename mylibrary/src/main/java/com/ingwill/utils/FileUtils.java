package com.ingwill.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Random;


public final class FileUtils {
	/**
	 * 根据文件得到相应的Uri对象，7.0以后用fromFile这种处理要出错
	 * @param context
	 * @param file
	 * @return
	 */
	public static Uri getFileUri(Context context,File file){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return FileProvider.getUriForFile(context, getFileProviderAuthority(context), file);
			//return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
		}else{
			return Uri.fromFile(file);
		}
	}

	public static int copy(String fromFile, String toFile)
	{
		//要复制的文件目录
		File[] currentFiles;
		File root = new File(fromFile);
		//如同判断SD卡是否存在或者文件是否存在
		//如果不存在则 return出去
		if(!root.exists())
		{
			return -1;
		}
		//如果存在则获取当前目录下的全部文件 填充数组
		currentFiles = root.listFiles();

		//目标目录
		File targetDir = new File(toFile);
		//创建目录
		if(!targetDir.exists())
		{
			targetDir.mkdirs();
		}
		//遍历要复制该目录下的全部文件
		for(int i= 0;i<currentFiles.length;i++)
		{
			if(currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
			{
				copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

			}else//如果当前项为文件则进行文件拷贝
			{
				copySdcardFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
			}
		}
		return 0;
	}


	//文件拷贝
	//要复制的目录下的所有非子目录(文件夹)文件拷贝
	public static int copySdcardFile(String fromFile, String toFile)
	{

		try
		{
			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0)
			{
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return 0;

		} catch (Exception ex)
		{
			return -1;
		}
	}

	/**
	 * 设置文件最后修改时间为当前时间
	 * @param strFile
	 * @return
	 */
	public static boolean setFileCurrentTime(String strFile){
		File f=new File(strFile);
		return f.setLastModified(System.currentTimeMillis());
	}

	//判断文件是否存在
	public static boolean isExist(String strFile)
	{
		try
		{
			File f=new File(strFile);
			if(!f.exists())
			{
				return false;
			}

		}
		catch (Exception e)
		{
			return false;
		}

		return true;
	}
	/**
	 * 获取FileProvider的auth
	 *
	 * @return
	 */
	public static  String getFileProviderAuthority(Context context) {
		try {
			for (ProviderInfo provider : context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS).providers) {
				if (FileProvider.class.getName().equals(provider.name) && provider.authority.endsWith(".provider")) {
					return provider.authority;
				}
			}
		} catch (PackageManager.NameNotFoundException ignore) {
		}
		return null;
	}

	/**
	 * 把流读成字符串
	 *
	 * @param is
	 *            输入流
	 * @return 字符串
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {

		} finally {
			try {
				is.close();
			} catch (IOException e) {

			}
		}
		return sb.toString();
	}

	/**
	 * 关闭流
	 *
	 * @param stream
	 *            可关闭的流
	 */
	public static void closeStream(Closeable stream) {
		try {
			if (stream != null)
				stream.close();
		} catch (IOException e) {

		}
	}

	public static byte[] InputStreamToByte(InputStream is) throws IOException {

		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		int ch;
		while ((ch = is.read()) != -1) {
			bytestream.write(ch);
		}
		byte byteData[] = bytestream.toByteArray();
		bytestream.close();
		return byteData;
	}
    public static String getRealPath(Activity context, Uri uri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor actualimagecursor = context.managedQuery(uri,proj,null,null,null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        return img_path;
    }
	/**
	 * 得到资源文件内容
	 * @param resfilepath
	 * @return
	 */
	public static String getResFile(String resfilepath){
		FileUtils f=new FileUtils();
		InputStream is =f.getClass().getResourceAsStream(resfilepath);
		BufferedReader br;
        StringBuilder strBlder = new StringBuilder("");
        try {  
            br = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while (null != (line = br.readLine())) {  
                strBlder.append(line + "\n");  
            }  
            br.close();  
        } catch (FileNotFoundException e) {
            e.printStackTrace();  
        } catch (IOException e) {
            e.printStackTrace();  
        }
        return strBlder.toString();
	}

	public static   boolean checkImageExtension(String file){
		String ext=getExtension(file);
		boolean flag=false;
		String[] limitFileType=new String[]{"jpg","gif","jpeg","png","bmp"};
		for(String e:limitFileType){
			if(e.equals(ext)){
				flag=true;
				break;
			}
		}
		return flag;
	}
	/**
	 * 
	 * @Title: getExtension
	 * @category 返回文件名后缀,没有. 比如说文件名是aaa.txt 扩展名是txt
	 * @param fileName
	 * @return
	 * @throws
	 */
	public static String getExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
	}

	/**
	 * 使用时间戳和前缀的方式重命名上传文件的名称
	 * 
	 * @param prefix
	 */
	public static final String resetUploadFileNamePrefix(String prefix,
														 String filename) {
		return prefix + System.currentTimeMillis() + getExtension(filename);
	}

	/**
	 * 使用时间戳和后缀的方式重命名上传文件的名称
	 * @param suffix
	 * @param filename
     * @return
     */
	public static final String resetUploadFileNameSuffix(String suffix,
														 String filename) {
		return System.currentTimeMillis() + suffix + getExtension(filename);
	}
	
	public static final String resetUploadFileName(String filename) {
		Random ran = new Random();
		return "" + ran.nextInt(10000) + System.currentTimeMillis()
				+ getExtension(filename);
	}

	/**
	 * 文件名是否有效 为*.*格式，前一个*不能包含/\<>*?|，后一个*只能是数字或字母并且有一个以上
	 * 
	 * @param filename
	 * @return
	 */
	public static final boolean isEffectiveFileName(String filename) {
		return null != filename
				&& filename.matches("[^/\\\\<>*?|]+\\.[0-9a-zA-Z]{1,}$");
	}

	/**
	 * 是否是图片文件 jpg、gif、bmp、png、jpeg
	 * 
	 * @param filename
	 * @return
	 */
	public static final boolean isImageFile(String filename) {
		return filename.matches("[^/\\\\<>*?|]+\\.(?i)(jpg|png|gif|bmp|jpeg)$");
	}

	/**
	 * 是否是压缩文件
	 * 
	 * @param filename
	 * @return
	 */
	public static final boolean isArchiveFile(String filename) {
		return filename.matches("[^/\\\\<>*?|]+\\.(?i)(7z|rar|zip|gz)$");
	}

	/**
	 * 是否是文档
	 * 
	 * @param filename
	 * @return
	 */
	public static final boolean isDocumentFile(String filename) {
		return filename.matches("[^/\\\\<>*?|]+\\.(?i)(doc|docx|pdf|xps|wps)$");
	}


	/**
	 * 删除文件
	 * @param filePath
	 * @return
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (!file.isDirectory()) {
			file.delete();
		} else{
			String[] fileList = file.list();
			for (int i = 0; i < fileList.length; i++) {
				File delFile = new File(filePath + "\\" + fileList[i]);
				if (!delFile.isDirectory()) {
					delFile.delete();
				} else{
					deleteFile(filePath + "\\" + fileList[i]);
				}
			}
			file.delete();
		}
		return true;
	}

	/**
	 * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
	 * @param activity
	 * @param imageUri
	 * @author yaoxing
	 * @date 2014-10-12
	 */
	@TargetApi(19)
	public static String getImageAbsolutePath(Activity context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}


}
