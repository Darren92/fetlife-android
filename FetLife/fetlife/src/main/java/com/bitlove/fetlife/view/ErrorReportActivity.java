package com.bitlove.fetlife.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bitlove.fetlife.R;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorReportActivity extends AppCompatActivity {

    private static final String EXTRA_ERROR_THROWABLE = "com.bitlove.fetlife.extra.error_throwable";

    public static void startActivity(Context context, Throwable throwable) {
        Intent intent = new Intent(context, ErrorReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_ERROR_THROWABLE, throwable);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Throwable throwable = (Throwable) getIntent().getSerializableExtra(EXTRA_ERROR_THROWABLE);

        StringWriter errorWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errorWriter));
        final String throwableText = errorWriter.toString();

        TextView errorText = (TextView) findViewById(R.id.errorText);
        errorText.setText(throwableText);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"Krisztian@fetlife.com"});
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "FetLife App Exception");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, throwableText);
                startActivity(Intent.createChooser(sharingIntent,"Share FetLife App Exception"));
            }
        });
    }

}
