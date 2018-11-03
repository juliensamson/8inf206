package ca.uqac.lecitoyen.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ca.uqac.lecitoyen.R;

public class AudioPlayer extends FrameLayout{

    private static final String TAG = "AudioPlayer";

    //private static final float DISABLE = 0.24f;
    //private static final float ENABLE = 1f;

    private static final int AUDIO_PROGRESS_UPDATE_TIME = 100;

    private static final String ERROR_VIEW_NOT_CREATED = "Make sure you use create first";
    private static final String ERROR_PLAYVIEW_NULL = "Play view cannot be null";
    private static final String ERROR_PLAYTIME_CURRENT_NEGATIVE = "Current playback time cannot be negative";
    private static final String ERROR_PLAYTIME_TOTAL_NEGATIVE = "Total playback time cannot be negative";

    private Context mContext;

    private View mRootView;

    private static Uri mUri;

    private Handler mProgressUpdateHandler;

    private MediaPlayer mMediaPlayer;

    private SeekBar mSeekBar;

    private TextView mAudioTitle;
    private TextView mAudioCreator;
    private TextView mRunTime;
    private TextView mTotalTime;

    private FrameLayout mPlayView;
    private ImageView mPlayButton;
    private FrameLayout mPauseView;
    private ImageView mPauseButton;

    private ImageView mAlbumImage;
    private ImageView mBackgroundImage;

    private boolean isCreated = false;
    private boolean isLoaded  = false;

    public AudioPlayer(@NonNull Context context) {
        super(context);
    }

    public AudioPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioPlayer create(Context context) {
        Log.d(TAG, "create");
        View playerUi = inflate(context, R.layout.layout_audio_player, this);

        //  Init image
        mAlbumImage = playerUi.findViewById(R.id.audioplayer_album_image);
        mBackgroundImage = playerUi.findViewById(R.id.audioplayer_album_image_background);

        //  Init textview
        mAudioTitle = playerUi.findViewById(R.id.audioplayer_title);
        mAudioCreator = playerUi.findViewById(R.id.audioplayer_creator);

        //  Init seekbar & time
        mSeekBar = playerUi.findViewById(R.id.audioplayer_seekbar_time);
        mRunTime = playerUi.findViewById(R.id.audioplayer_run_time);
        mTotalTime = playerUi.findViewById(R.id.audioplayer_total_time);
        //setAudioSeekbar();

        //  Init button
        mPlayView = playerUi.findViewById(R.id.audioplayer_play_view);
        mPlayButton = playerUi.findViewById(R.id.audioplayer_play_button);
        //setPlayOnClick();

        mPauseView = playerUi.findViewById(R.id.audioplayer_pause_view);
        mPauseButton = playerUi.findViewById(R.id.audioplayer_pause_button);
        //setPauseOnClick();

        //setPlayDisable();

        setViewsVisibility();

        this.mContext = context;
        this.isCreated = true;
        return this;
    }

    public AudioPlayer load(StorageReference st, String audioId) {
        Log.d(TAG, "load without picture");
        if (!isCreated)
            throw new IllegalArgumentException(ERROR_VIEW_NOT_CREATED);

        // LOAD ICON
        st.child(audioId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null)
                    Log.e(TAG, "Uri is null");
                Log.e(TAG, uri.getPath());
                //setPlayable();
                isLoaded = true;
                initMediaPlayer(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });

        return this;
    }

    public AudioPlayer load(StorageReference st, String imageId, String audioId) {
        Log.d(TAG, "load with image");
        if (!isCreated)
            throw new IllegalArgumentException(ERROR_VIEW_NOT_CREATED);

        //  Load image if there is image
        Glide.with(mContext).load(st.child(imageId)).into(mAlbumImage);

        // Load audio file
        st.child(audioId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null)
                    Log.e(TAG, "Uri is null");
                Log.e(TAG, uri.getPath());
                setPlayable();
                isLoaded = true;
                initMediaPlayer(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
        return this;
    }

    private AudioPlayer initMediaPlayer(Uri uri) {
        Log.d(TAG, "initPayer");
        if(uri == null)
            throw new IllegalArgumentException("Uri cannot be null. Call load() before");

        if(!isLoaded)
            throw new IllegalArgumentException("Uri cannot be null. Call load() before");

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(mContext, uri);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public void play() {
        if (mPlayButton == null) {
            throw new IllegalStateException(ERROR_PLAYVIEW_NULL);
        }

        if (mUri == null) {
            throw new IllegalStateException("Uri cannot be null. Call init() before calling this method");
        }

        if (mMediaPlayer == null) {
            throw new IllegalStateException("Call init() before calling this method");
        }

        if (mMediaPlayer.isPlaying()) {
            return;
        }

        //mProgressUpdateHandler.postDelayed(mUpdateProgress, AUDIO_PROGRESS_UPDATE_TIME);

        // enable visibility of all UI controls.
        setViewsVisibility();

        mMediaPlayer.start();

        setPausable();
    }

    public void pause() {

        if (mMediaPlayer == null) {
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            setPlayable();
        }
    }

    private void setAudioSeekbar() {

        if(mSeekBar == null) {
            return;
        }

        //  Set lenght of seekbar
        long finalTime = mMediaPlayer.getDuration();
        mSeekBar.setMax((int) finalTime);

        //  Init seekbar
        mSeekBar.setProgress(0);

        //  Set user sliding seekbar
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(seekBar.getProgress());

                // if the audio is paused and seekbar is moved,
                // update the play time in the UI.
                updateRuntime(seekBar.getProgress());
            }
        });

    }

    private void setPlayOnClick() {

        if(mPlayButton == null) {
            throw new NullPointerException(ERROR_PLAYVIEW_NULL);
        }

        mPlayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setPausable();
            }
        });

    }

    private void setPauseOnClick() {

        if(mPlayButton == null) {
            throw new NullPointerException(ERROR_PLAYVIEW_NULL);
        }

        mPlayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayable();
            }
        });

    }

    private void updateRuntime(int currentTime) {

        if (mRunTime == null) {
            // this view can be null if the user
            // does not want to use it. Don't throw
            // an exception.
            return;
        }

        if (currentTime < 0) {
            throw new IllegalArgumentException(ERROR_PLAYTIME_CURRENT_NEGATIVE);
        }

        StringBuilder playbackStr = new StringBuilder();

        // set the current time
        // its ok to show 00:00 in the UI
        playbackStr.append(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) currentTime), TimeUnit.MILLISECONDS.toSeconds((long) currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) currentTime))));

        mRunTime.setText(playbackStr);

        // DebugLog.i(currentTime + " / " + totalDuration);
    }




    /**
     * Ensure the views are visible before playing the audio.
     */


    private void setViewsVisibility() {

        if (mAlbumImage != null) {
            mAlbumImage.setVisibility(VISIBLE);
        }

        if (mBackgroundImage != null) {
            mBackgroundImage.setVisibility(VISIBLE);
        }

        if (mAudioTitle != null) {
            mAudioTitle.setVisibility(VISIBLE);
        }

        if (mAudioCreator != null) {
            mAudioCreator.setVisibility(VISIBLE);
        }

        if (mSeekBar != null) {
            mSeekBar.setVisibility(VISIBLE);
        }

        //if (mPlaybackTime != null) {
        //    mPlaybackTime.setVisibility(VISIBLE);
        //}

        if (mRunTime != null) {
            mRunTime.setVisibility(VISIBLE);
        }

        if (mTotalTime != null) {
            mTotalTime.setVisibility(VISIBLE);
        }

        if (mPlayView != null) {
            mPlayView.setVisibility(VISIBLE);
        }

        if (mPlayButton != null) {
            mPlayButton.setVisibility(VISIBLE);
        }

        if (mPauseView != null) {
            mPauseView.setVisibility(VISIBLE);
        }

        if (mPauseButton != null) {
            mPauseButton.setVisibility(VISIBLE);
        }
    }

    private void setPlayDisable() {
        if (mPlayButton != null) {
            mPlayButton.setVisibility(View.VISIBLE);
            //mPlayButton.setAlpha(DISABLE);
        }

        if (mPauseButton != null) {
            mPauseButton.setVisibility(View.GONE);
        }
    }


    /***
     * Changes audiowife state to enable play functionality.
     */
    private void setPlayable() {
        if (mPlayButton != null) {
            mPlayButton.setVisibility(View.VISIBLE);
            //mPlayButton.setAlpha(ENABLE);
        }

        if (mPauseButton != null) {
            mPauseButton.setVisibility(View.GONE);
        }
    }

    /****
     * Changes audio wife to enable pause functionality.
     */
    private void setPausable() {
        if (mPlayButton != null) {
            mPlayButton.setVisibility(View.GONE);
        }

        if (mPauseButton != null) {
            mPauseButton.setVisibility(View.VISIBLE);
            //mPauseButton.setAlpha(ENABLE);
        }
    }
}
