package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.data.PlayerTeam;
import com.floatingpanda.scoreboard.interfaces.SelectedMemberInterface;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.lang.reflect.Array;
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

            holder.positionSpinner.setAdapter(getAdapter(members.size()));
        }

        /*
        if (players != null) {
            Player current = players.get(position);
            holder.nicknameItemView.setText(current.getNickname());
            holder.groupsItemView.setText("7");

            holder.checkBoxItemView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        listener.addSelectedMember(current);
                    } else {
                        listener.removeSelectedMember(current);
                    }
                }
            });
        } else {
            holder.nicknameItemView.setText("No nickname");
        }

         */
    }

    private ArrayAdapter<Integer> getAdapter(int numberOfTeams) {
        List<Integer> positionList = new ArrayList<Integer>();

        for (int i = 1; i <= numberOfTeams; i++) {
            positionList.add(i);
        }

        ArrayAdapter<Integer> positions = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, positionList);
        return positions;
    }

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
