package com.example.communicare_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communicare_mobile.model.TileModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class RecyclerViewFragment extends Fragment implements OnAdapterItemClickListener {

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 10;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RadioButton mLinearLayoutRadioButton;
    protected RadioButton mGridLayoutRadioButton;

    protected RecyclerView mRecyclerView;
    protected TileViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<TileModel> mDataset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        initDataset("viewObjects.json", "elements", "home", "english");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tile_view_fragment, container, false);
        rootView.setTag(TAG);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);


        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        // TODO: Init layout based on json mapping or tile count
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new TileViewAdapter(mDataset, this);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)

        mLinearLayoutRadioButton = (RadioButton) rootView.findViewById(R.id.linear_layout_rb);
        mLinearLayoutRadioButton.setOnClickListener(v -> setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER));

        mGridLayoutRadioButton = (RadioButton) rootView.findViewById(R.id.grid_layout_rb);
        mGridLayoutRadioButton.setOnClickListener(v -> setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER));

        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset(String fileName, String objectKey, String category, String language) {

        // TODO: Replace with data from json - home screen
        mDataset = new ArrayList<>();
        try {
            JSONArray jsonDataArray = new JSONObject(LoadJsonFromAsset(fileName)).getJSONArray(objectKey);
            JSONObject jsonTransObj = new JSONObject(LoadJsonFromAsset("translations.json")).getJSONObject("translations");

            for (int i=0; i<jsonDataArray.length(); i++){
                JSONObject itemObj = jsonDataArray.getJSONObject(i);

                long id = itemObj.getLong("id");
                String label = itemObj.getString("label");
                String viewCategory = itemObj.getString("viewCategory");
                String viewRedirect = itemObj.getString("viewRedirect");
                boolean textToSpeech = itemObj.getBoolean("textToSpeech");

                if (viewCategory.equals(category)){
                    String translation = jsonTransObj.getJSONObject(label).getString(language);
                    TileModel tile = new TileModel();
                    tile.setId(id);
                    tile.setLabel(translation);
                    tile.setViewCategory(viewCategory);
                    tile.setViewRedirect(viewRedirect);
                    tile.setTextToSpeech(textToSpeech);
                    mDataset.add(tile);
                }

            }
        }catch (JSONException e){
            Log.d(TAG, "addItemsFromJSON: ", e);
            e.printStackTrace();
        }
    }

    public String  LoadJsonFromAsset(String fileName) {
        String json = null;
        try{
            InputStream in = getActivity().getAssets().open(fileName);
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onAdapterItemClickListener(TileModel tile) {
        // TODO: Implement textToSpeech logic here + adapter reload based on redirect
        Log.d(TAG, "RecyclerView Element " + tile + " clicked.");
    }
}
