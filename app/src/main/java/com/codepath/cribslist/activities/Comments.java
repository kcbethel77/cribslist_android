package com.codepath.cribslist.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.codepath.cribslist.R;
import com.codepath.cribslist.adapters.CommentsAdapter;
import com.codepath.cribslist.helper.SharedPref;
import com.codepath.cribslist.models.Comment;
import com.codepath.cribslist.network.CribslistClient;

import java.util.ArrayList;

public class Comments extends AppCompatActivity implements CribslistClient.GetComments {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    LinearLayoutManager layoutManager;
    ArrayList<Comment> comments;
    EditText commentBox;
    String threadId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = findViewById(R.id.toolbar);
        commentBox = findViewById(R.id.editText);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getIntent().getStringExtra("title"));
        }

        mRecyclerView = findViewById(R.id.comments);
        layoutManager = new LinearLayoutManager(Comments.this);
        comments = new ArrayList<>();
        mAdapter = new CommentsAdapter(comments);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        threadId = getIntent().getStringExtra("thread_id");
        CribslistClient.getCommentsForId(threadId, this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void handleGetComments(ArrayList<Comment> c) {
        comments.addAll(c);
        mAdapter.notifyDataSetChanged();
    }

    public void submitComment(View view) {
        String c = commentBox.getText().toString();
        if(!"".equals(c.trim())){
            SharedPref sp = SharedPref.getInstance();
            Comment newComment = new Comment(sp.getEmail(), c);
            long uid = Long.parseLong(sp.getUserId());
            newComment.setUser_id(uid);
            commentBox.setText("");
            comments.add(newComment);

            mAdapter.notifyItemInserted(comments.size() - 1);
            CribslistClient.addComment(newComment, threadId);
        }

    }
}
