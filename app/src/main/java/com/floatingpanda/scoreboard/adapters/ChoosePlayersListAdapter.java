package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.util.Log;
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

    private int teamNo;

    public ChoosePlayersListAdapter(Context context, ChoosePlayerInterface listener, int teamNo) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.teamNo = teamNo;
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
                //Log.w("ChoosePlayersListAdapter.java", "Team " + teamNo + ", checking position " + position + " player: " + current);
                holder.checkBoxItemView.setChecked(true);
            } else {
                //A bug occurs when selecting boxes and hitting the back button - multiple boxes become checked even though some of them aren't
                // team members. This else clause solves that.
                holder.checkBoxItemView.setChecked(false);
            }

            //Can't use on checked listener because it activates when checking the boxes in the above if condition.
            // This causes major problems, such as exponentially adding members to a team when you return to a page
            // with checked members.
            holder.checkBoxItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.checkBoxItemView.isChecked()) {
                        //Log.w("ChoosePlayerListAdapt.java", "Box checked, adding player to team " + teamNo + ": " + current);
                        listener.addPlayerToTeam(current);
                    } else {
                        //Log.w("ChoosePlayerListAdapt.java", "Box unchecked, removing player from team " + teamNo + ": " + current);
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

        /*
        Log.w("ChoosePlayerListAdapt.java", "Team Players");
        for (Member member : teamPlayers) {
            Log.w("ChoosePlayerListAdapt.java", "Team Player: " + member);
        }

        Log.w("ChoosePlayerListAdapt.java", "Potential Players");
        for (Member member : potentialPlayers) {
            Log.w("ChoosePlayerListAdapt.java", "Potential Player: " + member);
        }

         */

        teamAndPotentialPlayers = new ArrayList<>();
        teamAndPotentialPlayers.addAll(this.teamPlayers);
        teamAndPotentialPlayers.addAll(this.potentialPlayers);

        /*
        Log.w("ChoosePlayerListAdapt.java", "Team and Potential Players for team " + teamNo);
        for (int i = 0; i < teamAndPotentialPlayers.size(); i++) {
            if (i < teamPlayers.size()) {
                Log.w("ChoosePlayerListAdapt.java", "Team Player: " + teamAndPotentialPlayers.get(i));
            } else {
                Log.w("ChoosePlayerListAdapt.java", "Potential Player: " + teamAndPotentialPlayers.get(i));
            }
        }

         */
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
