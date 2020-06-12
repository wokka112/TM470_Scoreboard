package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class AddGameRecordPlayersAdapter extends RecyclerView.Adapter<AddGameRecordPlayersAdapter.AddGameRecordPlayerDialogViewHolder> {

    private Context context;
    private final LayoutInflater inflater;
    private List<Member> members;

    public AddGameRecordPlayersAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public AddGameRecordPlayersAdapter.AddGameRecordPlayerDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.activity_add_game_record_player_item, parent, false);
        return new AddGameRecordPlayersAdapter.AddGameRecordPlayerDialogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddGameRecordPlayersAdapter.AddGameRecordPlayerDialogViewHolder holder, int position) {
        if (members != null) {
            Member current = members.get(position);

            holder.positionSpinner.setAdapter(createPositionSpinnerAdapter(members.size()));
            //setupSearchableSpinner(holder);
        }
    }

    private ArrayAdapter<Integer> createPositionSpinnerAdapter(int numberOfTeams) {
        List<Integer> positionList = new ArrayList<Integer>();

        for (int i = 1; i <= numberOfTeams; i++) {
            positionList.add(i);
        }

        ArrayAdapter<Integer> positionsAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, positionList);
        return positionsAdapter;
    }

    /*
    private void setupSearchableSpinner(AddGameRecordPlayersAdapter.AddGameRecordPlayerDialogViewHolder holder) {
        holder.playerSearchableSpinner.setAdapter(new SearchableSpinnerAdapter(context, members));

        holder.playerSearchableSpinner.setPositiveButton("Ok");

        holder.playerSearchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* Maybe could use this???
                if (parent.getItemAtPosition(position) == null) {

                }
                 */
/*
                if (position == 0) {
                    return;
                }

                Toast.makeText(context, "Selected: " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();

                //add member to list of selected members.
                //add player to player team.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    */

    public void setPlayers(List<Member> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (members != null)
            return members.size();
        else return 0;
    }

    class AddGameRecordPlayerDialogViewHolder extends RecyclerView.ViewHolder {
        Spinner positionSpinner;
        SearchableSpinner playerSearchableSpinner;

        private AddGameRecordPlayerDialogViewHolder(View itemView) {
            super(itemView);
            positionSpinner = itemView.findViewById(R.id.player_item_position_spinner);
            playerSearchableSpinner = itemView.findViewById(R.id.player_item_player_spinner);
        }
    }
}
