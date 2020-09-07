package com.example.natarioapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.size.AspectRatio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class VideoPreviewActivity extends AppCompatActivity {

    private Uri videouri;
    UploadTask uploadTask;
    Member member;
    private static VideoResult videoResult;

    StorageReference storageReference;
    DatabaseReference databaseReference;

    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    public static void setVideoResult(@Nullable VideoResult result) {
        videoResult = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        playerView = findViewById(R.id.video_view);

        final VideoResult result = videoResult;
        if (result == null) {
            finish();
            return;
        }
        videouri = Uri.fromFile(result.getFile());
        LoadControl loadControl = new DefaultLoadControl();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(VideoPreviewActivity.this, trackSelector, loadControl);
        MediaSource audioSource = new ExtractorMediaSource(
                videouri,
                new DefaultDataSourceFactory(this, "MyExoplayer"),
                new DefaultExtractorsFactory(),
                null,
                null
        );

        simpleExoPlayer.prepare(audioSource);
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {

            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            setVideoResult(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.getPlaybackState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            Toast.makeText(this, "Sharing...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("video/*");
            Uri uri = FileProvider.getUriForFile(this,
                    this.getPackageName() + ".provider",
                    videoResult.getFile());
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.save) {
            Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();

            String folder_main = "Spoken_like_a_pro";
            File f = new File(Environment.getExternalStorageDirectory(), folder_main);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String prepend = "VIDEO_" + timestamp + "_";
            File videoFile = null;
            try {
                videoFile = File.createTempFile(prepend, ".mp4", f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                copyFile(videoResult.getFile(), videoFile);
                Toast.makeText(getApplicationContext(), "VIDEO SAVED", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (item.getItemId() == R.id.upload){
            storageReference = FirebaseStorage.getInstance().getReference("Video");
            databaseReference = FirebaseDatabase.getInstance().getReference("Video");

            final StorageReference reference = storageReference.child(System.currentTimeMillis() + ".mp4");
            uploadTask = reference.putFile(videouri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        Toast.makeText(VideoPreviewActivity.this, "data saved", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = user.getUid();
                        member.setUname(userId);
                        member.setVideoUrl(downloadUrl.toString());
                        String i = databaseReference.push().getKey();
                        databaseReference.child(i).setValue(member);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(VideoPreviewActivity.this, "Failed" + e, Toast.LENGTH_SHORT).show();
                }
            });

            return true;
        }
        if (item.getItemId() == R.id.view){
            Intent intent = new Intent(VideoPreviewActivity.this, FullScreen.class);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();
            intent.putExtra("name",userId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        String folder_main = "Spoken_like_a_pro";

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();

        }
    }
    public static void copyFile(File src, File dst) throws IOException{
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        Log.d("tag", "Input stream: " + src.toString() + " " + "Output Stream: " + dst.toString() );
        try
        {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally
        {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }
    private String getExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}