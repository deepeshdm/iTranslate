package com.deepesh.itranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Spinner language1_spinner, language2_spinner;
    EditText input_editText;
    TextView result_TextView, progressBarTextView_main,detected_language_textView;
    Button translate_button;
    ImageButton shuffle_imgButton;
    ImageView camera_imageView, copy_imageView;
    ProgressBar progressBar_main;
    // currently selected language names
    static String LANGUAGE1 = "English";
    static String LANGUAGE2 = "English";
    // tracks if models are already downloaded on device
    static Boolean IS_LANGUAGE1_DOWNLOADED = null;
    static Boolean IS_LANGUAGE2_DOWNLOADED = null;
    // represents Language Tags of currently selected languages
    static String L1_TAG = null;
    static String L2_TAG = null;
    static HashMap<String, String> Language_Tags;
    static HashMap<String, String> LanguageTag2Name;
    final static String TAG = "TAG";
    static RemoteModelManager modelManager;
    static Boolean DETECT_LANGUAGE;
    Toolbar toolbar_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-----------------------------------------------------------------------------

        set_language_tags();
        set_language_names();

        input_editText = findViewById(R.id.userText_editText);
        result_TextView = findViewById(R.id.result_textView);
        // make textview scrollable to display large texts
        result_TextView.setMovementMethod(new ScrollingMovementMethod());
        detected_language_textView=findViewById(R.id.detected_language_textView);
        translate_button = findViewById(R.id.translate_button);
        shuffle_imgButton = findViewById(R.id.swap_imageButton);
        language1_spinner = findViewById(R.id.language1_spinner);
        language2_spinner = findViewById(R.id.language2_spinner);
        modelManager = RemoteModelManager.getInstance();
        progressBar_main = findViewById(R.id.progressbar_main);
        progressBarTextView_main = findViewById(R.id.progressbarTextview_main);
        copy_imageView = findViewById(R.id.copy_imageview);

        toolbar_main = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);

        // sets icon on toolbar
        Objects.requireNonNull(getSupportActionBar()).setIcon(R.mipmap.ic_launcher);


        //-----------------------------------------------------------------------------

        // set drop down menus
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.languages_supported1, R.layout.color_spinner_layout);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language1_spinner.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.languages_supported2,R.layout.color_spinner_layout);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language2_spinner.setAdapter(adapter2);


        // triggers when translate button clicked
        translate_button.setOnClickListener(v -> {
            translate_text();
        });

        //-----------------------------------------------------------------------------

        // triggers everytime input text changes & Auto-Detect=True

        input_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

                if (DETECT_LANGUAGE){

                    String user_text = input_editText.getText().toString();

                    LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
                    languageIdentifier.identifyLanguage(user_text)

                            .addOnSuccessListener(languageCode -> {

                                if (languageCode.equals("und")) {

                                    Log.i(TAG, "Can't identify language.");
                                    detected_language_textView.setText("Language Detected : Unknown");


                                } else {

                                    //-----------------------------------------------------------

                                    // check if the detected language is supported by translation api
                                    if (LanguageTag2Name.containsKey(languageCode)){

                                        Log.i(TAG, "Language: " + languageCode);
                                        String languageName = LanguageTag2Name.get(languageCode);
                                        detected_language_textView.setText("Language Detected : " + languageName );

                                        //--------------------------------------------------------

                                        LANGUAGE1 = languageName;
                                        String lang1_tag = Language_Tags.get(LANGUAGE1);
                                        L1_TAG = TranslateLanguage.fromLanguageTag(lang1_tag);

                                        // checks if selected Language1 model is present on device
                                        TranslateRemoteModel model1 = new TranslateRemoteModel.Builder(L1_TAG).build();
                                        modelManager.isModelDownloaded(model1)
                                                .addOnSuccessListener(aBoolean -> {
                                                    IS_LANGUAGE1_DOWNLOADED = aBoolean;
                                                    Log.d(TAG, " IS_LANGUAGE1_DOWNLOADED : " + IS_LANGUAGE1_DOWNLOADED);
                                                    Log.d(TAG, " LANGUAGE1 DOWNLOADED ON DEVICE : " + aBoolean);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.d(TAG, "get_downloaded_models: FAILED ");
                                                    Log.d(TAG, "get_downloaded_models: " + e.getMessage());
                                                });


                                    }else {
                                        Log.i(TAG, "Can't identify language.");
                                        detected_language_textView.setText("Language Detected : Unknown");
                                    }
                                }

                                //-----------------------------------------------------------

                            })

                            .addOnFailureListener(e -> {
                                Log.d(TAG, "afterTextChanged : " + e.getMessage());
                            });
                }
            }
        });


        //-----------------------------------------------------------------------------

        // triggers when drop down item is selected from spinner1
        AdapterView.OnItemSelectedListener spinner1_listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                LANGUAGE1 = parent.getItemAtPosition(position).toString();

                if (LANGUAGE1.equals("Auto-Detect")) {

                    // when Auto-Detect is selected
                    DETECT_LANGUAGE = true;

                } else {

                    // turn off Auto-Detect if its On.
                    DETECT_LANGUAGE = false;
                    detected_language_textView.setText(" ");

                    // get language tag of selected language
                    String lang1_tag = Language_Tags.get(LANGUAGE1);
                    L1_TAG = TranslateLanguage.fromLanguageTag(lang1_tag);

                    // checks if selected Language1 model is present on device
                    TranslateRemoteModel model1 = new TranslateRemoteModel.Builder(L1_TAG).build();
                    modelManager.isModelDownloaded(model1)
                            .addOnSuccessListener(aBoolean -> {
                                IS_LANGUAGE1_DOWNLOADED = aBoolean;
                                Log.d(TAG, " IS_LANGUAGE1_DOWNLOADED : " + IS_LANGUAGE1_DOWNLOADED);
                                Log.d(TAG, " LANGUAGE1 DOWNLOADED ON DEVICE : " + aBoolean);
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, "get_downloaded_models: FAILED ");
                                Log.d(TAG, "get_downloaded_models: " + e.getMessage());
                            });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        // triggers when drop down item is selected from spinner2
        AdapterView.OnItemSelectedListener spinner2_listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                LANGUAGE2 = parent.getItemAtPosition(position).toString();

                // get language tag of selected language
                String lang2_tag = Language_Tags.get(LANGUAGE2);
                L2_TAG = TranslateLanguage.fromLanguageTag(lang2_tag);


                // checks if selected Language2 model is present on device
                TranslateRemoteModel model2 = new TranslateRemoteModel.Builder(L2_TAG).build();
                modelManager.isModelDownloaded(model2)
                        .addOnSuccessListener(aBoolean -> {
                            IS_LANGUAGE2_DOWNLOADED = aBoolean;
                            Log.d(TAG, " IS_LANGUAGE2_DOWNLOADED : " + IS_LANGUAGE2_DOWNLOADED);
                            Log.d(TAG, " LANGUAGE2 DOWNLOADED ON DEVICE : " + aBoolean);
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "get_downloaded_models: FAILED ");
                            Log.d(TAG, "get_downloaded_models: " + e.getMessage());
                        });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        language1_spinner.setOnItemSelectedListener(spinner1_listener);
        language2_spinner.setOnItemSelectedListener(spinner2_listener);


    }


    //=============================================================================


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.supported_lang){

            Intent intent = new Intent(this,Supported_Languages.class);
            startActivity(intent);

        } else if (item.getItemId()==R.id.license){

            Intent intent = new Intent(this,License_Activity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    //=============================================================================


    public void translate_text() {

        String user_text = input_editText.getText().toString();

        if (user_text.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Text", Toast.LENGTH_SHORT).show();
            return;
        }

        //-----------------------------------------------------------------------------

        // Incase Auto-Detect is On & its unable to detect the language

        if (DETECT_LANGUAGE & LANGUAGE1.equals("Auto-Detect")){
            detected_language_textView.setText("Unable to Detect Language,Select from the Menu");
            return;
        }


        //-----------------------------------------------------------------------------

        // Create translator:
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(L1_TAG)
                .setTargetLanguage(L2_TAG)
                .build();
        final Translator translator = Translation.getClient(options);


        //-----------------------------------------------------------------------------

        // download the language models if not present on device


        // if both models are present on device
        if (IS_LANGUAGE1_DOWNLOADED & IS_LANGUAGE2_DOWNLOADED) {
            Log.d(TAG, " BOTH MODELS ARE PRESENT ON DEVICE ");

            // translates the text & displays it
            translator.translate(user_text)
                    .addOnSuccessListener(s -> {
                        Log.d(TAG, "translate_string : TRANSLATED TEXT : " + s);
                        result_TextView.setText(s);
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "translate_string: " + e.getMessage());
                    });
        }


        // if both models are not present on device
        if (!IS_LANGUAGE1_DOWNLOADED & !IS_LANGUAGE2_DOWNLOADED) {

            Log.d(TAG, " BOTH MODELS ARE NOT PRESENT ON DEVICE : " + LANGUAGE1 + " & " + LANGUAGE2);
            DownloadLanguageModel_Dialog(null, translator);

        }


        if (!IS_LANGUAGE1_DOWNLOADED & IS_LANGUAGE2_DOWNLOADED || !IS_LANGUAGE2_DOWNLOADED & IS_LANGUAGE1_DOWNLOADED) {

            String not_downloaded_model = (IS_LANGUAGE1_DOWNLOADED) ? LANGUAGE2 : LANGUAGE1;
            Log.d(TAG, " ONLY ONE MODEL IS NOT PRESENT : " + not_downloaded_model);
            DownloadLanguageModel_Dialog(not_downloaded_model, translator);

        }


    }


    //=============================================================================


    // triggers if any selected language model is not downloaded on device
    public void DownloadLanguageModel_Dialog(String undowloaded_model, Translator translator) {

        String models_to_download = null;
        if (undowloaded_model == null) {
            models_to_download = LANGUAGE1 + "," + LANGUAGE2;
        } else {
            models_to_download = undowloaded_model;
        }


        String msg = "The selected language models (" + models_to_download.toUpperCase() + ") are not downloaded." +
                "Once downloaded you can translate the language even when offline." +
                "Download size is 30-50 MB and will take 2-3 mins depending on your Internet connection.";

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle("iTranslate")
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false);

        builder.setPositiveButton("Download Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                progressBar_main.setVisibility(View.VISIBLE);
                progressBarTextView_main.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Downloading now...", Toast.LENGTH_SHORT).show();

                // downloads the selected language models if not present
                DownloadConditions conditions = new DownloadConditions.Builder().build();
                translator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener(unused -> {

                            //--------------------------------------------------

                            progressBar_main.setVisibility(View.INVISIBLE);
                            progressBarTextView_main.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "onClick: DOWNLOAD SUCESSFUL !");
                            Toast.makeText(getApplicationContext(),
                                    "Downloading Completed !", Toast.LENGTH_SHORT).show();

                            //--------------------------------------------------

                            // get language tag of selected language
                            String lang1_tag = Language_Tags.get(LANGUAGE1);
                            L1_TAG = TranslateLanguage.fromLanguageTag(lang1_tag);

                            // get language tag of selected language
                            String lang2_tag = Language_Tags.get(LANGUAGE2);
                            L2_TAG = TranslateLanguage.fromLanguageTag(lang2_tag);

                            IS_LANGUAGE1_DOWNLOADED = true;
                            IS_LANGUAGE2_DOWNLOADED = true;

                            // if both models are downloaded sucessfully the translate text
                            translate_text();


                            //--------------------------------------------------

                        })
                        .addOnFailureListener(e -> {
                            progressBar_main.setVisibility(View.INVISIBLE);
                            progressBarTextView_main.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "onClick : " + e.getMessage());
                            Toast.makeText(getApplicationContext(),
                                    "Downloading Failed ! Try again", Toast.LENGTH_SHORT).show();
                        });

            }
        });


        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //remove the Dialog box form screen.
                builder.create().dismiss();
            }
        });

        builder.show();
    }


    //=============================================================================

    private void set_language_tags() {

        Language_Tags = new HashMap<>();

        // total 59 languages supported
        Language_Tags.put("Afrikaans", "af");
        Language_Tags.put("Arabic", "ar");
        Language_Tags.put("Belarusian", "be");
        Language_Tags.put("Bulgarian", "bg");
        Language_Tags.put("Bengali", "bn");
        Language_Tags.put("Catalan", "ca");
        Language_Tags.put("Czech", "cs");
        Language_Tags.put("Welsh", "cy");
        Language_Tags.put("Danish", "da");
        Language_Tags.put("German", "de");
        Language_Tags.put("Greek", "el");
        Language_Tags.put("English", "en");
        Language_Tags.put("Esperanto", "eo");
        Language_Tags.put("Spanish", "es");
        Language_Tags.put("Estonian", "et");
        Language_Tags.put("Persian", "fa");
        Language_Tags.put("Finnish", "fi");
        Language_Tags.put("French", "fr");
        Language_Tags.put("Irish", "ga");
        Language_Tags.put("Galician", "gl");
        Language_Tags.put("Gujarati", "gu");
        Language_Tags.put("Hebrew", "he");
        Language_Tags.put("Croatian", "hr");
        Language_Tags.put("Haitian", "ht");
        Language_Tags.put("Hungarian", "hu");
        Language_Tags.put("Indonesian", "id");
        Language_Tags.put("Icelandic", "is");
        Language_Tags.put("Italian", "it");
        Language_Tags.put("Japanese", "ja");
        Language_Tags.put("Georgian", "ka");
        Language_Tags.put("Kannada", "kn");
        Language_Tags.put("Korean", "ko");
        Language_Tags.put("Lithuanian", "lt");
        Language_Tags.put("Hindi", "hi");
        Language_Tags.put("Latvian", "lv");
        Language_Tags.put("Macedonian", "mk");
        Language_Tags.put("Marathi", "mr");
        Language_Tags.put("Malay", "ms");
        Language_Tags.put("Maltese", "mt");
        Language_Tags.put("Dutch", "nl");
        Language_Tags.put("Norwegian", "no");
        Language_Tags.put("Polish", "pl");
        Language_Tags.put("Portuguese", "pt");
        Language_Tags.put("Romanian", "ro");
        Language_Tags.put("Russian", "ru");
        Language_Tags.put("Slovak", "sk");
        Language_Tags.put("Slovenian", "sl");
        Language_Tags.put("Albanian", "sq");
        Language_Tags.put("Swedish", "sv");
        Language_Tags.put("Swahili", "sw");
        Language_Tags.put("Tamil", "ta");
        Language_Tags.put("Telugu", "te");
        Language_Tags.put("Thai", "th");
        Language_Tags.put("Tagalog", "tl");
        Language_Tags.put("Turkish", "tr");
        Language_Tags.put("Ukrainian", "uk");
        Language_Tags.put("Urdu", "ur");
        Language_Tags.put("Vietnamese", "vi");
        Language_Tags.put("Chinese", "zh");

    }

    private void set_language_names() {

        LanguageTag2Name = new HashMap<>();

        // total 59 languages supported
        LanguageTag2Name.put("af", "Afrikaans");
        LanguageTag2Name.put("ar", "Arabic");
        LanguageTag2Name.put("be", "Belarusian");
        LanguageTag2Name.put("bg", "Bulgarian");
        LanguageTag2Name.put("bn", "Bengali");
        LanguageTag2Name.put("ca", "Catalan");
        LanguageTag2Name.put("cs", "Czech");
        LanguageTag2Name.put("cy", "Welsh");
        LanguageTag2Name.put("da", "Danish");
        LanguageTag2Name.put("de", "German");
        LanguageTag2Name.put("el", "Greek");
        LanguageTag2Name.put("en", "English");
        LanguageTag2Name.put("eo", "Esperanto");
        LanguageTag2Name.put("es", "Spanish");
        LanguageTag2Name.put("et", "Estonian");
        LanguageTag2Name.put("fa", "Persian");
        LanguageTag2Name.put("fi", "Finnish");
        LanguageTag2Name.put("fr", "French");
        LanguageTag2Name.put("ga", "Irish");
        LanguageTag2Name.put("gl", "Galician");
        LanguageTag2Name.put("gu", "Gujarati");
        LanguageTag2Name.put("he", "Hebrew");
        LanguageTag2Name.put("hi", "Hindi");
        LanguageTag2Name.put("hr", "Croatian");
        LanguageTag2Name.put("ht", "Haitian");
        LanguageTag2Name.put("hu", "Hungarian");
        LanguageTag2Name.put("id", "Indonesian");
        LanguageTag2Name.put("is", "Icelandic");
        LanguageTag2Name.put("it", "Italian");
        LanguageTag2Name.put("ja", "Japanese");
        LanguageTag2Name.put("ka", "Georgian");
        LanguageTag2Name.put("kn", "Kannada");
        LanguageTag2Name.put("ko", "Korean");
        LanguageTag2Name.put("lt", "Lithuanian");
        LanguageTag2Name.put("lv", "Latvian");
        LanguageTag2Name.put("mk", "Macedonian");
        LanguageTag2Name.put("mr", "Marathi");
        LanguageTag2Name.put("ms", "Malay");
        LanguageTag2Name.put("mt", "Maltese");
        LanguageTag2Name.put("nl", "Dutch");
        LanguageTag2Name.put("no", "Norwegian");
        LanguageTag2Name.put("pl", "Polish");
        LanguageTag2Name.put("pt", "Portuguese");
        LanguageTag2Name.put("ro", "Romanian");
        LanguageTag2Name.put("ru", "Russian");
        LanguageTag2Name.put("sk", "Slovak");
        LanguageTag2Name.put("sl", "Slovenian");
        LanguageTag2Name.put("sq", "Albanian");
        LanguageTag2Name.put("sv", "Swedish");
        LanguageTag2Name.put("sw", "Swahili");
        LanguageTag2Name.put("ta", "Tamil");
        LanguageTag2Name.put("te", "Telugu");
        LanguageTag2Name.put("th", "Thai");
        LanguageTag2Name.put("tl", "Tagalog");
        LanguageTag2Name.put("tr", "Turkish");
        LanguageTag2Name.put("uk", "Ukrainian");
        LanguageTag2Name.put("ur", "Urdu");
        LanguageTag2Name.put("vi", "Vietnamese");
        LanguageTag2Name.put("zh", "Chinese");

    }

    //=============================================================================

    // triggers when clicked on copy imageview
    public void CopyResult2Clipboard(View view) {
        String text = result_TextView.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("translation-result", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Translation Result Copied !", Toast.LENGTH_SHORT).show();
    }


}
























