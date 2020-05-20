package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BgCategoryRepository;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameRepository;
import com.floatingpanda.scoreboard.data.PlayMode;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.thomashaertel.widget.MultiSpinner;

import java.util.ArrayList;
import java.util.List;

//TODO keep searchable spinner comment stuff for when I make the gamerecord creation. It could be useful for
// finding and adding board games. Same for players.

//TODO set up something to sort the bgcategory list into alphabetic order.

public class BoardGameAddEditViewModel extends AndroidViewModel {

    private BoardGameRepository boardGameRepository;
    private BgCategoryRepository bgCategoryRepository;
    private List<BgCategory> selectedBgCategories;
    private List<BgCategory> allBgCategoriesNotLive;
    private LiveData<List<BgCategory>> allBgCategoriesLiveData;
    //SpinnerAdapter adapter;
    private ArrayAdapter<BgCategory> adapter;

    public BoardGameAddEditViewModel(Application application) {
        super(application);
        boardGameRepository = new BoardGameRepository(application);
        bgCategoryRepository = new BgCategoryRepository(application);
        selectedBgCategories = new ArrayList<BgCategory>();
        allBgCategoriesLiveData = bgCategoryRepository.getAll();
        allBgCategoriesNotLive = new ArrayList<BgCategory>();
    }

    public LiveData<List<BgCategory>> getAllBgCategories() { return allBgCategoriesLiveData; }

    public List<BgCategory> getAllBgCategoriesNotLive() { return allBgCategoriesNotLive; }

    public void setAllBgCategoriesNotLive(List<BgCategory> bgCategories) { this.allBgCategoriesNotLive = bgCategories; }

    public List<BgCategory> getSelectedBgCategories() {
        return selectedBgCategories;
    }

    public void setSelectedBgCategories(List<BgCategory> bgCategories) {
        this.selectedBgCategories = new ArrayList<BgCategory>(bgCategories);
        //TODO remove printBgCategories(), it is for testing purposes.
        printBgCategories();
        //setChipGroupChips(bgCategories);
    }

    public void addSelectedBgCategory(BgCategory bgCategory) {
        this.selectedBgCategories.add(bgCategory);
    }

    public void removeSelectedBgCategory(BgCategory bgCategory) {
        this.selectedBgCategories.remove(bgCategory);
    }

    public void clearSelectedBgCategories() {
        this.selectedBgCategories.clear();
    }

    //TODO remove this method, it is for testing purposes.
    private void printBgCategories() {
        Log.w("BoardGameAddAct.java", "Printing categories:");
        int i = 1;
        for (BgCategory bgCategory : selectedBgCategories) {
            Log.w("BoardGameAddAct.java", "BgCategory " + i + ": " + bgCategory.getCategoryName());
        }
    }

    public List<PlayMode.PlayModeEnum> getPlayModes(boolean competitive, boolean cooperative, boolean solitaire) {
        List<PlayMode.PlayModeEnum> playModes = new ArrayList<>();

        if (competitive) {
            playModes.add(PlayMode.PlayModeEnum.COMPETITIVE);
        }

        if (cooperative) {
            playModes.add(PlayMode.PlayModeEnum.COOPERATIVE);
        }

        if (solitaire) {
            playModes.add(PlayMode.PlayModeEnum.SOLITAIRE);
        }

        return playModes;
    }

    public BoardGame.TeamOption getTeamOption(int checkboxId) {
        switch(checkboxId) {
            case R.id.bgadd_no_teams_radiobutton:
                return BoardGame.TeamOption.NO_TEAMS;
            case R.id.bgadd_teams_allowed_radiobutton:
                return BoardGame.TeamOption.TEAMS_OR_SOLOS;
            case R.id.bgadd_teams_only_radiobutton:
                return BoardGame.TeamOption.TEAMS_OR_SOLOS;
            default:
                return BoardGame.TeamOption.ERROR;
        }
    }

    public Chip createChip(MultiSpinner multiSpinner, ChipGroup chipGroup, BgCategory bgCategory) {
        Chip chip = new Chip(chipGroup.getContext());
        chip.setText((bgCategory.getCategoryName()));
        chip.setCloseIconVisible(true);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedBgCategory(bgCategory);
                chipGroup.removeView(chip);

                //TODO look into making this better? Feels very clunky and long.
                int position = adapter.getPosition(bgCategory);
                boolean[] selected = multiSpinner.getSelected();
                selected[position] = false;

                multiSpinner.setSelected(selected);
            }
        });

        addSelectedBgCategory(bgCategory);

        return chip;
    }

    public boolean[] getSelected(List<BgCategory> bgCategories) {
        boolean[] selected = new boolean[adapter.getCount()];

        for (int i = 0; i < bgCategories.size(); i++) {
            int position = adapter.getPosition(bgCategories.get(i));
            selected[position] = true;
        }

        return selected;
    }

    private void setupAdapter(Context context) {
        //TODO select the selected ones to begin with. May need to do in the view...
        List<BgCategory> bgCategories = new ArrayList<BgCategory>(getAllBgCategoriesNotLive());

        adapter = new ArrayAdapter<BgCategory>(context, android.R.layout.simple_spinner_item, bgCategories);
    }

    public ArrayAdapter<BgCategory> getAdapter(Context context) {
        if (adapter == null) {
            setupAdapter(context);
        }

        return adapter;
    }

    public BgCategory getAdapterItem(int position) {
        return adapter.getItem(position);
    }

    //Stuff for the searchable spinner list. This could be useful for when I'm making the board game
    // selection element in the game record creation/editing activity.

    /*
    private void setupAdapter(Context context) {
        List<BgCategory> bgCategories = new ArrayList<BgCategory>(getAllBgCategoriesNotLive());
        bgCategories.removeAll(getSelectedBgCategories());

        adapter = new SpinnerAdapter(context, bgCategories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public SpinnerAdapter getSpinnerListAdapter(Context context) {
        if (adapter == null) {
            setupAdapter(context);
        }

        return adapter;
    }
     */

    /*
    private void setupAdapter(Context context) {
        List<BgCategory> bgCategories = new ArrayList<BgCategory>(getAllBgCategoriesNotLive());
        bgCategories.removeAll(getSelectedBgCategories());

        adapter = new ArrayAdapter<BgCategory> (
                context, android.R.layout.simple_spinner_item, bgCategories
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public ArrayAdapter<BgCategory> getSpinnerListAdapter(Context context) {
        if (adapter == null) {
            setupAdapter(context);
        }

        return adapter;
    }

     */
}
