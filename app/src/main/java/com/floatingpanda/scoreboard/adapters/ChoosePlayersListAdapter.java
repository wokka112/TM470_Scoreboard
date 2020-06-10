package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.ChoosePlayerInterface;

import java.util.ArrayList;
import java.util.List;

public class ChoosePlayersListAdapter extends RecyclerView.Adapter<ChoosePlayersListAdapter.ChoosePlayersDialogViewHolder> {
    private final LayoutInflater inflater;
    private List<Member> teamAndPotentialPlayers;
    private List<Member> potentialPlayers;
    private List<Member> teamPlayers;
    private ChoosePlayerInterface listener;

    public ChoosePlayersListAdapter(Context context, ChoosePlayerInterface listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public ChoosePlayersListAdapter.ChoosePlayersDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_dialog_item, parent, false);
        return new ChoosePlayersListAdapter.ChoosePlayersDialogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChoosePlayersListAdapter.ChoosePlayersDialogViewHolder holder, int position) {
        if (teamAndPotentialPlayers != null) {
            Member current = teamAndPotentialPlayers.get(position);
            holder.nicknameItemView.setText(current.getNickname());
            holder.groupsItemView.setText("7");

            if(position < teamPlayers.size()) {
                holder.checkBoxItemView.setChecked(true);
            }

            /*
            holder.checkBoxItemView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        listener.addPlayerToTeam(current);
                    } else {
                        listener.removePlayerFromTeam(current);
                    }
                }
            });

             */

            holder.checkBoxItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.checkBoxItemView.isChecked()) {
                        listener.addPlayerToTeam(current);
                    } else {
                        listener.removePlayerFromTeam(current);
                    }
                }
            });
        } else {
            holder.nicknameItemView.setText("No nickname");
        }
    }

    public void setTeamAndPotentialPlayers(List<Member> teamPlayers, List<Member> potentialPlayers) {
        this.teamPlayers = teamPlayers;
        this.potentialPlayers = potentialPlayers;

        teamAndPotentialPlayers = new ArrayList<>();
        teamAndPotentialPlayers.addAll(this.teamPlayers);
        teamAndPotentialPlayers.addAll(this.potentialPlayers);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (teamAndPotentialPlayers != null)
            return teamAndPotentialPlayers.size();
        else return 0;
    }

    class ChoosePlayersDialogViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxItemView;
        TextView nicknameItemView, groupsItemView;
        ImageView imageItemView;

        private ChoosePlayersDialogViewHolder(View itemView) {
            super(itemView);

            checkBoxItemView = itemView.findViewById(R.id.checkbox);
            nicknameItemView = itemView.findViewById(R.id.rmember_name_output);
            groupsItemView = itemView.findViewById(R.id.rmember_groups_output);
            imageItemView = itemView.findViewById(R.id.rmember_image);
        }
    }
}
