package com.happym.mathsquare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.happym.mathsquare.Model.LeaderboardEntry;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<LeaderboardEntry> leaderboardList;

    public LeaderboardAdapter(List<LeaderboardEntry> leaderboardList) {
        this.leaderboardList = leaderboardList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboards, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LeaderboardEntry entry = leaderboardList.get(position);
        LeaderboardViewHolder viewHolder = (LeaderboardViewHolder) holder;

        viewHolder.rank.setText(String.valueOf(position + 1));
        viewHolder.name.setText(entry.getFirstName() + " " + entry.getLastName());
        viewHolder.grade.setText("Grade " + entry.getGrade());   // Set grade
        viewHolder.section.setText(entry.getSection());
        viewHolder.points.setText(String.valueOf(entry.getPoints()));
    }

    @Override
    public int getItemCount() {
        return leaderboardList.size();
    }

    private static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView rank, name, grade, section, points;

        LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.text_rank);
            name = itemView.findViewById(R.id.text_name);
            grade = itemView.findViewById(R.id.text_grade);
            section = itemView.findViewById(R.id.text_section);
            points = itemView.findViewById(R.id.text_points);
        }
    }
}
