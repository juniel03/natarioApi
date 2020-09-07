package com.example.natarioapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private final static CameraLogger LOG = CameraLogger.create("DemoApp");
    ImageButton btnRec;
    CameraView camera;
    private final static boolean USE_FRAME_PROCESSOR = true;
    private final static boolean DECODE_BITMAP = false;
    private boolean recording = false;
    private FirebaseAuth fAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        btnRec = (ImageButton) findViewById(R.id.ibRec);

        camera = findViewById(R.id.camera);
        camera.setLifecycleOwner(this);
        camera.setMode(Mode.VIDEO);

        camera.addCameraListener(new Listener());

        if (USE_FRAME_PROCESSOR) {
            camera.addFrameProcessor(new FrameProcessor() {
                private long lastTime = System.currentTimeMillis();

                @Override
                public void process(@NonNull Frame frame) {
                    long newTime = frame.getTime();
                    long delay = newTime - lastTime;
                    lastTime = newTime;
                    LOG.v("Frame delayMillis:", delay, "FPS:", 1000 / delay);
                    if (DECODE_BITMAP) {
                        if (frame.getFormat() == ImageFormat.NV21
                                && frame.getDataClass() == byte[].class) {
                            byte[] data = frame.getData();
                            YuvImage yuvImage = new YuvImage(data,
                                    frame.getFormat(),
                                    frame.getSize().getWidth(),
                                    frame.getSize().getHeight(),
                                    null);
                            ByteArrayOutputStream jpegStream = new ByteArrayOutputStream();
                            yuvImage.compressToJpeg(new Rect(0, 0,
                                    frame.getSize().getWidth(),
                                    frame.getSize().getHeight()), 100, jpegStream);
                            byte[] jpegByteArray = jpegStream.toByteArray();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(jpegByteArray,
                                    0, jpegByteArray.length);
                            //noinspection ResultOfMethodCallIgnored
                            bitmap.toString();
                        }
                    }
                }
            });
        }

        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (camera.getMode() == Mode.PICTURE) {
                    message("Can't record HQ videos while in PICTURE mode.", false);
                    return;
                }
                if (!recording){
                    message("Recording for 5 seconds...", true);
                    camera.takeVideo(new File(getFilesDir(), "video.mp4"), 10000);
                    btnRec.setBackgroundResource(R.drawable.recording);
                    recording = true;
                }else{
                    camera.stopVideo();
                    btnRec.setBackgroundResource(R.drawable.record);
                    recording = false;
                }


            }
        });
    }
    private void message(@NonNull String content, boolean important) {
        if (important) {
            LOG.w(content);
            Toast.makeText(this, content, Toast.LENGTH_LONG).show();
        } else {
            LOG.i(content);
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid && !camera.isOpened()) {
            camera.open();
        }
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    public void changepassword(View view) {
        final EditText resetpassword  = new EditText(view.getContext());
        final AlertDialog.Builder resetpasswordDialog = new AlertDialog.Builder(view.getContext());
        resetpasswordDialog.setTitle("Change Password");
        resetpasswordDialog.setMessage("Enter New password not less than 6 characters");
        resetpasswordDialog.setView(resetpassword);

        resetpasswordDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newpassword  = resetpassword.getText().toString();
                user.updatePassword(newpassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "CHANGED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "CHANGED FAILED " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        resetpasswordDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //close
            }
        });
        resetpasswordDialog.create().show();
    }

    public void view(View view) {
        Intent intent = new Intent(MainActivity.this, Gallery.class);
        startActivity(intent);
    }


    private class Listener extends CameraListener {

        @Override
        public void onPictureTaken(PictureResult result) {
            // A Picture was taken!
        }
        @Override
        public void onVideoRecordingStart() {
            super.onVideoRecordingStart();
            LOG.w("onVideoRecordingStart!");
        }

        @Override
        public void onVideoTaken(VideoResult result) {
            // A Video was taken!
            btnRec.setBackgroundResource(R.drawable.record);
            LOG.w("onVideoTaken called! Launching activity.");
            VideoPreviewActivity.setVideoResult(result);
            Intent intent = new Intent(MainActivity.this, VideoPreviewActivity.class);
            startActivity(intent);
            LOG.w("onVideoTaken called! Launched activity.");
        }
        @Override
        public void onVideoRecordingEnd() {
            super.onVideoRecordingEnd();
            message("Video taken. Processing...", false);
            LOG.w("onVideoRecordingEnd!");
        }
    }
}