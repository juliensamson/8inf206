package ca.uqac.lecitoyen.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.dialogs.SelectImageTypeDialog;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Event;
import ca.uqac.lecitoyen.models.Image;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.util.Constants;
import ca.uqac.lecitoyen.util.ImageHandler;
import ca.uqac.lecitoyen.util.Util;
import ca.uqac.lecitoyen.views.MultimediaView;
import ca.uqac.lecitoyen.views.ToolbarView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**TODO:
 *   - OnClick
 *   - Extract information from edit text field
 *   - Update DB firebase et Storage for the image
 */

public class CreateEventDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = CreateEventDialogFragment.class.getSimpleName();
    private static final String ARG_EVENT = "event";
    private static final String ARG_USER  = "user-auth";

    private Calendar mCalendar;
    private Activity mActivity;
    private DatabaseManager dbManager;
    private ImageHandler mImageHandler;

    private Event mEventSelect;
    private User mUserAuth;

    private Uri mImageUri;
    private Bitmap mBitmapImage;

    private ToolbarView mToolbar;
    private EditText mEventTitleField;
    private EditText mAddEventImage;
    private MultimediaView mEvenImageView;
    private EditText mAddEventDateBegin, mAddEventDateEnd;
    private EditText mEventLocationField;
    private EditText mAddEventType;
    private EditText mEventPriceField;
    private EditText mEventDescriptionField;

    public CreateEventDialogFragment() {
    }

    public static CreateEventDialogFragment newInstance(Event event, User user) {
        CreateEventDialogFragment createEventDialogFragment = new CreateEventDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EVENT, event);
        args.putParcelable(ARG_USER, user);
        createEventDialogFragment.setArguments(args);
        return createEventDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mCalendar = Calendar.getInstance();
        this.mActivity = getActivity();
        this.dbManager = DatabaseManager.getInstance();
        this.mImageHandler = ImageHandler.getInstance();

        if(getArguments() != null) {
            mEventSelect = getArguments().getParcelable(ARG_EVENT);
            mUserAuth = getArguments().getParcelable(ARG_USER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_bottom_create_event, container, false);
        mToolbar = rootView.findViewById(R.id.create_event_toolbar);
        mEventTitleField = rootView.findViewById(R.id.create_event_title_field);
        mAddEventImage = rootView.findViewById(R.id.create_event_add_image);
        mEvenImageView = rootView.findViewById(R.id.create_event_image_view);
        mAddEventDateBegin = rootView.findViewById(R.id.create_event_add_date_begin);
        mAddEventDateEnd = rootView.findViewById(R.id.create_event_add_date_end);
        mEventLocationField =  rootView.findViewById(R.id.create_event_location_field);
        mAddEventType = rootView.findViewById(R.id.create_event_add_type);
        mEventPriceField = rootView.findViewById(R.id.create_event_price_field);
        mEventDescriptionField = rootView.findViewById(R.id.create_event_description_field);

        mToolbar.onButtonClickListener(this);
        rootView.findViewById(R.id.create_event_add_image).setOnClickListener(this);
        rootView.findViewById(R.id.create_event_add_date_begin).setOnClickListener(this);
        rootView.findViewById(R.id.create_event_add_date_end).setOnClickListener(this);
        rootView.findViewById(R.id.create_event_add_type).setOnClickListener(this);
        rootView.findViewById(R.id.create_event_price_field).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_view_button:
                updateDB();
                break;
            case R.id.create_event_add_image:
                showImagePicker();
                break;
            case R.id.create_event_add_date_begin:
                Log.d(TAG, "date beign click");
                showDatePicker();
                break;
            //case R.id.create_event_add_date_end:
            //    Log.d(TAG, "date end click");
            //    showDatePicker();
            //    break;
            case R.id.create_event_add_type:
                Log.d(TAG, "even type click");
                showEventType();
                break;
            case R.id.create_event_price_field:
                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode + " " + resultCode);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case Constants.REQUEST_GALLERY_CODE:
                    setImageUri(data.getData());
                    break;
                case Constants.REQUEST_CAMERA_CODE:    //TODO: Make this work somehow
                    setImageBitmap((Bitmap) data.getExtras().get("data"));
                    break;
                default:
                    break;
            }

        } else if (requestCode == RESULT_CANCELED) {
            Log.e(TAG, "Some error occured");
        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_WEEK, dayOfMonth);
            showTimePicker();
        }

    };


    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hour);
            mCalendar.set(Calendar.MINUTE, minute);
            updateLabel();
        }

    };



    /**
     *
     *      Update
     *
     */

    private void updateUI() {

        if(mEventSelect == null) {
            mToolbar.buttonToolbar(mActivity, "Publier");
        } else {
            mToolbar.buttonToolbar(mActivity, "Modifier");
        }


    }

    //TODO: Add date begin, end
    private void updateDB() {
        Log.d(TAG, "updateDB");
        if(!validateForm())
            return;

        Event event = new Event();
        event.setEid(dbManager.getDatabaseEvents().push().getKey());
        event.setTitle(mEventTitleField.getText().toString());
        event.setEventDate(mCalendar.getTimeInMillis());
        event.setLocation(mEventLocationField.getText().toString());
        event.setEventType(mAddEventType.getText().toString());
        if(!mEventPriceField.getText().toString().isEmpty())
            event.setPrice(Integer.valueOf(mEventPriceField.getText().toString()));
        else
            event.setPrice(0);
        updateImage(event);

        dbManager.writeEventToFirebase(event);
        dismiss();
        //photo


    }

    private void updateImage(Event event) {

        if(mEvenImageView.getVisibility() != View.GONE) {

            Image eventImage = new Image();
            eventImage.setImageid("image" + event.getEid() + Util.getRandomNumber());
            eventImage.setName("");

            event.setPid(eventImage.getImageid());

            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            mBitmapImage.compress(Bitmap.CompressFormat.JPEG, 20, boas);
            byte[] data =boas.toByteArray();

            StorageReference stEvent = dbManager.getStorageEvent(event.getEid());

            try {

                if(event.getPid() == null)
                    throw new NullPointerException("No image was added");

                UploadTask uploadTask = stEvent.child(event.getPid()).putBytes(data);
                uploadTask
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d(TAG, "Byte transferred: " + taskSnapshot.getBytesTransferred());

                                //TODO: add byte transfer to ProgressDialog
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                dismiss();
                                Log.d(TAG, "image uploaded");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                });

            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage());
            }



        }

    }


    /**
     *
     *      Pickers (date, type, etc)
     *
     */

    private void showImagePicker() {
        final SelectImageTypeDialog dialog = new SelectImageTypeDialog(mActivity);
        dialog.create().OnCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = mImageHandler.openCamera(mActivity);
                startActivityForResult(intent, Constants.REQUEST_CAMERA_CODE);
                dialog.dismiss();
            }
        }).OnGalleryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = mImageHandler.openGallery(mActivity);
                startActivityForResult(intent, Constants.REQUEST_GALLERY_CODE);
                dialog.dismiss();
            }
        }).show();
    }

    private void showDatePicker() {
        new DatePickerDialog(mActivity, date, mCalendar
                .get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(mActivity, time,
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE),
                true)
                .show();
    }

    private void showEventType() {
        final ArrayList<String> eventTypeList = new ArrayList<String>();
        eventTypeList.add(getString(R.string.event_type_music));
        eventTypeList.add(getString(R.string.event_type_art));
        eventTypeList.add(getString(R.string.event_type_photography));
        eventTypeList.add(getString(R.string.event_type_other));


        final BottomSheetDialog eventType =  new BottomSheetDialog(mActivity);
        View rootView = View.inflate(mActivity, R.layout.dialog_bottom_expand_type_list, null);
        ListView listView = rootView.findViewById(R.id.dialog_bottom_type_list);
        listView.setAdapter(new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, eventTypeList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAddEventType.setText(eventTypeList.get(i));
                eventType.dismiss();
            }
        });
        eventType.setContentView(rootView);
        eventType.show();

    }

    private void updateLabel() {
        String myFormat = "dd MMM yyyy HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA_FRENCH);

        mAddEventDateBegin.setText(sdf.format(mCalendar.getTime()));
    }


    private boolean validateForm() {

        boolean valid = true;

        String event = mEventTitleField.getText().toString();
        if (TextUtils.isEmpty(event)) {
            Toast.makeText(mActivity, "L'événement n'a pas de titre", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            mEventTitleField.setError(null);
        }

        String eventdate = mAddEventDateBegin.getText().toString();
        if (TextUtils.isEmpty(eventdate)) {
            Toast.makeText(mActivity, "L'événement na pas de date", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            mAddEventDateBegin.setError(null);
        }

        String location = mEventLocationField.getText().toString();
        if (TextUtils.isEmpty(location)) {
            Toast.makeText(mActivity, "L'événement na pas d'endroit", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            mEventTitleField.setError(null);
        }


        return valid;
    }

    private void setImageUri(Uri uri) {

        mImageUri = uri;

        try {

            mBitmapImage = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), mImageUri);

            mEvenImageView.with(mActivity)
                    .setEditable(true)
                    .setFrameSize()
                    .loadImages(mBitmapImage, "");

        } catch (IOException e) {
            Log.e(TAG, "3" + e.getMessage());
        }
    }

    private void setImageBitmap(Bitmap bitmap) {

        try {

            mBitmapImage = bitmap;
            mEvenImageView.setEditable(true).loadImages(mBitmapImage, "");

        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

    }

}
