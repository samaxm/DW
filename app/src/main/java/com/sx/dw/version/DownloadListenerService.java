package com.sx.dw.version;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.core.util.ToastUtil;

import java.io.File;
/**
 * @ClassName: DownloadListenerService
 * @Description: 下载更新安装包 
 * TODO 待优化，分层处理
 * @author: fanjie
 * @date: 2016年8月23日 下午12:51:22
 */
public class DownloadListenerService extends Service
{
    private static final String UPDATE_INFO = "update_info";

    private long downloadId;
    private BroadcastReceiver downLoadReceiver;
    private UpdateInfo info;
    
    public static void startMe(Context context, UpdateInfo info){
        Intent i = new Intent(context,DownloadListenerService.class);
        i.putExtra(UPDATE_INFO,info);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    
    @Override
    public void onStart(Intent intent, int startId)
    {
        LogUtils.d("onStart");
        super.onStart(intent, startId);
        info = (UpdateInfo) intent.getSerializableExtra(UPDATE_INFO);

        if(TextUtils.isEmpty(info.getDownloadurl())){
            throw new NullPointerException("启动Service需要传入的Url为空");
        }
        
        downLoad();
    }

    private void downLoad()
    {
        Uri uri = Uri.parse(info.getDownloadurl());
        LogUtils.d("downLoad, uri = " + info.getDownloadurl());
//        File folder = new File(HOME);
        File folder = Environment.getExternalStoragePublicDirectory("DW");
        if(!(folder.exists()&&folder.isDirectory())){
            folder.mkdirs();
        }
        String fileName = "DW"+info.getVersionName()+".apk";
        Uri localPath = Uri.fromFile(new File(folder,fileName));
        LogUtils.d("localPath = " + localPath);
        final DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Request request = new Request(uri);
//        TODO 有wifi时下载，或根据用户的设置传入
        request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
        request.setDestinationUri(localPath);
        request.setVisibleInDownloadsUi(true);
        downloadId = dm.enqueue(request);
        downLoadReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent)
            {
                LogUtils.d("onReceive");
                long sysDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                if(downloadId == sysDownloadId){
                    Query query = new Query();
                    query.setFilterById(downloadId);
                    Cursor cursor = dm.query(query);
                    if(cursor.moveToFirst()){
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                        if(path == null){
                            ToastUtil.showToast("更新失败");
                            return;
                        }else{
//                            安装应用
                            path = path.substring(path.indexOf(Environment.getExternalStorageDirectory().toString(),0));
                            Intent installIntent = new Intent(Intent.ACTION_VIEW);
                            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            installIntent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                            startActivity(installIntent);
                            stopSelf();
                        }
                    }
//                    此处未关闭将导致内存溢出
                    cursor.close();
                }
                
            }
            
        };
        registerReceiver(downLoadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(downLoadReceiver);
    }

}
