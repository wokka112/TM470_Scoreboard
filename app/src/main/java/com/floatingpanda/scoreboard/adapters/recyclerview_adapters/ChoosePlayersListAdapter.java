package com.floatingpanda.scoreboard.adapters.recyclerview_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.SelectedMemberInterface;

import java.util.ArrayList;
import java.util.List;

//TODO remove and replace with addgroupmemberslistadapter? Or vice versa??
/**
 * Recyclerview adapter for displaying a list of players to choose from for a team. Members are
 * displayed with checkboxes next to them for selecting them, which in turn triggers methods in the
 * object implementing SelectedMemberInterface which is stored as "listener". When a box is checked,
 * addSelectedMember() is called, and when it is unchecked, removeSelectedMember() is called.
 *
 * This is similar to the MemberListAdapter except for the addition of checkboxes and the listener.
 */
public class ChoosePlayersListAdapter extends RecyclerView.Adapter<ChoosePlayersListAdapter.ChoosePlayersDialogViewHolder> {
    private final LayoutInflater inflater;
    private List<Member> teamAndPotentialPlayers;
    private List<Member> teamPlayers;
    private SelectedMemberInterface listener;

    public ChoosePlayersListAdapter(Context context, SelectedMemberInterface listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public ChoosePlayersListAdapter.ChoosePlayersDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_checkbox_member_item, parent, false);
        return new ChoosePlayersListAdapter.ChoosePlayersDialogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChoosePlayersListAdapter.ChoosePlayersDialogViewHolder holder, int position) {
        if (teamAndPotentialPlayers != null) {
            Member current = teamAndPotentialPlayers.get(position);
            holder.nicknameItemView.setText(current.getNickname());

            if(position < teamPlayers.size()) {
                holder.checkBoxItemView.setChecked(true);
            } else {
                //Without this else clause a bug occurs when selecting boxes and hitting the back button - multiple boxes
                // become checked even though some of them aren't team members.
                holder.checkBoxItemView.setChecked(false);
            }

            //Can't use on checked listener because it activates when checking the boxes in the above if condition.
            // This causes major problems, such as exponentially adding members to a team when you return to a page
            // with checked members.
            holder.checkBoxItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.checkBoxItemView.isChecked()) {
                        listener.addSelectedMember(current);
                    } else {
                        listener.removeSelectedMember(current);
                    }
                }
            });
        } else {
            holder.nicknameItemView.setText("No Players");
        }
    }

    //TODO change teamandpotentialplayers to all players and remove potential players.
    /**
     * Sets the list of teamplayers for this team as well as the list of potential players that can
     * be added to the team (i.e. do not belong to this team or any other team yet).
     *
     * Must be called before adapter will display anything.
     * @param teamPlayers list of members that belong to this team
     * @param potentialPlayers list of members that do not belong to this team or any other team
     */
    public void setTeamAndPotentialPlayers(List<Member> teamPlayers, List<Member> potentialPlayers) {
        this.teamPlayers = teamPlayers;

        teamAndPotentialPlayers = new ArrayList<>();
        teamAndPotentialPlayers.addAll(this.teamPlayers);
        teamAndPotentialPlayers.addAll(potentialPlayers);

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
        TextView nicknameItemView;
        ImageView imageItemView;

        private ChoosePlayersDialogViewHolder(View itemView) {
            super(itemView);

            checkBoxItemView = itemView.findViewById(R.id.checkbox);
            nicknameItemView = itemView.findViewById(R.id.rmember_name_output);
            imageItemView = itemView.findViewById(R.id.rmember_image);
        }
    }
}
