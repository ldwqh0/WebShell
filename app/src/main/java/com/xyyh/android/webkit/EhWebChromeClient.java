package com.xyyh.android.webkit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.xyyh.tjschool.R;

import java.io.File;

import static com.xyyh.android.webkit.Codes.*;

public class EhWebChromeClient extends WebChromeClient {

    private Activity activity;

    private ValueCallback valueCallback;


    public EhWebChromeClient(Activity activity) {
        this.activity = activity;
    }


    private File tempCameraFile;

    /**
     * 拦截处理文件选择框的事件
     *
     * @param webView
     * @param filePathCallback
     * @param fileChooserParams
     * @return
     */
    @Override
    public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        this.valueCallback = filePathCallback;
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(R.string.file_dialog_title);
        dialog.setCancelable(false);
        dialog.setItems(R.array.file_dialog_items, (DialogInterface dialog_, int which) -> {
            switch (which) {
                case 0:
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String path = getDefaultPath() + File.separator + System.currentTimeMillis() + ".jpg";
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempCameraFile = new File(path)));
                    cameraIntent.putExtra(MediaStore.Images.Media.CONTENT_TYPE, "image/jpeg");
                    activity.startActivityForResult(cameraIntent, IMAGE_CAPTURE);
                    break;
                case 1:
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    String path_ = getDefaultPath() + File.separator + System.currentTimeMillis() + ".mp4";
                    videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempCameraFile = new File(path_)));
                    videoIntent.putExtra(MediaStore.Images.Media.CONTENT_TYPE, "video/mpeg4");
                    activity.startActivityForResult(videoIntent, IMAGE_CAPTURE);
                    break;
                case 2:
                    EhWebChromeClient.this.openSystemDialog();
                    break;
                default:
                    if (EhWebChromeClient.this.valueCallback != null) {
                        EhWebChromeClient.this.valueCallback.onReceiveValue(null);
                    }
                    EhWebChromeClient.this.valueCallback = null;
                    break;
            }
        });
        dialog.show();
        return true;
    }

    public void callbackValue(Intent intent) {
        Uri[] uris = null;
        if (intent != null) {
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                int count = clipData.getItemCount();
                uris = new Uri[count];
                for (int i = 0; i < count; i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    uris[i] = item.getUri();
                }
            } else {
                Uri uri = intent.getData();
                uris = new Uri[]{uri};
            }
        }
        valueCallback.onReceiveValue(uris);
        valueCallback = null;
    }

    public void callBackCamera() {
        if (tempCameraFile.exists()) {
            valueCallback.onReceiveValue(new Uri[]{Uri.fromFile(tempCameraFile)});
        } else {
            valueCallback.onReceiveValue(null);
        }
        valueCallback = null;
    }

    public void callBackVideo() {
        callBackCamera();
    }

    private void openSystemDialog() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        activity.startActivityForResult(Intent.createChooser(i, "select a file"), FILE_CHOOSE_REQUEST);
    }


    private String getDefaultPath() {
        File externalDataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String path = externalDataDir.getAbsolutePath() + File.separator + activity.getResources().getString(R.string.app_name);
        externalDataDir = new File(path);
        if (!externalDataDir.exists()) {
            externalDataDir.mkdirs();
        }
        return path;
    }
}