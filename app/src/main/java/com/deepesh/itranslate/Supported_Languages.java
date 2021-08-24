package com.deepesh.itranslate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.Objects;

public class Supported_Languages extends AppCompatActivity {

    Toolbar toolbar;
    TextView title_textView,display_textView;
    String supported_languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supported_languages);

        toolbar = findViewById(R.id.supportedL_toolbar);
        setSupportActionBar(toolbar);

        title_textView = findViewById(R.id.textView_supportedL);
        display_textView = findViewById(R.id.Display_supportedLang_textview);
        display_textView.setMovementMethod(new ScrollingMovementMethod());

        // setup back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        supported_languages =
                "                Afrikaans \n" +
                        "                Arabic\n" +
                        "                Belarusian\n" +
                        "                Bulgarian\n" +
                        "                Bengali\n" +
                        "                Catalan\n" +
                        "                Czech\n" +
                        "                Welsh\n" +
                        "                Danish\n" +
                        "                German\n" +
                        "                Greek\n" +
                        "                English\n" +
                        "                Esperanto\n" +
                        "                Spanish\n" +
                        "                Estonian\n" +
                        "                Persian\n" +
                        "                Finnish\n" +
                        "                French\n" +
                        "                Irish\n" +
                        "                Galician\n" +
                        "                Gujarati\n" +
                        "                Hebrew\n" +
                        "                Hindi\n" +
                        "                Croatian\n" +
                        "                Haitian\n" +
                        "                Hungarian\n" +
                        "                Indonesian\n" +
                        "                Icelandic\n" +
                        "                Italian\n" +
                        "                Japanese\n" +
                        "                Georgian\n" +
                        "                Kannada\n" +
                        "                Korean\n" +
                        "                Lithuanian\n" +
                        "                Latvian\n" +
                        "                Macedonian\n" +
                        "                Marathi\n" +
                        "                Malay\n" +
                        "                Maltese\n" +
                        "                Dutch\n" +
                        "                Norwegian\n" +
                        "                Polish\n" +
                        "                Portuguese\n" +
                        "                Romanian\n" +
                        "                Russian\n" +
                        "                Slovak\n" +
                        "                Slovenian\n" +
                        "                Albanian\n" +
                        "                Swedish\n" +
                        "                Swahili\n" +
                        "                Tamil\n" +
                        "                Telugu\n" +
                        "                Thai\n" +
                        "                Tagalog\n" +
                        "                Turkish\n" +
                        "                Ukrainian\n" +
                        "                Urdu\n" +
                        "                Vietnamese\n" +
                        "                Chinese";

        display_textView.setText(supported_languages);

    }

}













