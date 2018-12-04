package ca.uqac.lecitoyen.fragments.userUI;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapters.HorizontalEventTypeAdapter;
import ca.uqac.lecitoyen.adapters.VerticalEventAdapter;
import ca.uqac.lecitoyen.adapters.SearchUserAdapter;
import ca.uqac.lecitoyen.buttons.ToggleButton;
import ca.uqac.lecitoyen.fragments.BaseFragment;
import ca.uqac.lecitoyen.fragments.CreateEventDialogFragment;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Event;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.util.Constants;
import ca.uqac.lecitoyen.views.ToolbarView;

//  TODO: Handle research of (user, post, etc.)


public class SearchFragment extends BaseFragment implements View.OnClickListener {

    private final static String TAG = SearchFragment.class.getSimpleName();

    private final static String ARG_USERAUTH = "user";
    private final static String ARG_USERS = "events";
    private final static String ARG_EVENTS = "events";

    private MainUserActivity activity;
    private iHandleFragment mHandleFragment;
    private DatabaseManager dbManager;

    //  Views
    private ToolbarView mSearchToolbar;
    private FloatingActionButton mAddEventButton;
    private RecyclerView mSearchRecyclerView, mEventByDateRecyclerView, mEventRecyclerView;
    //private RecyclerView.Adapter mNewsfeedAdapter;
    private RecyclerView.Adapter mSearchAdapter, mEventByDateAdapter, mEventAdapter;


    //  Data Structure
    private User mUserAuth;
    private ArrayList<RecyclerView.Adapter> mEventsByDate = new ArrayList<>();
    private ArrayList<Event> mEventsList = new ArrayList<>();
    private ArrayList<User> mUsersList = new ArrayList<>();


    public SearchFragment() {
        // Required empty public constructor
    }

    //TODO: Add Even arrayList
    public static SearchFragment newInstance(User userAuth, ArrayList<User> users, ArrayList<Event> events) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USERAUTH, userAuth);
        args.putParcelableArrayList(ARG_USERS, users);
        args.putParcelableArrayList(ARG_EVENTS, events);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (MainUserActivity) getActivity();
        this.dbManager = DatabaseManager.getInstance();

        long day = 1000 * 3600 * 24;
        //For test

        if (getArguments() != null) {
            mUserAuth = getArguments().getParcelable(ARG_USERAUTH);
            mUsersList = getArguments().getParcelableArrayList(ARG_USERS);
            mEventsList = getArguments().getParcelableArrayList(ARG_EVENTS);
            mSearchAdapter = new SearchUserAdapter(activity, mUsersList);
            //mEventAdapter = new HorizontalEventAdapter(activity, mEventsList);
        } else {
            Log.e(TAG, "Arguments are null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mSearchToolbar = view.findViewById(R.id.search_toolbar);
        mSearchToolbar.searchToolbar(
                activity,
                getResources().getString(R.string.fragment_search),
                dbManager.getStorageUserProfilPicture(mUserAuth.getUid(), mUserAuth.getPid())
        );

        mSearchToolbar.onImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUserAuth != null) {
                    UserProfileFragment fragment = UserProfileFragment.newInstance(Constants.FROM_PROFILE, mUserAuth);
                    activity.doUserProfileTransaction(fragment, MainUserActivity.AUTH_USER);
                }
            }
        });

        view.findViewById(R.id.search_add_event).setOnClickListener(this);


        LinearLayoutManager hllm = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        mEventRecyclerView = view.findViewById(R.id.search_event_type_recycler_view);
        mEventRecyclerView.setLayoutManager(hllm);

        LinearLayoutManager llm = new LinearLayoutManager(activity);
        mEventByDateRecyclerView = view.findViewById(R.id.search_event_by_date_recycler_view);
        mEventByDateRecyclerView.setHasFixedSize(false);
        mEventByDateRecyclerView.setLayoutManager(llm);
        mEventByDateRecyclerView.setNestedScrollingEnabled(false);


        //testAdapter(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.user_menu, menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (MainUserActivity) getActivity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.search_add_event:
                CreateEventDialogFragment createEvent = CreateEventDialogFragment.newInstance(null, mUserAuth);
                createEvent.show(activity.getSupportFragmentManager(), getTag());
                //createDialog.createEventView().show();
               // mHandleFragment.inflateFragment(R.string.fragment_create_event, "");
                break;

        }
    }

    private void updateUI() {

        if(mEventsList != null && !mEventsList.isEmpty()) {

            ArrayList<ToggleButton> toggleButtons = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                ToggleButton button = new ToggleButton(activity);
                button.create(ToggleButton.CIRCLE_BUTTON);
                button.setTitle("Type " + i);
                button.setCircleButtonColor(R.color.primaryColor);
                toggleButtons.add(button);
            }
            RecyclerView.Adapter typeAdapter = new HorizontalEventTypeAdapter(activity, toggleButtons);
            mEventRecyclerView.setAdapter(typeAdapter);


            ArrayList<ArrayList<Event>> mCompleteEventList = new ArrayList<>();
            ArrayList<Event> mBydateEventList = mEventsList;
            mCompleteEventList.add(mBydateEventList);
            mEventByDateAdapter = new VerticalEventAdapter(activity, mCompleteEventList);
            mEventByDateRecyclerView.setAdapter(mEventByDateAdapter);

        }

    }

    private void testAdapter(View view) {

        ArrayList<ToggleButton> toggleButtons = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ToggleButton button = new ToggleButton(activity);
            button.create(ToggleButton.CIRCLE_BUTTON);
            button.setTitle("Type " + i);
            button.setCircleButtonColor(R.color.primaryColor);
            toggleButtons.add(button);
        }
        LinearLayoutManager hllm = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.search_event_type_recycler_view);
        recyclerView.setLayoutManager(hllm);

        RecyclerView.Adapter typeAdapter = new HorizontalEventTypeAdapter(activity, toggleButtons);
        recyclerView.setAdapter(typeAdapter);




        long day = 1000 * 3600 * 24;
        //For test
        ArrayList<ArrayList<Event>> mCompleteEventList = new ArrayList<>();
        ArrayList<Event> mBydateEventList = new ArrayList<>();
        for(int j = 0; j < 5; j++) {


            for (int i = 0; i < 5; i++) {
                Event event = new Event();
                event.setTitle("Event " + i);
                event.setEventDate(System.currentTimeMillis());
                event.setLocation("Place du royaume, Chicoutimi");
                event.setPrice(i * 2);
                mBydateEventList.add(event);
            }
            mCompleteEventList.add(mBydateEventList);
        }

        LinearLayoutManager llm = new LinearLayoutManager(activity);
        mEventByDateRecyclerView = view.findViewById(R.id.search_event_by_date_recycler_view);
        mEventByDateRecyclerView.setHasFixedSize(false);
        mEventByDateRecyclerView.setLayoutManager(llm);
        mEventByDateRecyclerView.setNestedScrollingEnabled(false);

        mEventByDateAdapter = new VerticalEventAdapter(activity, mCompleteEventList);
        mEventByDateRecyclerView.setAdapter(mEventByDateAdapter);


        //mEventRecyclerView = view.findViewById(R.id.search_event_recycler_view);
        //snapHelper.attachToRecyclerView(mEventRecyclerView);
        //mEventRecyclerView.setNestedScrollingEnabled(false);
        //mEventRecyclerView.setLayoutManager(llm);


    }

    public interface OnSearchFragmentInteractionListener {

        void onFragmentInteraction(String something);

    }
}
