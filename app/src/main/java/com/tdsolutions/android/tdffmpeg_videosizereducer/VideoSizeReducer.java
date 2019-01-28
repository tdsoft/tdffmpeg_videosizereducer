package com.tdsolutions.android.tdffmpeg_videosizereducer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

public class VideoSizeReducer {

    private static FFmpeg ffmpeg;
    public static void initialize(Context context){
        ffmpeg = FFmpeg.getInstance(context);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            Toast.makeText(context,"FFmpeg is not supported by device", Toast.LENGTH_LONG).show();
        }
    }
    public FFmpeg compressVideo(Context context, File inputFile, final File outPutFile, @NonNull final OnVideoCompressListener onVideoCompressListener) {
        if(outPutFile!= null && outPutFile.exists()){
            outPutFile.delete();
        }
        // ffmpeg is supported
        try {
            final long totalDur = getVideoDuration(context, inputFile);
//            ffmpeg.execute(new String[]{"-i", inputFile.getAbsolutePath(), "-c:v", "libx264", "-crf", "24", "-b:v", "1M", "-c:a", "aac", outPutFile.getAbsolutePath()}, onVideoCompressListener); work
//            ffmpeg.execute(new String[]{"-y", "-i", inputFile.getAbsolutePath(), "-b:v", "3000000", outPutFile.getAbsolutePath()}, onVideoCompressListener); work fine
//            ffmpeg.execute(new String[]{"-y", "-i", inputFile.getAbsolutePath(), "-filter:v", "scale=720:-1", "-c:a", "copy", outPutFile.getAbsolutePath()}, onVideoCompressListener); //work fine take(4min 10sec) 70.27MB => 7.19MB
            ffmpeg.execute(new String[]{"-y", "-i", inputFile.getAbsolutePath(), "-vf", "scale=720:-1","-c:a", "copy", outPutFile.getAbsolutePath()}, new ExecuteBinaryResponseHandler(){
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onProgress(String message) {
                    super.onProgress(message);
                    Log.d("video comprs onProgress", message);
                    Pattern timePattern = Pattern.compile("(?<=time=)[\\d:.]*");
                    Scanner sc = new Scanner(message);

                    String match = sc.findWithinHorizon(timePattern, 0);
                    if (match != null) {
                        String[] matchSplit = match.split(":");
                        if (totalDur != 0) {
                            float progress = (((Integer.parseInt(matchSplit[0]) * 3600 +
                                    Integer.parseInt(matchSplit[1]) * 60 +
                                    Float.parseFloat(matchSplit[2]))) * 1000) / totalDur;
                            float showProgress = (progress * 100);
                            onVideoCompressListener.onProgress((int) showProgress);
                        }
                    }
                }


                @Override
                public void onFailure(String message) {
                    super.onFailure(message);
                    onVideoCompressListener.onFailed();
                    Log.d("video comprs onFailure", message);
                }

                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);
                    onVideoCompressListener.onSuccess(outPutFile);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    onVideoCompressListener.onFinish();
                }
            }); //work fine take(4min 10sec) 70.27MB => 7.19MB
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }

        return ffmpeg;
    }

    public long getVideoDuration(Context context, File videoFile) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(context, Uri.fromFile(videoFile));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        retriever.release();
        return timeInMillisec;
    }
}
