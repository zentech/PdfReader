package org.techgeorge.pdfreader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private TextView outputTextView;
    private static final int READ_REQUEST_CODE = 42;
    private static final String PRIMARY = "primary";
    private static final String LOCAL_STORAGE = "/storage/self/primary/";
    private static final String EXT_STORAGE = "/storage/7764-A034/";
    private static final String COLON = ":";

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        outputTextView = findViewById(R.id.output_text);
        outputTextView.setMovementMethod(new ScrollingMovementMethod());

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

        /* getting user permission for external storage */
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if(resultData != null) {
                Uri uri = resultData.getData();
//                Toast.makeText(this, uri.getPath(), Toast.LENGTH_SHORT).show();
                Log.v("URI", uri.getPath());
                readPdfFile(uri);
            }
        }
    }

    /**
     * open pdf file and convert to text and start text 2 speech
     * @param uri
     */
    public void readPdfFile(Uri uri) {
        String fullPath;
        //convert from uri to full path
        if(uri.getPath().contains(PRIMARY)) {
            fullPath = LOCAL_STORAGE + uri.getPath().split(COLON)[1];
        }
        else {
            fullPath = EXT_STORAGE + uri.getPath().split(COLON)[1];
        }
        Log.v("URI", uri.getPath()+" "+fullPath);
        String stringParser;
        try {
            PdfReader pdfReader = new PdfReader(fullPath);
            stringParser = PdfTextExtractor.getTextFromPage(pdfReader, 1).trim();
            pdfReader.close();
            outputTextView.setText(stringParser);
            textToSpeech.speak(stringParser, TextToSpeech.QUEUE_FLUSH,null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
