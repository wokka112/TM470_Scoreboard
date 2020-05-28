package com.floatingpanda.scoreboard.views.fragments;

public class AddGroupMemberDialogFragment /*extends DialogFragment*/ {

    /*
    private List<Member> memberList;

    public AddGroupMemberDialogFragment(List<Member> memberList) {
        this.memberList = memberList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_search_list, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        View holder = layoutInflater.inflate(R.layout.dialog_search_list, null);

        TextView buttonTextView = holder.findViewById(R.id.dialog_button_textview);
        ImageButton addMember = holder.findViewById(R.id.dialog_add_button);

        RecyclerView recyclerView = holder.findViewById(R.id.dialog_recycler_view);

        final AddGroupMemberDialogListAdapter adapter = new AddGroupMemberDialogListAdapter(getActivity());
        printMemberList();
        adapter.setMembers(memberList);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        buttonTextView.setText("Create new member");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(holder)
                .setPositiveButton("Add Group Members", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().cancel();
                    }
                });

        return builder.create();
    }

    private void printMemberList() {
        Log.w("AddGMDFrag.java", "Member list size: " + memberList.size());

        for (Member member : memberList) {
            Log.w("AddGMDFrag.java", "Member: " + member);
        }
    }

     */
}
